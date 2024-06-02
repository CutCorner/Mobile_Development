package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryStorage
import com.example.cepstun.data.local.UserDatabase
import com.example.cepstun.data.responseFirebase.DatabaseUpdateResult
import com.example.cepstun.data.responseFirebase.StorageResult
import com.example.cepstun.ui.activity.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class PersonalDataViewModel (
    private val auth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    private val storage: RepositoryStorage,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _database = MutableLiveData<UserDatabase?>()
    val database: LiveData<UserDatabase?>
        get() = _database

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getDatabase()
    }

    private fun getDatabase() {
        viewModelScope.launch {
            val user = auth.currentUser
            val uid = user?.uid
            val dbRef = repositoryDatabase.getDatabase()
            val userRef = dbRef.child("users").child(uid!!)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userDatabase = dataSnapshot.getValue(UserDatabase::class.java)
                    _database.value = userDatabase
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    _message.value = databaseError.message
                }
            })
        }
    }

//    fun updateDatabase(name:String?, phone:String?, date:String?, gender:String?, photo: File?){
//        viewModelScope.launch {
//            val uid = auth.currentUser!!.uid
//            if (photo != null){
//                val uri = storage.uploadFile(photo, uid).await().toString()
//                repositoryDatabase.updateDatabase(name, phone, date, gender, uri)
//            } else {
//                repositoryDatabase.updateDatabase(name, phone, date, gender, null)
//            }
//        }
//    }

    fun updateDatabase(name:String?, phone:String?, date:String?, gender:String?, photo: File?){
        viewModelScope.launch {
            _isLoading.value = true
            val uid = auth.currentUser!!.uid
            if (photo != null){
                when (val result = storage.uploadFile(photo, uid)) {
                    is StorageResult.Success -> {
                        when (val dbResult = repositoryDatabase.updateDatabase(name, phone, date, gender, result.uri.toString())) {
                            is DatabaseUpdateResult.Success -> {
                                _message.value = "Successfully update profile data"
                            }
                            is DatabaseUpdateResult.Error -> {
                                _message.value = "Failed to update database: ${dbResult.exception.message}"
                            }
                        }
                    }
                    is StorageResult.Error -> {
                        _message.value = "Failed to upload file: ${result.exception.message}"
                    }
                }
            } else {
                when (val dbResult = repositoryDatabase.updateDatabase(name, phone, date, gender, null)) {
                    is DatabaseUpdateResult.Success -> {
                        _message.value = "Successfully update profile data"
                    }
                    is DatabaseUpdateResult.Error -> {
                        _message.value = "Failed to update database: ${dbResult.exception.message}"
                    }
                }
            }
            _isLoading.value = false
        }
    }
}