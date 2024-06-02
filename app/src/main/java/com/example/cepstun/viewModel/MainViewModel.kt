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
import com.example.cepstun.ui.activity.ChooseUserActivity
import com.example.cepstun.ui.activity.OnBoardingActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class MainViewModel (
    private val auth: RepositoryAuth,
    private val database: RepositoryDatabase,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _levelUser = MutableLiveData<String?>()
    val levelUser: LiveData<String?> = _levelUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun cekUserLoginAndLevel() {
        _isLoading.value = true
        val user = auth.currentUser
        if (user == null) {
            _isLoading.value = false
            Intent(context, OnBoardingActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
        } else {
            viewModelScope.launch {
                if (!database.checkUserExists(user.uid)){
                    _isLoading.value = false
                    Intent(context, ChooseUserActivity::class.java).also { intent ->
                        intent.putExtra(ChooseUserActivity.ID_TOKEN, user.uid)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                } else {
                    val dbRef = database.getDatabase()
                    val userRef = dbRef.child("users").child(user.uid)

                    userRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val userDatabase = dataSnapshot.getValue(UserDatabase::class.java)
                            _levelUser.value = userDatabase?.level
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            _errorMessage.value = databaseError.message
                            _isLoading.value = false
                        }
                    })
                }
            }
        }

    }

}