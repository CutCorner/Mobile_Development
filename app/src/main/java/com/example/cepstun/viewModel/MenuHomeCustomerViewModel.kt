package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.QueueManager
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.RepositoryNotification
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.FeedbackData
import com.example.cepstun.data.local.Image
import com.example.cepstun.data.local.entity.customer.HistoryCustomer
import com.example.cepstun.data.local.entity.customer.NotificationCustomer
import com.example.cepstun.utils.getCurrentDate
import com.example.cepstun.utils.getStringAddress
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class MenuHomeCustomerViewModel(
    private val auth: RepositoryAuth,
    private val database: RepositoryDatabase,
    private val history: RepositoryHistory,
    private val notification: RepositoryNotification,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _address = MutableLiveData<String?>()
    val address: LiveData<String?> = _address

    private val _permissionRequired = MutableLiveData<Boolean>()
    val permissionRequired: LiveData<Boolean> = _permissionRequired

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _feedbackData = MutableLiveData<FeedbackData?>()
    val feedbackData: LiveData<FeedbackData?> = _feedbackData

    private val _listBarber = MutableLiveData<List<BarberData>>()
    val listBarber: LiveData<List<BarberData>> = _listBarber

    private val _listBarber2 = MutableLiveData<List<BarberData>>()
    val listBarber2: LiveData<List<BarberData>> = _listBarber2

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _lat = MutableLiveData<Double>()
    private val _long = MutableLiveData<Double>()

    fun getLastLocation(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        try {
                            _lat.value = it.latitude
                            _long.value = it.longitude
                            val latLng = LatLng(it.latitude, it.longitude)
                            _address.value = latLng.getStringAddress(context)
                        } catch (e: IOException) {
                            _message.value = context.getString(R.string.error_location_MenuHome, e.printStackTrace())
                        }
                    } ?: run {
                        _message.value = context.getString(R.string.error_location2_MenuHome)
                    }
                }
        } catch (e: SecurityException) {
            _permissionRequired.value = true
        }
    }

    fun getQueue() = QueueManager.getQueue(context)

    fun getMessage() {
        val userId = auth.currentUser?.uid
        val dbRef = database.getDatabase()
        val messageRef = dbRef.child("users").child(userId!!).child("message")

        messageRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val feedbackData = FeedbackData(
                        idBarber = dataSnapshot.child("idBarber").value as String,
                        nameBarber = dataSnapshot.child("nameBarber").value as String,
                        logoBarber = dataSnapshot.child("logoBarber").value as String,
                        modelBarber = dataSnapshot.child("modelBarber").value as String,
                        addOnBarber = dataSnapshot.child("addOnBarber").value as String,
                        priceBarber = (dataSnapshot.child("priceBarber").value as Long).toInt(),
                        status = dataSnapshot.child("status").value as String,
                        message = dataSnapshot.child("message").value as String
                    )
                    _feedbackData.value = feedbackData
                    saveToHistory(feedbackData)
                    saveToNotification(feedbackData)
                    messageRef.removeValue()
                } else {
                    _feedbackData.value = null
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _message.value = databaseError.message
            }
        })
    }

    fun saveToHistory(feedbackData: FeedbackData){
        val historyData = HistoryCustomer(
            idBarber = feedbackData.idBarber,
            nameBarber = feedbackData.nameBarber,
            logoBarber = feedbackData.logoBarber,
            modelBarber = feedbackData.modelBarber,
            addOnBarber = feedbackData.addOnBarber,
            priceBarber = feedbackData.priceBarber,
            status = feedbackData.status,
            message = feedbackData.message
        )

        viewModelScope.launch(Dispatchers.IO) {
            history.insertCusHistory(historyData)
        }

    }

    fun saveToNotification(feedbackData: FeedbackData){
        val dateNow = getCurrentDate()
        val notificationData = NotificationCustomer(
            nameBarber = feedbackData.nameBarber,
            modelBarber = feedbackData.modelBarber,
            addOnBarber = feedbackData.addOnBarber,
            message = feedbackData.message,
            status = feedbackData.status,
            date = dateNow
        )

        viewModelScope.launch(Dispatchers.IO) {
            notification.insertNotificationHistory(notificationData)
        }
    }

    fun sendFeedback(barberId: String, review: String, rating: Float, model: String, addOn: String) {
        val userId = auth.currentUser?.uid

        val dbRef = database.getDatabase()
        val userRef = dbRef.child("user").child(userId!!)
        var imageCustomer = ""
        var nameCustomer = ""

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    imageCustomer = dataSnapshot.child("photo").value as String
                    nameCustomer = dataSnapshot.child("fname").value as String
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _message.value = databaseError.message
            }
        })

        val date = getCurrentDate()

        // Prepare data for API
        val dataForApi = mapOf(
            "barberId" to barberId,
            "idCustomer" to userId,
            "nameCustomer" to nameCustomer,
            "imageCustomer" to imageCustomer,
            "reviewCustomer" to review,
            "scoreCustomer" to rating,
            "model" to date,
            "addOn" to model,
            "date" to addOn
        )

        // Log data
        Log.d("SendFeedbackToApi", "Data for API: $dataForApi")
    }

    fun getListBarber() {
//        _isLoading.value = true
//        if (_lat.value != null && _long.value != null){
//            viewModelScope.launch {
//                try {
//                    val response = barberApi.getRecommendation(lat = _lat.value!!, long = _long.value!!, k=15)
//                    if (response.isNotEmpty()){
//                        val barberDataList = response.map {
//                            BarberData(
//                                id = it.barberId.toString(),
//                                name = it.name,
//                                logo = it.imgSrc,
//                                rate = it.rating,
//                                lat = it.lat,
//                                lon = it.lon,
//                                location = it.location,
//                                model = listOf(),
//                                image = listOf(Image(id = UUID.randomUUID().toString(), picture = it.imgSrc)),
//                                rating = listOf()
//                            )
//                        }
//                        _listBarber.value = barberDataList
//                        _isLoading.value = false
//                    } else{
//                        _isLoading.value = false
//                        _message.value =
//                            context.getString(R.string.data_not_found_make_sure_your_location_is_switched_on)
//                    }
//                } catch (e: retrofit2.HttpException){
//                    _isLoading.value = false
//                    _message.value = e.message
//                } catch (e: Exception){
//                    _isLoading.value = false
//                    _message.value = e.message
//                }
//            }
//        }

        _listBarber.value = BarberDataList.barberDataValue
    }

    fun findBarbershop(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = barberApi.searchBarber(query)
                if (response.result.data.isNotEmpty()){
                    val barberDataList = response.result.data.map {
                        BarberData(
                            id = it.barberId,
                            name = it.name,
                            logo = it.imgSrc,
                            rate = null,
                            lat = it.lat,
                            lon = it.long,
                            location = it.location,
                            model = listOf(),
                            image = listOf(Image(id = UUID.randomUUID().toString(), picture = it.imgSrc)),
                            rating = listOf()
                        )
                    }
                    _listBarber2.value = barberDataList
                    _isLoading.value = false
                } else{
                    _isLoading.value = false
                    _message.value =
                        context.getString(R.string.data_not_found_make_sure_your_location_is_switched_on)
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