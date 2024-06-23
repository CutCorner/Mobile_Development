package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.net.http.HttpException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryStorage
import com.example.cepstun.data.responseFirebase.DatabaseUpdateResult
import com.example.cepstun.data.responseFirebase.StorageResult
import com.example.cepstun.ui.activity.barbershop.InputModelActivity
import com.example.cepstun.utils.reduceFileImage
import com.example.cepstun.utils.uriToFile
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterBarberViewModel (
    private val auth: RepositoryAuth,
    private val database: RepositoryDatabase,
    private val storage: RepositoryStorage,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application){

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerBarber(nameBarber: String, logo: Uri, lat: Double, lon: Double, location: String, listImageBarber: List<Uri>, phone: String){
        _isLoading.value = true
        val barber = auth.currentUser
        val logoBarber = uriToFile(logo, context).reduceFileImage()
        val barberId = barber!!.uid

        val listImageBarberFiles = listImageBarber.map { uriToFile(it, context).reduceFileImage() }

        viewModelScope.launch {
            val token = "Bearer ${barber.getIdToken(true).await().token}"
            try {
                val response = barberApi.createBarbershop(token, nameBarber, lat, lon, location, phone, logoBarber, listImageBarberFiles)
                if (response.status == true) {
                    _message.value = response.message
                    when (val storageResult = storage.uploadFile(logoBarber, barberId)) {
                        is StorageResult.Success -> {
                            when (val dbResult = database.updateDatabase(nameBarber, phone, null, null, storageResult.uri.toString())) {
                                is DatabaseUpdateResult.Success -> {
                                    _message.value =
                                        context.getString(R.string.successfully_register_barber_account)
                                    Intent(context, InputModelActivity::class.java).also {
                                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        it.putExtra(InputModelActivity.EXTRA_BARBERSHOP_ID, response.result?.barberId)
                                        context.startActivity(it)
                                    }
                                }
                                is DatabaseUpdateResult.Error -> {
                                    _message.value = context.getString(
                                        R.string.failed_to_register_database_error,
                                        dbResult.exception.message
                                    )
                                }
                            }
                        }
                        is StorageResult.Error -> {
                            _message.value = context.getString(
                                R.string.failed_to_upload_file,
                                storageResult.exception.message
                            )
                        }
                    }
                } else {
                    _message.value =
                        context.getString(R.string.failed_to_create_barbershop, response.message)
                }
            } catch (e: retrofit2.HttpException){
                _isLoading.value = false
                val errorJsonString = e.response()?.errorBody()?.string()
                _message.value = context.getString(R.string.failed_to_create_barbershop, errorJsonString)
            } catch (e: Exception){
                _isLoading.value = false
                _message.value = context.getString(R.string.failed_to_create_barbershop, e.message)
            }
        }
    }

}