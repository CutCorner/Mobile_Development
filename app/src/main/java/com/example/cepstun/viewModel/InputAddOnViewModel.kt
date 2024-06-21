package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.local.AddAddOn
import com.example.cepstun.ui.activity.MainActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class InputAddOnViewModel(
    private val auth: RepositoryAuth,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun saveAddOn(listAddAddOn: List<AddAddOn>, idBarber: String) {
        _isLoading.value = true
        if (listAddAddOn.isEmpty()) {
            _isLoading.value = false
            Intent(context, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(it)
            }

        } else {
            val barber = auth.currentUser
            viewModelScope.launch {
                var allModelsAddedSuccessfully = true
                for (model in listAddAddOn) {
                    val token = "Bearer ${barber?.getIdToken(true)?.await()?.token}"
                    val name = model.name
                    val price = model.price
                    try {
                        val response = barberApi.addAddOn(idBarber, token, name, price)
                        if (response.status != true) {
                            allModelsAddedSuccessfully = false
                            _message.value = context.getString(
                                R.string.failed_to_add_addon_service, response.message
                            )
                        }
                    } catch (e: Exception) {
                        allModelsAddedSuccessfully = false
                        _message.value =
                            context.getString(R.string.failed_to_add_addon_service, e.message)
                    }
                }

                _isLoading.value = false
                if (allModelsAddedSuccessfully) {
                    _message.value =
                        context.getString(R.string.successfully_add_addon_service_to_barbershop)
                    Intent(context, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(it)
                    }
                }
            }
        }
    }
}