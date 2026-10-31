package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Category(
    @DocumentId val id: String = "",
    val companyId: String = "",
    val name: String = "",
    val color: String = "#5EEAD4",
    @ServerTimestamp val createdAt: Timestamp? = null
)
