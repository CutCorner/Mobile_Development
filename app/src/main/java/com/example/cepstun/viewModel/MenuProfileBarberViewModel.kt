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

class MenuProfileBarberViewModel (
    private val auth: RepositoryAuth,
    private val barberApi: RepositoryBarberApi,
    private val history: RepositoryHistory,
    private val preference: RepositorySharedPreference,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _barberData = MutableLiveData<ResultData>()
    val barberData: LiveData<ResultData> = _barberData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getBarberData() {
        _isLoading.value = true
        val barber = auth.currentUser
        if (barber != null){
            viewModelScope.launch {
                val token = "Bearer ${barber.getIdToken(true).await()?.token}"
                try {
                    val response = barberApi.getAccountBarber(token)
                    if (!response.status) {
                        _isLoading.value = false
                        _message.value =
                            context.getString(R.string.failed_to_get_data_barber, response.message)
                    } else{
                        _isLoading.value = false
                        _barberData.value = response.result
                    }
                } catch (e: retrofit2.HttpException){
                    _isLoading.value = false
                    _message.value = e.message
                } catch (e: Exception){
                    _isLoading.value = false
                    _message.value = e.message
                }
            }
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

    fun deleteAllBarHistory(){
        viewModelScope.launch(Dispatchers.IO) {
            history.deleteBarHistory()
        }
    }

    fun getQueue() = QueueManager.getQueue(context)

}