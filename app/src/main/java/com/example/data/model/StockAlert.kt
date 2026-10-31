package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class StockAlert(
    @DocumentId val id: String = "",
    val companyId: String = "",
    val productId: String = "",
    val productName: String = "",
    val currentStock: Long = 0L,
    val threshold: Long = 0L,
    val resolved: Boolean = false,
    val triggeredAt: Timestamp? = null,
    val resolvedAt: Timestamp? = null
)
