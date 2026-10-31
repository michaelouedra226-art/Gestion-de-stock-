package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class StockFlowRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createCompanyAndAssign_authenticatedUser_succeeds() = runBlocking {
        val userUid = signInTestUser("founder@stockflow.internal")
        val repository = StockFlowRepository(firestore, auth)

        val companyId = withTimeout(10000L) {
            repository.createCompanyAndAssign(
                companyName = "Apex Supply",
                brandColor = "#5EEAD4",
                currency = "EUR",
                defaultThreshold = 5L,
                initialCategory = "Outillage"
            )
        }

        assertTrue(companyId.isNotBlank())
        assertEquals(userUid, repository.requireUserId())
    }

    @Test
    fun addProductAndRecordMovement_authenticatedUser_updatesStock() = runBlocking {
        signInTestUser("ops@stockflow.internal")
        val repository = StockFlowRepository(firestore, auth)

        val companyId = withTimeout(10000L) {
            repository.createCompanyAndAssign(
                companyName = "Logix Pro",
                brandColor = "#A78BFA",
                currency = "EUR",
                defaultThreshold = 3L,
                initialCategory = null
            )
        }

        val product = withTimeout(10000L) {
            repository.addProduct(
                companyId = companyId,
                sku = "SKU-909",
                name = "Connecteur Industriel",
                description = "Acier trempé",
                categoryId = "",
                initialStock = 10L,
                threshold = 3L,
                unitPrice = 14.50
            )
        }

        assertNotNull(product.id)
        assertEquals("SKU-909", product.sku)

        // Record a stock exit (sortie)
        withTimeout(10000L) {
            repository.recordMovement(
                companyId = companyId,
                productId = product.id,
                type = "out",
                quantity = 4L,
                reason = "Commande client #8801"
            )
        }

        // Attempting an exit exceeding available stock must fail
        try {
            repository.recordMovement(
                companyId = companyId,
                productId = product.id,
                type = "out",
                quantity = 50L,
                reason = "Excédent impossible"
            )
            fail("Should have failed with stock insuffisant")
        } catch (e: Exception) {
            assertTrue(e.message?.contains("Stock insuffisant") == true)
        }
    }

    @Test
    fun unauthenticatedAccess_throwsIllegalStateException() = runBlocking {
        auth.signOut()
        val repository = StockFlowRepository(firestore, auth)
        try {
            repository.requireUserId()
            fail("Unauthenticated access should throw IllegalStateException")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("User must be signed in") == true)
        }
    }
}
