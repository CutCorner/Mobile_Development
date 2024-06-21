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
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.local.AddOn
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.Booking
import com.example.cepstun.utils.getRandomString
import kotlinx.coroutines.launch

class CheckoutViewModel (
    private val auth: RepositoryAuth,
    private val database: RepositoryDatabase,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String>()
    val message: MutableLiveData<String> = _message

    private val _dialog = MutableLiveData<String>()
    val dialog: LiveData<String> = _dialog

    private val _startActivityEvent = MutableLiveData<String>()
    val startActivityEvent: LiveData<String> = _startActivityEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _addOns = MutableLiveData<List<AddOn>>()
    val addOns: LiveData<List<AddOn>> = _addOns

    private val _nameBarber = MutableLiveData<String>()
    val nameBarber: LiveData<String> = _nameBarber


    fun bookedBarber(model: String, addOn: String, price: Double, barberId: String){
        _isLoading.value = true
        val user = auth.currentUser
        val uid = user?.uid
        val dbRef = database.getDatabase()
        val userRef = dbRef.child("users").child(uid!!)
        userRef.get().addOnSuccessListener {
            val name = it.child("fname").value.toString()

            // Determine next queue number for the barber
            val barberRef = dbRef.child("queue").child(barberId)
            barberRef.get().addOnSuccessListener { dataSnapshot ->
                val lastQueueNumber = if (dataSnapshot.hasChildren()) {
                    dataSnapshot.children.last().key?.toInt() ?: 0
                } else {
                    0
                }
                val nextQueueNumber = lastQueueNumber + 1

                val idOrder = getRandomString()
                val newBooking = Booking(
                    idOrder = idOrder,
                    userId = uid,
                    name = name,
                    model = model,
                    addon = addOn,
                    price = price,
                    proses = "Dalam Antrian"
                )
                barberRef.child(nextQueueNumber.toString()).setValue(newBooking)
                    .addOnSuccessListener {
                        _message.value = context.getString(R.string.booking_successfully)
                        _startActivityEvent.value = nextQueueNumber.toString()
                        _isLoading.value = false
                    }.addOnFailureListener {e->
                        _message.value = context.getString(R.string.booking_failed, e.message)
                        _isLoading.value = false
                    }
            }
        }
    }

    fun getAddOn(barberId: String){
        // get API
//        viewModelScope.launch {
//            val response = barberApi.getDetailBarberShop(barberId)
//            if (!response.result?.addons.isNullOrEmpty()){
//                _addOns.value = response.result?.addons?.map {
//                    AddOn(
//                        name = it.name.toString(),
//                        price = it.price?.toInt() ?: 0
//                    )
//                }
//            }
//        }

        // get List dummy
        val addOnsDummy = listOf(
            AddOn("AddOn A", 5000),
            AddOn("AddOn B", 10000),
            AddOn("AddOn C", 15000),
            AddOn("AddOn D", 20000),
            AddOn("AddOn E", 25000)
        )
        _addOns.value = addOnsDummy

        BarberDataList.barberDataValue.find { it.id == barberId }?.let {
            _nameBarber.value = it.name
        }

    }

}