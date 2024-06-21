package com.example.cepstun.data

import com.example.cepstun.data.local.UserDatabase
import com.example.cepstun.data.responseFirebase.DatabaseUpdateResult
import com.facebook.AccessToken
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RepositoryDatabase private constructor(private val database: FirebaseDatabase) {

    suspend fun checkUserExists(uid: String): Boolean {
        val task = database.getReference("users").child(uid).get().await()
        return task.exists()
    }

    suspend fun uploadToDatabase(userType: String): Boolean {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val providerId = user.providerData[1].providerId
            val photoUrl = when (providerId) {
                "facebook.com" -> {
                    val accessToken = AccessToken.getCurrentAccessToken()
                    "${user.photoUrl}?access_token=${accessToken?.token}"
                }
                "google.com" -> {
                    user.photoUrl.toString()
                }
                else -> {
                    user.photoUrl.toString()
                }
            }

            val userMap = UserDatabase(
                fname = user.displayName.toString(),
                level = userType,
                photo = photoUrl,
                email = user.providerData[1].email,
                phone = user.providerData[1].phoneNumber
            )
            return try {
                database.getReference("users")
                    .child(user.uid)
                    .setValue(userMap)
                    .await()
                true
            } catch (e: Exception) {
                false
            }
        } else {
            return false
        }
    }

    fun getDatabase(): DatabaseReference {
        return database.reference
    }

    suspend fun updateDatabase(fname: String?, no: String?, date: String?, gender: String?, photo: String?): DatabaseUpdateResult {
        val user = Firebase.auth.currentUser
        return if (user != null) {
            val userMap = mutableMapOf<String, Any>()
            if (fname != null) userMap["fname"] = fname
            if (no != null) userMap["phone"] = no
            if (date != null) userMap["dateOfBirth"] = date
            if (gender != null) userMap["gender"] = gender
            if (photo != null) userMap["photo"] = photo
            try {
                database.getReference("users")
                    .child(user.uid)
                    .updateChildren(userMap)
                    .await()
                DatabaseUpdateResult.Success
            } catch (e: Exception) {
                DatabaseUpdateResult.Error(e)
            }
        } else {
            DatabaseUpdateResult.Error(Exception("User not logged in"))
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: RepositoryDatabase? = null

        fun clearInstance() {
            INSTANCE = null
        }

        fun getInstance(database: FirebaseDatabase): RepositoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryDatabase(database)
                INSTANCE = instance
                instance
            }
        }
    }
}