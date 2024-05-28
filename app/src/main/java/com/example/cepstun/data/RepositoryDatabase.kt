package com.example.cepstun.data

import com.example.cepstun.data.local.UserDatabase
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RepositoryDatabase private constructor(private val database: FirebaseDatabase) {


    suspend fun checkUserExists(uid: String): Boolean {
        val task = database.getReference("users").child(uid).get().await()
        return task.exists()
    }

    suspend fun addUserToDatabase(
        uid: String,
        email: String,
        level: String,
        photoUrl: String?
    ): Boolean {
        val userMap = mapOf(
            "FName" to email,
            "Level" to level,
            "Photo" to photoUrl
        )
        return try {
            database.getReference("users")
                .child(uid)
                .setValue(userMap)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

//    suspend fun uploadToDatabase(userType: String): Boolean {
//        val userMap = mapOf(
//            "FName" to FirebaseAuth.getInstance().currentUser?.displayName.toString(),
//            "Level" to userType,
//            "Photo" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
//        )
//        return try {
//            database.getReference("users")
//                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
//                .setValue(userMap)
//                .await()
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }

    suspend fun uploadToDatabase(userType: String): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val providerId = user.providerData[1].providerId // Dapatkan penyedia identitas
            val photoUrl = when (providerId) {
                "facebook.com" -> {
                    val accessToken = AccessToken.getCurrentAccessToken() // Dapatkan access token
                    "${user.photoUrl}?access_token=${accessToken?.token}" // URL gambar profil Facebook dengan access token
                }
                "google.com" -> {
                    user.photoUrl.toString() // URL gambar profil dari Firebase Auth untuk Google
                }
                else -> {
                    user.photoUrl.toString() // Jika penyedia identitas lain, gunakan URL gambar profil dari Firebase Auth
                }
            }

            val userMap = UserDatabase(
                user.displayName.toString(),
                userType,
                photoUrl
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

    suspend fun getDatabase(): DatabaseReference {
        return database.reference
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