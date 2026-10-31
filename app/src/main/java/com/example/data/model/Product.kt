package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val companyId: String = "",
    val sku: String = "",
    val name: String = "",
    val description: String = "",
    val categoryId: String = "",
    val stock: Long = 0L,
    val threshold: Long = 5L,
    val unitPrice: Double = 0.0,
    val archived: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val isLowStock: Boolean get() = stock > 0L && stock <= threshold
    val isOutOfStock: Boolean get() = stock <= 0L
    val totalValue: Double get() = stock * unitPrice
}
