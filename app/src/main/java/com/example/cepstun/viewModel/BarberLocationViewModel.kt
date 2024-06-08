package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.QueueManager
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.RepositorySharedPreference
import com.example.cepstun.data.local.Booking
import com.example.cepstun.data.local.Queue
import com.example.cepstun.data.local.entity.HistoryCustomer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BarberLocationViewModel(
    private val history: RepositoryHistory,
    private val database: RepositoryDatabase,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String>()
    val message: MutableLiveData<String> = _message

    private val _dialog = MutableLiveData<String>()
    val dialog: LiveData<String> = _dialog

    private val _currentQueue = MutableLiveData<Int>()
    val currentQueue: LiveData<Int> = _currentQueue

    private val _remainingQueue = MutableLiveData<Int>()
    val remainingQueue: LiveData<Int> = _remainingQueue

    private val _order = MutableLiveData<Booking?>()
    val order: LiveData<Booking?> = _order


    val combinedQueueData = MediatorLiveData<Pair<Int, Int>>().apply {
        addSource(_currentQueue) { cur ->
            val rem = _remainingQueue.value ?: 0
            value = Pair(cur ?: 0, rem)
        }
        addSource(_remainingQueue) { rem ->
            val cur = _currentQueue.value ?: 0
            value = Pair(cur, rem ?: 0)
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

                    if (!dataSnapshot.hasChild(position.toString())){
                        _currentQueue.value = -1
                        deleteQueue()
                    } else {
                        _currentQueue.value = dataSnapshot.children.first().key?.toInt()

                        val queueList = mutableListOf<Map<String, Any>>()
                        for (childSnapshot in dataSnapshot.children) {
                            val queueData = childSnapshot.value as Map<String, Any>
                            queueList.add(queueData)
                        }

                        // Update _remainingQueue.value with the count of childSnapshot.key > position
                        _remainingQueue.value = dataSnapshot.children.count { it.key?.toInt()!! < position }

                        if (dataSnapshot.children.first().key?.toInt() == position){
                            val currentUser = queueList[0]["name"]
                            _dialog.value = "$currentUser, your haircut is starting now."
                        } else if (dataSnapshot.children.count { it.key?.toInt()!! < position } == 1){
                            val nextUser = queueList[1]["name"]
                            _dialog.value = "$nextUser, you are next in line for a haircut."
                        }

                    }
                } else {
                    _currentQueue.value = -1
                    deleteQueue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _message.value = databaseError.message
            }
        })
    }

    fun cancelQueue(barberId: String, position: Int) {
        val dbRef = database.getDatabase()
        dbRef.child("queue").child(barberId).child(position.toString()).removeValue()
            .addOnSuccessListener {
                _message.value = "Queue cancelled successfully"
            }
            .addOnFailureListener { e ->
                _message.value = e.message
            }
    }

    fun saveQueue(barberId: String, yourQueue: String) {
        QueueManager.addQueue(context, Queue(barberId, yourQueue))
    }

    fun deleteQueue() {
        QueueManager.deleteQueue(context)
    }

    fun getQueue() = QueueManager.getQueue(context)

    fun insertCusHistory(historyCus: HistoryCustomer) {
        viewModelScope.launch(Dispatchers.IO) {
            history.insertCusHistory(historyCus)
        }
    }

}