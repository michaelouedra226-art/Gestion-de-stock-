package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Category
import com.example.data.model.Company
import com.example.data.model.Movement
import com.example.data.model.Product
import com.example.data.model.StockAlert
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StockFlowRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val path = "users/$userId"
        val docRef = db.collection("users").document(userId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, path)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(UserProfile::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val path = "users/$uid"
        try {
            val payload = mutableMapOf<String, Any>(
                "uid" to uid,
                "email" to profile.email,
                "displayName" to profile.displayName,
                "companyId" to profile.companyId,
                "role" to profile.role,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (profile.createdAt == null) {
                payload["createdAt"] = FieldValue.serverTimestamp()
            }
            db.collection("users").document(uid).set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    fun observeCompany(companyId: String): Flow<Company?> = callbackFlow {
        if (companyId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val path = "companies/$companyId"
        val docRef = db.collection("companies").document(companyId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, path)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(Company::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun createCompanyAndAssign(
        companyName: String,
        brandColor: String,
        currency: String,
        defaultThreshold: Long,
        initialCategory: String?
    ): String {
        val uid = requireUserId()
        val userEmail = auth.currentUser?.email ?: ""
        val userDisplayName = auth.currentUser?.displayName ?: ""
        val companyId = "cmp_" + UUID.randomUUID().toString().replace("-", "").take(12)
        val compPath = "companies/$companyId"

        try {
            val companyPayload = hashMapOf<String, Any>(
                "name" to companyName.trim(),
                "slug" to companyName.trim().lowercase().replace(Regex("[^a-z0-9]"), "-"),
                "brandColor" to brandColor,
                "currency" to currency,
                "locale" to "fr-FR",
                "defaultThreshold" to defaultThreshold,
                "ownerId" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("companies").document(companyId).set(companyPayload).await()

            val userPayload = hashMapOf<String, Any>(
                "uid" to uid,
                "email" to userEmail,
                "displayName" to userDisplayName,
                "companyId" to companyId,
                "role" to "owner",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid).set(userPayload).await()

            if (!initialCategory.isNullOrBlank()) {
                val catId = "cat_" + UUID.randomUUID().toString().replace("-", "").take(8)
                val catPayload = hashMapOf<String, Any>(
                    "companyId" to companyId,
                    "name" to initialCategory.trim(),
                    "color" to brandColor,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                db.collection("companies").document(companyId)
                    .collection("categories").document(catId).set(catPayload).await()
            }

            return companyId
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, compPath)
            throw e
        }
    }

    suspend fun updateCompany(company: Company) {
        requireUserId()
        val path = "companies/${company.id}"
        try {
            val payload = mapOf<String, Any>(
                "name" to company.name.trim(),
                "brandColor" to company.brandColor,
                "currency" to company.currency,
                "defaultThreshold" to company.defaultThreshold,
                "locale" to company.locale,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("companies").document(company.id).update(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    fun observeProducts(companyId: String): Flow<List<Product>> = callbackFlow {
        if (companyId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val path = "companies/$companyId/products"
        val query = db.collection("companies").document(companyId)
            .collection("products")
            .whereEqualTo("companyId", companyId)
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, path)
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(Product::class.java) } ?: emptyList()
            trySend(list.filter { !it.archived })
        }
        awaitClose { registration.remove() }
    }

    suspend fun addProduct(
        companyId: String,
        sku: String,
        name: String,
        description: String,
        categoryId: String,
        initialStock: Long,
        threshold: Long,
        unitPrice: Double
    ): Product {
        requireUserId()
        val path = "companies/$companyId/products"
        val prodId = "prd_" + UUID.randomUUID().toString().replace("-", "").take(10)
        try {
            val productPayload = hashMapOf<String, Any>(
                "companyId" to companyId,
                "sku" to sku.trim().uppercase(),
                "name" to name.trim(),
                "description" to description.trim(),
                "categoryId" to categoryId,
                "stock" to initialStock,
                "threshold" to threshold,
                "unitPrice" to unitPrice,
                "archived" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val docRef = db.collection("companies").document(companyId)
                .collection("products").document(prodId)
            docRef.set(productPayload).await()

            // If initial stock is low, record alert
            if (initialStock <= threshold) {
                val alertId = "alt_" + prodId
                val alertPayload = hashMapOf<String, Any>(
                    "companyId" to companyId,
                    "productId" to prodId,
                    "productName" to name.trim(),
                    "currentStock" to initialStock,
                    "threshold" to threshold,
                    "resolved" to false,
                    "triggeredAt" to FieldValue.serverTimestamp()
                )
                db.collection("companies").document(companyId)
                    .collection("alerts").document(alertId).set(alertPayload).await()
            }

            // If initialStock > 0, log initial movement
            if (initialStock > 0) {
                val movId = "mov_" + UUID.randomUUID().toString().replace("-", "").take(10)
                val movPayload = hashMapOf<String, Any>(
                    "companyId" to companyId,
                    "productId" to prodId,
                    "productName" to name.trim(),
                    "type" to "in",
                    "quantity" to initialStock,
                    "reason" to "Stock initial",
                    "date" to Timestamp.now(),
                    "createdAt" to FieldValue.serverTimestamp()
                )
                db.collection("companies").document(companyId)
                    .collection("movements").document(movId).set(movPayload).await()
            }

            return Product(
                id = prodId,
                companyId = companyId,
                sku = sku.trim().uppercase(),
                name = name.trim(),
                description = description.trim(),
                categoryId = categoryId,
                stock = initialStock,
                threshold = threshold,
                unitPrice = unitPrice,
                archived = false
            )
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    suspend fun updateProduct(product: Product) {
        requireUserId()
        val path = "companies/${product.companyId}/products/${product.id}"
        try {
            val payload = mapOf<String, Any>(
                "sku" to product.sku.trim().uppercase(),
                "name" to product.name.trim(),
                "description" to product.description.trim(),
                "categoryId" to product.categoryId,
                "stock" to product.stock,
                "threshold" to product.threshold,
                "unitPrice" to product.unitPrice,
                "archived" to product.archived,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("companies").document(product.companyId)
                .collection("products").document(product.id).update(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun deleteProduct(companyId: String, productId: String) {
        requireUserId()
        val path = "companies/$companyId/products/$productId"
        try {
            // Soft delete to preserve historical audit data
            db.collection("companies").document(companyId)
                .collection("products").document(productId)
                .update(
                    mapOf(
                        "archived" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }

    suspend fun recordMovement(
        companyId: String,
        productId: String,
        type: String, // "in" or "out"
        quantity: Long,
        reason: String
    ) {
        requireUserId()
        require(quantity > 0) { "La quantité doit être supérieure à zéro." }
        val productRef = db.collection("companies").document(companyId)
            .collection("products").document(productId)
        val movementsColl = db.collection("companies").document(companyId)
            .collection("movements")
        val alertsColl = db.collection("companies").document(companyId)
            .collection("alerts")

        val path = "companies/$companyId/movements"
        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(productRef)
                if (!snapshot.exists()) {
                    throw IllegalStateException("Produit introuvable")
                }
                val currentStock = snapshot.getLong("stock") ?: 0L
                val threshold = snapshot.getLong("threshold") ?: 5L
                val productName = snapshot.getString("name") ?: ""

                val newStock = if (type == "in") {
                    currentStock + quantity
                } else {
                    if (currentStock < quantity) {
                        throw IllegalStateException("Stock insuffisant : $currentStock disponible(s), $quantity demandé(s).")
                    }
                    currentStock - quantity
                }

                // Update product stock
                transaction.update(
                    productRef,
                    mapOf(
                        "stock" to newStock,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )

                // Add movement document
                val movId = "mov_" + UUID.randomUUID().toString().replace("-", "").take(10)
                val movDocRef = movementsColl.document(movId)
                val movementData = hashMapOf<String, Any>(
                    "companyId" to companyId,
                    "productId" to productId,
                    "productName" to productName,
                    "type" to type,
                    "quantity" to quantity,
                    "reason" to reason.trim(),
                    "date" to Timestamp.now(),
                    "createdAt" to FieldValue.serverTimestamp()
                )
                transaction.set(movDocRef, movementData)

                // Alert synchronization
                val alertDocRef = alertsColl.document("alt_" + productId)
                if (newStock <= threshold) {
                    val alertData = hashMapOf<String, Any>(
                        "companyId" to companyId,
                        "productId" to productId,
                        "productName" to productName,
                        "currentStock" to newStock,
                        "threshold" to threshold,
                        "resolved" to false,
                        "triggeredAt" to FieldValue.serverTimestamp()
                    )
                    transaction.set(alertDocRef, alertData)
                } else {
                    val resolvedData = hashMapOf<String, Any>(
                        "companyId" to companyId,
                        "productId" to productId,
                        "productName" to productName,
                        "currentStock" to newStock,
                        "threshold" to threshold,
                        "resolved" to true,
                        "resolvedAt" to FieldValue.serverTimestamp()
                    )
                    transaction.set(alertDocRef, resolvedData)
                }
            }.await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    fun observeMovements(companyId: String): Flow<List<Movement>> = callbackFlow {
        if (companyId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val path = "companies/$companyId/movements"
        val query = db.collection("companies").document(companyId)
            .collection("movements")
            .whereEqualTo("companyId", companyId)
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, path)
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(Movement::class.java) } ?: emptyList()
            trySend(list.sortedByDescending { it.createdAt ?: it.date })
        }
        awaitClose { registration.remove() }
    }

    fun observeMovementsForProduct(companyId: String, productId: String): Flow<List<Movement>> = callbackFlow {
        if (companyId.isBlank() || productId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val path = "companies/$companyId/movements"
        val query = db.collection("companies").document(companyId)
            .collection("movements")
            .whereEqualTo("companyId", companyId)
            .whereEqualTo("productId", productId)
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, path)
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(Movement::class.java) } ?: emptyList()
            trySend(list.sortedByDescending { it.createdAt ?: it.date })
        }
        awaitClose { registration.remove() }
    }

    fun observeCategories(companyId: String): Flow<List<Category>> = callbackFlow {
        if (companyId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val path = "companies/$companyId/categories"
        val query = db.collection("companies").document(companyId)
            .collection("categories")
            .whereEqualTo("companyId", companyId)
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, path)
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(Category::class.java) } ?: emptyList()
            trySend(list.sortedBy { it.name })
        }
        awaitClose { registration.remove() }
    }

    suspend fun addCategory(companyId: String, name: String, color: String) {
        requireUserId()
        val path = "companies/$companyId/categories"
        val catId = "cat_" + UUID.randomUUID().toString().replace("-", "").take(8)
        try {
            val payload = hashMapOf<String, Any>(
                "companyId" to companyId,
                "name" to name.trim(),
                "color" to color,
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("companies").document(companyId)
                .collection("categories").document(catId).set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    suspend fun deleteCategory(companyId: String, categoryId: String) {
        requireUserId()
        val path = "companies/$companyId/categories/$categoryId"
        try {
            db.collection("companies").document(companyId)
                .collection("categories").document(categoryId).delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }

    fun observeAlerts(companyId: String): Flow<List<StockAlert>> = callbackFlow {
        if (companyId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val path = "companies/$companyId/alerts"
        val query = db.collection("companies").document(companyId)
            .collection("alerts")
            .whereEqualTo("companyId", companyId)
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, path)
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(StockAlert::class.java) } ?: emptyList()
            trySend(list.filter { !it.resolved })
        }
        awaitClose { registration.remove() }
    }

    suspend fun resolveAlert(companyId: String, alertId: String) {
        requireUserId()
        val path = "companies/$companyId/alerts/$alertId"
        try {
            db.collection("companies").document(companyId)
                .collection("alerts").document(alertId).update(
                    mapOf(
                        "resolved" to true,
                        "resolvedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }
}
