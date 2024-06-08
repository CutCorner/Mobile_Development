package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.QueueManager
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.local.Booking
import com.example.cepstun.data.local.UserDatabase
import com.example.cepstun.data.local.entity.HistoryCustomer
import com.example.cepstun.data.local.onGoingCustomer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderPagerViewModel(
    private val history: RepositoryHistory,
    private val database: RepositoryDatabase,
    private val auth: RepositoryAuth,
    application: Application
) :
    AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _onGoing = MutableLiveData<onGoingCustomer?>()
    val onGoing: LiveData<onGoingCustomer?> = _onGoing

    private val _message = MutableLiveData<String>()
    val message : LiveData<String> = _message

    private val _order = MutableLiveData<Booking?>()
    val order: LiveData<Booking?> = _order

    /*
                                                            Customer
     */
    // History
    fun getCusHistory(): LiveData<List<HistoryCustomer>> = history.getCusHistory()

    fun insertCusHistory(historyCus: HistoryCustomer) {
        viewModelScope.launch(Dispatchers.IO) {
            history.insertCusHistory(historyCus)
        }
    }

    fun deleteCusHistory(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            history.deleteCusHistoryById(id)
        }
    }

    fun observeQueue(barberId: String, position: Int) {
        val dbRef = database.getDatabase()
        val barberRef = dbRef.child("queue").child(barberId)

        barberRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val bookingSnapshot = dataSnapshot.child(position.toString())
                    if (bookingSnapshot.exists()) {
                        val booking = Booking(
                            userId = bookingSnapshot.child("userId").getValue(String::class.java) ?: "",
                            name = bookingSnapshot.child("name").getValue(String::class.java) ?: "",
                            model = bookingSnapshot.child("model").getValue(String::class.java) ?: "",
                            addon = bookingSnapshot.child("addon").getValue(String::class.java) ?: "",
                            price = bookingSnapshot.child("price").getValue(Double::class.java) ?: 0.0,
                            proses = bookingSnapshot.child("proses").getValue(String::class.java) ?: ""
                        )
                        _order.value = booking
                    }
                } else {
                    _order.value = null
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _message.value = databaseError.message
            }
        })
    }

    fun getQueue() = QueueManager.getQueue(context)

//    fun getCusOnGoing(){
//        viewModelScope.launch(Dispatchers.IO) {
//            val dbRef = database.getDatabase()
//            val user = auth.currentUser!!
//            val userRef = dbRef.child("Barber").child(user.uid)
//
//            userRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val userDatabase = dataSnapshot.getValue(UserDatabase::class.java)
//                    _onGoing.value = userDatabase?.onGoing
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    _message.value = databaseError.message
//                }
//            })
//        }
//    }


    /*
                                                        Barber
     */
    // History

    // OnGoing


}