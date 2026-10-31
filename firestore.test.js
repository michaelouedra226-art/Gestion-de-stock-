const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
const DATABASE_ID = "ai-studio-android-stockflo-a2ff8746-06e4-4d98-9eb4-29cf16be98cf";
let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      host: "127.0.0.1",
      port: 8085,
      rules: fs.readFileSync("firestore.rules", "utf8"),
      databaseId: DATABASE_ID,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write user profiles", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore(DATABASE_ID);
  await assertFails(unauthedDb.collection("users").doc("user_1").get());
  await assertFails(unauthedDb.collection("users").doc("user_1").set({
    uid: "user_1",
    email: "user1@example.com",
    createdAt: new Date(),
  }));
});

test("Authenticated user can create their own user profile", async () => {
  const user1Db = testEnv.authenticatedContext("user_1").firestore(DATABASE_ID);
  await assertSucceeds(user1Db.collection("users").doc("user_1").set({
    uid: "user_1",
    email: "user1@domain.com",
    displayName: "Admin One",
    companyId: "comp_1",
    role: "owner",
    createdAt: new Date(),
  }));
});

test("User cannot create or edit someone else's profile", async () => {
  const user1Db = testEnv.authenticatedContext("user_1").firestore(DATABASE_ID);
  await assertFails(user1Db.collection("users").doc("user_2").set({
    uid: "user_2",
    email: "user2@domain.com",
    createdAt: new Date(),
  }));
});

test("User can create a company with ownerId matching uid", async () => {
  const user1Db = testEnv.authenticatedContext("user_1").firestore(DATABASE_ID);
  await assertSucceeds(user1Db.collection("companies").doc("comp_1").set({
    name: "Alpha Logistics",
    brandColor: "#5EEAD4",
    currency: "EUR",
    ownerId: "user_1",
    defaultThreshold: 5,
    createdAt: new Date(),
  }));
});

test("Cross-company user cannot read or write products in another company", async () => {
  // Setup user_1 and comp_1
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminDb = context.firestore(DATABASE_ID);
    await adminDb.collection("users").doc("user_1").set({
      uid: "user_1",
      email: "u1@test.com",
      companyId: "comp_1",
      role: "owner",
      createdAt: new Date(),
    });
    await adminDb.collection("users").doc("user_2").set({
      uid: "user_2",
      email: "u2@test.com",
      companyId: "comp_2",
      role: "owner",
      createdAt: new Date(),
    });
    await adminDb.collection("companies").doc("comp_1").set({
      name: "Alpha Corp",
      brandColor: "#5EEAD4",
      currency: "EUR",
      ownerId: "user_1",
      createdAt: new Date(),
    });
    await adminDb.collection("companies").doc("comp_1").collection("products").doc("prod_1").set({
      companyId: "comp_1",
      sku: "SKU-001",
      name: "Widget Pro",
      stock: 10,
      threshold: 3,
      createdAt: new Date(),
    });
  });

  const user1Db = testEnv.authenticatedContext("user_1").firestore(DATABASE_ID);
  const user2Db = testEnv.authenticatedContext("user_2").firestore(DATABASE_ID);

  // user_1 can read their company's product
  await assertSucceeds(user1Db.collection("companies").doc("comp_1").collection("products").doc("prod_1").get());

  // user_2 cannot read user_1 company's product
  await assertFails(user2Db.collection("companies").doc("comp_1").collection("products").doc("prod_1").get());

  // user_2 cannot create a product in user_1's company
  await assertFails(user2Db.collection("companies").doc("comp_1").collection("products").doc("prod_2").set({
    companyId: "comp_1",
    sku: "SKU-002",
    name: "Hacked Item",
    stock: 5,
    threshold: 2,
    createdAt: new Date(),
  }));
});
