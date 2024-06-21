package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.QueueManager
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryFavorite
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.RepositoryNotification
import com.example.cepstun.data.RepositorySharedPreference
import com.example.cepstun.data.local.UserDatabase
import com.example.cepstun.data.remote.response.barber.ResultData
import com.example.cepstun.ui.activity.LoginActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuProfileCustomerViewModel(
    private val auth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    private val history: RepositoryHistory,
    private val favorite: RepositoryFavorite,
    private val preference: RepositorySharedPreference,
    private val notification: RepositoryNotification,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _database = MutableLiveData<UserDatabase?>()
    val database: LiveData<UserDatabase?>
        get() = _database

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _barberData = MutableLiveData<ResultData>()
    val barberData: LiveData<ResultData> = _barberData

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

    fun deleteAllCusHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            history.deleteCusHistory()
        }
    }

    fun deleteAllFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            favorite.deleteFavoriteAll()
        }
    }

    fun deleteAllNotification(){
        viewModelScope.launch(Dispatchers.IO) {
            notification.deleteNotificationCustomer()
        }
    }

    fun deleteUserLevel(){
        viewModelScope.launch(Dispatchers.IO) {
            preference.clearUserLevel()
        }
    }

    fun signOut() {
        auth.logout()
        Intent(context, LoginActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    fun getQueue() = QueueManager.getQueue(context)

}