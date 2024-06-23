package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryStorage
import com.example.cepstun.data.remote.dataClass.ImageItem
import com.example.cepstun.data.remote.response.barber.ResultData
import com.example.cepstun.ui.activity.MainActivity
import com.example.cepstun.utils.downloadImage
import com.example.cepstun.utils.getFullImageUrl
import com.example.cepstun.utils.reduceFileImage
import com.example.cepstun.utils.uriToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class EditDataBarbershopViewModel(
    private val auth: RepositoryAuth,
    private val database: RepositoryDatabase,
    private val storage: RepositoryStorage,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _barberData = MutableLiveData<ResultData>()
    val barberData: LiveData<ResultData> = _barberData

    fun getBarberData() {
        _isLoading.value = true
        val barber = auth.currentUser
        if (barber != null) {
            viewModelScope.launch {
                val token = "Bearer ${barber.getIdToken(true).await()?.token}"
                try {
                    val response = barberApi.getAccountBarber(token)
                    if (!response.status) {
                        _isLoading.value = false
                        _message.value =
                            context.getString(R.string.failed_to_get_data_barber, response.message)
                    } else {
                        _isLoading.value = false
                        _barberData.value = response.result
                    }
                } catch (e: retrofit2.HttpException) {
                    _isLoading.value = false
                    _message.value = e.message
                } catch (e: Exception) {
                    _isLoading.value = false
                    _message.value = e.message
                }
            }
        }
    }

    fun editBarber(
        nameBarber: String,
        logo: Uri,
        lat: Double,
        lon: Double,
        location: String,
        imageItems: List<ImageItem>,
        phone: String
    ) {
        _isLoading.value = true
        val barber = auth.currentUser

        var logoBarber: Uri
        var fileLogoBarber: File



        viewModelScope.launch {
            val token = "Bearer ${barber?.getIdToken(true)?.await()?.token}"

            val listImageBarberFiles = imageItems.map {
                when (it) {
                    is ImageItem.UriImage -> uriToFile(it.uri, context).reduceFileImage()
                    is ImageItem.UrlImage -> {
                        if (it.file != null) {
                            it.file
                        } else {
                            val downloadedImageUri = withContext(Dispatchers.IO) {
                                downloadImage(
                                    it.url.getFullImageUrl(), context
                                )
                            }
                            uriToFile(downloadedImageUri!!, context).reduceFileImage()
                        }
                    }
                }
            }

            _barberData.value?.let { barberData ->

                if (barberData.store.isNotEmpty()) {
                    val barberId = barberData.store[0].barberId
                    val logoUrl = barberData.store[0].imgSrc

                    try {
                        if (logo.toString() == Uri.parse(logoUrl).toString()) {
                            logoBarber = withContext(Dispatchers.IO) {
                                downloadImage(
                                    logoUrl.getFullImageUrl(), context
                                )!!
                            }
                            fileLogoBarber = uriToFile(logoBarber, context).reduceFileImage()
                        } else {
                            fileLogoBarber = uriToFile(logo, context).reduceFileImage()
                        }

                        val response = barberApi.updateBarbershop(
                            token,
                            barberId,
                            nameBarber,
                            lat,
                            lon,
                            location,
                            phone,
                            fileLogoBarber,
                            listImageBarberFiles
                        )
                        if (response.status == true) {
                            _message.value = response.message
                            getBarberData()
                            Intent(context, MainActivity::class.java). also {
                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(it)
                            }
                        } else {
                            _message.value = context.getString(
                                R.string.failed_to_create_barbershop, response.message
                            )
                        }

                    } catch (e: retrofit2.HttpException) {
                        _isLoading.value = false
                        val errorJsonString = e.response()?.errorBody()?.string()
                        _message.value =
                            context.getString(R.string.failed_to_create_barbershop, errorJsonString)
                    } catch (e: Exception) {
                        _isLoading.value = false
                        _message.value =
                            context.getString(R.string.failed_to_create_barbershop, e.message)
                    }
                }
            }
        }
    }
}