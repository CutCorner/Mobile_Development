package com.example.cepstun.data

import com.google.firebase.auth.FirebaseAuth
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

    suspend fun uploadToDatabase(userType: String): Boolean {
        val userMap = mapOf(
            "FName" to FirebaseAuth.getInstance().currentUser?.displayName.toString(),
            "Level" to userType,
            "Photo" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        )
        return try {
            database.getReference("users")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .setValue(userMap)
                .await()
            true
        } catch (e: Exception) {
            false
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