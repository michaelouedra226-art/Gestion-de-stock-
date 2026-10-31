package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val companyId: String = "",
    val role: String = "owner", // owner, admin, member
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

sealed interface UserProfileState {
    object Loading : UserProfileState
    data class Loaded(val profile: UserProfile?) : UserProfileState
    data class Error(val message: String) : UserProfileState
}
