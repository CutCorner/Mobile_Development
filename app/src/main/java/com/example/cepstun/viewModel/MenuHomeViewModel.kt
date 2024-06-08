package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cepstun.R
import com.example.cepstun.data.QueueManager
import com.example.cepstun.helper.getStringAddress
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

class MenuHomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _address = MutableLiveData<String?>()
    val address: LiveData<String?> = _address

    private val _permissionRequired = MutableLiveData<Boolean>()
    val permissionRequired: LiveData<Boolean> = _permissionRequired

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    fun getLastLocation(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        try {
                            val latLng = LatLng(it.latitude, it.longitude)
                            _address.value = latLng.getStringAddress(context)
                        } catch (e: IOException) {
                            _errorMessage.value = context.getString(R.string.error_location_MenuHome, e.printStackTrace())
                        }
                    } ?: run {
                        _errorMessage.value = context.getString(R.string.error_location2_MenuHome)
                    }
                }
        } catch (e: SecurityException) {
            _permissionRequired.value = true
        }
    }

    fun getQueue() = QueueManager.getQueue(context)

}