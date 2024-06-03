package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.local.UserDatabase
import com.example.cepstun.ui.activity.LoginActivity
import com.example.cepstun.ui.activity.OnBoardingActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class MenuProfileViewModel(
    private val auth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _database = MutableLiveData<UserDatabase?>()
    val database: LiveData<UserDatabase?>
        get() = _database

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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
                    _errorMessage.value = databaseError.message
                }
            })
        }
    }

    fun signOut() {
        auth.logout()
        Intent(context, LoginActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

}