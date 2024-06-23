package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.local.AddAddOn
import com.example.cepstun.data.remote.response.barber.ResultData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChangeAddOnViewModel (
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

    fun deleteAddOn(name: String) {
        _isLoading.value = true
        val barber = auth.currentUser
        if (barber != null){
            viewModelScope.launch {
                val token = "Bearer ${barber.getIdToken(true).await()?.token}"
                _barberData.value?.let { barberData ->
                    if (barberData.store.isNotEmpty()) {
                        val barberId = barberData.store[0].barberId
                        try {
                            val response = barberApi.deleteAddOnBarber(barberId, token, name)
                            if (!response.status) {
                                _isLoading.value = false
                                _message.value = context.getString(
                                    R.string.failed_to_delete_addon,
                                    name,
                                    response.message
                                )
                            } else{
                                _isLoading.value = false
                                _message.value =
                                    context.getString(R.string.addon_successfully_deleted, name)
                                getBarberData()
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
        }
    }

    fun addAddOn(addon: AddAddOn) {
        val barber = auth.currentUser
        if (barber != null){
            viewModelScope.launch {
                val token = "Bearer ${barber.getIdToken(true).await()?.token}"
                val name = addon.name
                val price = addon.price
                _barberData.value?.let { barberData ->
                    if (barberData.store.isNotEmpty()) {
                        val barberId = barberData.store[0].barberId
                        try {
                            val response = barberApi.addAddOn(barberId, token, name, price)
                            if (response.status != true) {
                                _message.value = context.getString(R.string.failed_to_add_addon_service, response.message)
                            } else {
                                _message.value =
                                    context.getString(R.string.successfully_added_the_addon)
                                getBarberData()
                            }
                        } catch (e: retrofit2.HttpException) {
                            _message.value = context.getString(R.string.failed_to_add_addon_service, e.message)
                        } catch (e: Exception) {
                            _message.value = context.getString(R.string.failed_to_add_addon_service, e.message)
                        }
                    }
                }
            }
        }
    }

}