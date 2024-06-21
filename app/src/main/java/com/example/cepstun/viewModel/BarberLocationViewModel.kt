package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.QueueManager
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.RepositorySharedPreference
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.Booking
import com.example.cepstun.data.local.Queue
import com.example.cepstun.data.local.QueueData
import com.example.cepstun.data.local.entity.customer.HistoryCustomer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarberLocationViewModel(
    private val history: RepositoryHistory,
    private val database: RepositoryDatabase,
    setting: RepositorySharedPreference,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _message = MutableLiveData<String>()
    val message: MutableLiveData<String> = _message

    private val _dialog = MutableLiveData<String>()
    val dialog: LiveData<String> = _dialog

    private val _currentQueue = MutableLiveData<Int>()

    private val _remainingQueue = MutableLiveData<Int>()
    val remainingQueue: LiveData<Int> = _remainingQueue

    private val _order = MutableLiveData<Booking?>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
        _isLoading.value = true
        val dbRef = database.getDatabase()
        val barberRef = dbRef.child("queue").child(barberId)

        barberRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    _isLoading.value = false
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

                    if (!dataSnapshot.hasChild(position.toString())){
                        _currentQueue.value = -1
                        deleteQueue()
                    } else {
                        _currentQueue.value = dataSnapshot.children.first().key?.toInt()

                        val queueList = mutableListOf<QueueData>()
                        for (childSnapshot in dataSnapshot.children) {
                            val queueData = childSnapshot.getValue(QueueData::class.java)
                            queueData?.let {
                                queueList.add(it)
                            }
                        }

                        _remainingQueue.value = dataSnapshot.children.count { it.key?.toInt()!! < position }

                        if (dataSnapshot.children.first().key?.toInt() == position){
                            val currentUser = queueList[0].name
                            _dialog.value = context.getString(
                                R.string.your_haircut_is_starting_now,
                                currentUser
                            )
                        } else if (dataSnapshot.children.count { it.key?.toInt()!! < position } == 1){
                            val nextUser = queueList[1].name
                            _dialog.value = context.getString(
                                R.string.you_are_next_in_line_for_a_haircut,
                                nextUser
                            )
                        }

                    }
                } else {
                    _isLoading.value = false
                    _currentQueue.value = -1
                    deleteQueue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _isLoading.value = false
                _message.value = databaseError.message
            }

        })
    }

    fun cancelQueue(barberId: String, position: Int) {
        insertCusHistory(barberId)

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

    private fun insertCusHistory(barberId: String) {
        val barberData = BarberDataList.barberDataValue.find { it.id == barberId }!!
        val historyData = HistoryCustomer(
            idBarber = barberId,
            nameBarber = barberData.name,
            logoBarber = barberData.logo,
            modelBarber = _order.value!!.model,
            addOnBarber = _order.value!!.addon,
            priceBarber = _order.value!!.price.toInt(),
            status = "Batal"
        )

        viewModelScope.launch(Dispatchers.IO) {
            history.insertCusHistory(historyData)
        }
    }

    fun setMapStyle(mMap: GoogleMap) {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplication(), R.raw.map_style))
            if (!success) {
                _message.postValue("Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            _message.postValue("Can't find style. Error: ")
        }
    }

    val themeSetting: LiveData<Boolean?> = setting.getThemeSetting().asLiveData()

}