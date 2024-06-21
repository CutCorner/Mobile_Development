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
import com.example.cepstun.data.local.ListOrder
import com.example.cepstun.data.local.entity.barbershop.HistoryBarbershop
import com.example.cepstun.data.local.entity.customer.HistoryCustomer
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

    private val _message = MutableLiveData<String>()
    val message : LiveData<String> = _message

    private val _order = MutableLiveData<Booking?>()
    val order: LiveData<Booking?> = _order

    private val _listOrder = MutableLiveData<ListOrder?>()
    val listOrder: LiveData<ListOrder?> = _listOrder

    /*
                                                            Customer
     */
    // History
    fun getCusHistory(): LiveData<List<HistoryCustomer>> = history.getCusHistory()

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
                            idOrder = bookingSnapshot.child("idOrder").getValue(String::class.java) ?: "",
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

    // OnGoing
    fun getQueue() = QueueManager.getQueue(context)


    /*
                                                        Barber
     */
    // History
    fun getBarHistory(): LiveData<List<HistoryBarbershop>> = history.getBarHistory()

    // OnGoing
    fun getFirstBooked(){
        val barberId = auth.currentUser?.uid
        val dbRef = database.getDatabase()
        val barberRef = dbRef.child("queue").child(barberId!!)
        barberRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.hasChildren()) {
                val firstChildSnapshot = dataSnapshot.children.first()
                val position = firstChildSnapshot.key?.toInt() ?: 0
                val userId = firstChildSnapshot.child("userId").getValue(String::class.java) ?: ""
                val name = firstChildSnapshot.child("name").getValue(String::class.java) ?: ""
                val model = firstChildSnapshot.child("model").getValue(String::class.java) ?: ""
                val addon = firstChildSnapshot.child("addon").getValue(String::class.java) ?: ""
                val price = firstChildSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                val proses = firstChildSnapshot.child("proses").getValue(String::class.java) ?: ""

                val order = ListOrder(
                    position = position,
                    userId = userId,
                    name = name,
                    model = model,
                    addon = addon,
                    price = price,
                    proses = proses
                )
                _listOrder.value = order
            } else {
                _listOrder.value = null
            }
        }
    }

}