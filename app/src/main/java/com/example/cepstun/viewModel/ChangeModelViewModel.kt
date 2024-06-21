package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.local.AddModel
import com.example.cepstun.data.local.ListOrder
import com.example.cepstun.data.remote.dataClass.DeleteModelRequest
import com.example.cepstun.data.remote.response.barber.ResultData
import com.example.cepstun.utils.reduceFileImage
import com.example.cepstun.utils.uriToFile
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChangeModelViewModel (
    private val auth: RepositoryAuth,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

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

    fun deleteModel(name: String) {
        _isLoading.value = true
        val barber = auth.currentUser
        if (barber != null){
            viewModelScope.launch {
                val token = "Bearer ${barber.getIdToken(true).await()?.token}"
                val request = DeleteModelRequest(name)
                _barberData.value?.let { barberData ->
                    if (barberData.store.isNotEmpty()) {
                        val barberId = barberData.store[0].barberId
                        try {
                            val response = barberApi.deleteModelBarber(barberId, token, request)
                            if (!response.status) {
                                _isLoading.value = false
                                _message.value = context.getString(
                                    R.string.failed_to_delete_model,
                                    name,
                                    response.message
                                )
                                Log.d("response false", "deleteModel: ${response.message}")
                            } else{
                                _isLoading.value = false
                                _message.value =
                                    context.getString(R.string.model_successfully_deleted, name)
                                getBarberData()
                            }
                        } catch (e: retrofit2.HttpException){
                            _isLoading.value = false
                            _message.value = e.message
                            Log.d("retrofit2.HttpException", "deleteModel: ${e.message}")
                        } catch (e: Exception){
                            _isLoading.value = false
                            _message.value = e.message
                            Log.d("Exception", "deleteModel: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    fun addModel(model: AddModel) {
        val barber = auth.currentUser
        if (barber != null){
            viewModelScope.launch {
                val token = "Bearer ${barber.getIdToken(true).await()?.token}"
                val name = model.name
                val price = model.price
                val imageFile = uriToFile(model.uri, context).reduceFileImage()
                _barberData.value?.let { barberData ->
                    if (barberData.store.isNotEmpty()) {
                        val barberId = barberData.store[0].barberId
                        try {
                            val response = barberApi.addModel(barberId, token, name, price, imageFile)
                            if (response.status != true) {
                                _message.value = context.getString(R.string.failed_to_add_model, response.message)
                            } else {
                                _message.value =
                                    context.getString(R.string.successfully_added_the_model)
                                getBarberData()
                            }
                        } catch (e: retrofit2.HttpException) {
                            _message.value = context.getString(R.string.failed_to_add_model, e.message)
                        } catch (e: Exception) {
                            _message.value = context.getString(R.string.failed_to_add_model, e.message)
                        }
                    }
                }
            }
        }
    }

}