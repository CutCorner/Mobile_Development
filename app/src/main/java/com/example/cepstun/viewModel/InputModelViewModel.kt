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
import com.example.cepstun.data.local.AddModel
import com.example.cepstun.ui.activity.barbershop.InputAddonActivity
import com.example.cepstun.utils.reduceFileImage
import com.example.cepstun.utils.uriToFile
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class InputModelViewModel (
    private val auth: RepositoryAuth,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application){

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun saveModel(listAddModel: List<AddModel>, idBarber: String) {
        val barber = auth.currentUser

        if (barber != null){
            viewModelScope.launch {
                _isLoading.value = true
                var allModelsAddedSuccessfully = true
                for (model in listAddModel) {
                    val token = "Bearer ${barber.getIdToken(true).await()?.token}"
                    val name = model.name
                    val price = model.price
                    val imageFile = uriToFile(model.uri, context).reduceFileImage()
                    try {
                        val response = barberApi.addModel(idBarber, token, name, price, imageFile)
                        if (response.status != true) {
                            allModelsAddedSuccessfully = false
                            _message.value = context.getString(R.string.failed_to_add_model, response.message)
                        }
                    } catch (e: retrofit2.HttpException) {
                        allModelsAddedSuccessfully = false
                        _message.value = context.getString(R.string.failed_to_add_model, e.message)
                    } catch (e: Exception) {
                        allModelsAddedSuccessfully = false
                        _message.value = context.getString(R.string.failed_to_add_model, e.message)
                    }
                }

                _isLoading.value = false
                if (allModelsAddedSuccessfully) {
                    _message.value = context.getString(R.string.successfully_add_model_to_barbershop)
                    Intent(context, InputAddonActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        it.putExtra(InputAddonActivity.EXTRA_BARBER_ID, idBarber)
                        context.startActivity(it)
                    }
                }
            }
        }
    }



}