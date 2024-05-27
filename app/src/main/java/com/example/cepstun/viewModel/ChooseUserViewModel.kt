package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.ui.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class ChooseUserViewModel(
//    private val auth: FirebaseAuth,
//    private val database: FirebaseDatabase
    private val repositoryDatabase: RepositoryDatabase,
    application: Application
) : AndroidViewModel(application)  {

    private val _isUserSelected = MutableLiveData<Boolean>()
    val isUserSelected: MutableLiveData<Boolean> = _isUserSelected

    private val _showToast = MutableLiveData<String>()
    val showToast: MutableLiveData<String> = _showToast

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    init {
        _isUserSelected.value = false
    }

    fun selectedUser(){
        _isUserSelected.value = true
    }

    fun uploadToDatabase(userType: String) {
        viewModelScope.launch {
            if (repositoryDatabase.uploadToDatabase(userType)){
                Intent(context, MainActivity::class.java). also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(it)
                }
                _showToast.value = context.getString(R.string.saved)
            } else {
                _showToast.value = context.getString(R.string.fail_connect)
            }
        }


//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val user = task.result?.user
//                if (user != null) {
//                    database.getReference("users").child(user.uid).get()
//                        .addOnCompleteListener { task2 ->
//                            if (task2.isSuccessful) {
//                                val userMap = mapOf(
//                                    "FName" to user.displayName.toString(),
//                                    "Level" to userType,
//                                    "Photo" to user.photoUrl.toString()
//                                )
//                                database.getReference("users").child(user.uid).setValue(userMap)
//                                    .addOnSuccessListener {
//                                        Intent(context, MainActivity::class.java). also {
//                                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                            context.startActivity(it)
//                                        }
//                                        Toast.makeText(context,
//                                            context.getString(R.string.saved), Toast.LENGTH_LONG).show()
//                                    }.addOnFailureListener { e ->
//                                        Toast.makeText(context, e.printStackTrace().toString(), Toast.LENGTH_LONG).show()
//                                    }
//                            } else {
//                                Toast.makeText(context,
//                                    context.getString(R.string.fail_save), Toast.LENGTH_LONG).show()
//                            }
//                        }
//                }
//            } else {
//                Toast.makeText(context, context.getString(R.string.fail_connect), Toast.LENGTH_LONG).show()
//            }
//        }
    }

}