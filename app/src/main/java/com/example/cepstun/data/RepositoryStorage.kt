package com.example.cepstun.data

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.cepstun.data.responseFirebase.StorageResult
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.File

class RepositoryStorage private constructor(private val storage: FirebaseStorage) {

//    fun uploadFile(file: File, uid: String): Task<UploadTask.TaskSnapshot> {
//        val storageRef = storage.reference
//        val fileRef = storageRef.child("users/$uid/photo/${file.name}")
//        return fileRef.putFile(file.toUri())
//    }

//    fun uploadFile(file: File, uid: String): Task<Uri> {
//        val storageRef = storage.reference
//        val fileRef = storageRef.child("users/$uid/photo/${file.name}")
//        return fileRef.putFile(file.toUri()).continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            fileRef.downloadUrl
//        }
//    }

//    fun uploadFile(file: File, uid: String): Task<Uri> {
//        val storageRef = storage.reference
//        val fileRef = storageRef.child("users/$uid/photo/${file.name}")
//
//        // Get reference to the 'photo' folder
//        val photoFolderRef = storageRef.child("users/$uid/photo")
//
//        // List all files in the 'photo' folder
//        photoFolderRef.listAll().addOnSuccessListener { listResult ->
//            // For each file in the folder, delete it
//            for (item in listResult.items) {
//                item.delete()
//            }
//        }
//
//        // Now upload the new file
//        return fileRef.putFile(file.toUri()).continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            fileRef.downloadUrl
//        }
//    }

    suspend fun uploadFile(file: File, uid: String): StorageResult {
        val storageRef = storage.reference
        val fileRef = storageRef.child("users/$uid/photo/${file.name}")

        // Get reference to the 'photo' folder
        val photoFolderRef = storageRef.child("users/$uid/photo")

        // List all files in the 'photo' folder
        photoFolderRef.listAll().addOnSuccessListener { listResult ->
            // For each file in the folder, delete it
            for (item in listResult.items) {
                item.delete()
            }
        }

        // Now upload the new file
        return try {
            val uri = fileRef.putFile(file.toUri()).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.await()
            StorageResult.Success(uri)
        } catch (e: Exception) {
            StorageResult.Error(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RepositoryStorage? = null

        fun getInstance(storage: FirebaseStorage): RepositoryStorage {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryStorage(storage)
                INSTANCE = instance
                instance
            }
        }
    }

}