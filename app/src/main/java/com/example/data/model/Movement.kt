package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Movement(
    @DocumentId val id: String = "",
    val companyId: String = "",
    val productId: String = "",
    val productName: String = "",
    val type: String = "in", // "in" or "out"
    val quantity: Long = 1L,
    val reason: String = "",
    val date: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    val isIn: Boolean get() = type == "in"
    val isOut: Boolean get() = type == "out"
}
