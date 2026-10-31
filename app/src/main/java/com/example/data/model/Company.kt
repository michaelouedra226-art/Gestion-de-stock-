package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Company(
    @DocumentId val id: String = "",
    val name: String = "",
    val slug: String = "",
    val brandColor: String = "#5EEAD4",
    val currency: String = "EUR",
    val locale: String = "fr-FR",
    val defaultThreshold: Long = 5L,
    val ownerId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
