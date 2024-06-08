package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.local.Booking
import com.example.cepstun.ui.activity.BarberLocationActivity
import com.example.cepstun.ui.activity.ChooseUserActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CheckoutViewModel (
    private val auth: RepositoryAuth,
    private val database: RepositoryDatabase,
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


    fun bookedBarber(model: String, addOn: String, price: Double, barberId: String){
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

                // Add new booking to database
                val newBooking = Booking(
                    userId = uid,
                    name = name,
                    model = model,
                    addon = addOn,
                    price = price,
                    proses = "on Queue"
                )
                barberRef.child(nextQueueNumber.toString()).setValue(newBooking)
                    .addOnSuccessListener {
                        _message.value = "Booking successfull"
                        _startActivityEvent.value = nextQueueNumber.toString()

                    }.addOnFailureListener {e->
                        _message.value = "Booking failed: ${e.message}"
                    }
            }
        }
    }

}