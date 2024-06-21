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
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.ListOrder
import com.example.cepstun.data.local.entity.barbershop.HistoryBarbershop
import com.example.cepstun.data.remote.response.barber.ResultData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuHomeBarberViewModel (
    private val auth: RepositoryAuth,
    private val database: RepositoryDatabase,
    private val history: RepositoryHistory,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    fun getIdBarber () = auth.currentUser?.uid

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _order = MutableLiveData<List<ListOrder>?>()
    val order: LiveData<List<ListOrder>?> = _order

    private val _barberData = MutableLiveData<ResultData>()
    val barberData: LiveData<ResultData> = _barberData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getBarberData() {
        _isLoading.value = true
        val barber = auth.currentUser
        if (barber != null){
            viewModelScope.launch {
                val token = "Bearer ${barber.getIdToken(true).await()?.token}"
                try {
                    val response = barberApi.getAccountBarber(token)
                    if (!response.status) {
                        _isLoading.value = false
                        _message.value =
                            context.getString(R.string.failed_to_get_data_barber, response.message)
                    } else{
                        _isLoading.value = false
                        _barberData.value = response.result
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

    fun cekBarbershopOpen(): Task<Boolean> {
        val barberId = getIdBarber()
        val dbRef = database.getDatabase()
        val barberRef = dbRef.child("users").child(barberId.toString()).child("open")

        return barberRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                dataSnapshot.getValue(Boolean::class.java) ?: false
            } else {
                _message.value = task.exception?.message
                false
            }
        }
    }

    fun setOpen(isOpen: Boolean) {
        val barberId = getIdBarber()
        val dbRef = database.getDatabase()
        val openRef = dbRef.child("users").child(barberId.toString()).child("open")

        openRef.setValue(isOpen)
            .addOnSuccessListener {
                if (isOpen){
                    _message.value = "Barbershop open"
                } else {
                    _message.value = "Barbershop closed"
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _message.value = it.message
            }
    }

    fun getAllQueue(barberId: String) {
        val dbRef = database.getDatabase()
        val barberRef = dbRef.child("queue").child(barberId)

        barberRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val queueList = mutableListOf<ListOrder>()
                    for (childSnapshot in dataSnapshot.children) {
                        val position = childSnapshot.key?.toInt() ?: 0
                        val order = ListOrder(
                            position = position,
                            userId = childSnapshot.child("userId").getValue(String::class.java) ?: "",
                            name = childSnapshot.child("name").getValue(String::class.java) ?: "",
                            model = childSnapshot.child("model").getValue(String::class.java) ?: "",
                            addon = childSnapshot.child("addon").getValue(String::class.java) ?: "",
                            price = childSnapshot.child("price").getValue(Double::class.java) ?: 0.0,
                            proses = childSnapshot.child("proses").getValue(String::class.java) ?: ""
                        )
                        queueList.add(order)
                    }
                    _order.value = queueList

                } else {
                    _order.value = null
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _isLoading.value = false
                _message.value = databaseError.message
            }
        })
    }

    fun acceptOrder(order: ListOrder){
        _isLoading.value = true
        saveAcceptHistory(order)

        val barberId = getIdBarber()
        val dbRef = database.getDatabase()
        dbRef.child("queue").child(barberId!!).child(order.position.toString()).removeValue()
            .addOnSuccessListener {
                _message.value =
                    context.getString(R.string.order_completed_send_feedback_form_to_customer)
            }
            .addOnFailureListener { e ->
                _message.value = e.message
            }

        val sendFeedback = dbRef.child("users").child(order.userId).child("message")

        val barberData = BarberDataList.barberDataValue.find { it.id == barberId }!!

        val feedbackData = mapOf(
            "idBarber" to barberId,
            "nameBarber" to barberData.name,
            "logoBarber" to barberData.logo,
            "modelBarber" to order.model,
            "addOnBarber" to order.addon,
            "priceBarber" to order.price.toInt(),
            "status" to "Pesanan Selesai",
            "message" to "-"
        )

        sendFeedback.setValue(feedbackData)
            .addOnSuccessListener {
                _message.value = context.getString(R.string.feedback_sent_successfully)
            }
            .addOnFailureListener { e ->
                _message.value = e.message
            }

        _isLoading.value = false
    }

    private fun saveAcceptHistory(order: ListOrder) {

        val historyData = HistoryBarbershop(
            userId = order.userId,
            nameCustomer = order.name,
            positionCustomer = order.position,
            modelCustomer = order.model,
            addOnCustomer = order.addon,
            price = order.price.toInt(),
            status = "Pesanan Selesai",
            reason = null
        )

        viewModelScope.launch(Dispatchers.IO) {
            history.insertBarHistory(historyData)
        }
    }

    fun declineOrder(order: ListOrder, reason: String){
        _isLoading.value = true
        saveDeclineHistory(order, reason)

        val barberId = getIdBarber()
        val dbRef = database.getDatabase()
        dbRef.child("queue").child(barberId!!).child(order.position.toString()).removeValue()
            .addOnSuccessListener {
                _message.value = context.getString(R.string.order_canceled_send_reason_to_customer)
            }
            .addOnFailureListener { e ->
                _message.value = e.message
            }

        val sendFeedback = dbRef.child("users").child(order.userId).child("message")

        val barberData = BarberDataList.barberDataValue.find { it.id == barberId }

        val feedbackData = mapOf(
            "idBarber" to barberId,
            "nameBarber" to barberData?.name,
            "logoBarber" to barberData?.logo,
            "modelBarber" to order.model,
            "addOnBarber" to order.addon,
            "priceBarber" to order.price.toInt(),
            "status" to "Pesanan Di Batalkan",
            "message" to reason
        )

        sendFeedback.setValue(feedbackData)
            .addOnSuccessListener {
                _message.value = context.getString(R.string.reason_sent_successfully)
            }
            .addOnFailureListener { e ->
                _message.value = e.message
            }

        _isLoading.value = false
    }

    private fun saveDeclineHistory(order: ListOrder, reason:String) {

        val historyData = HistoryBarbershop(
            userId = order.userId,
            nameCustomer = order.name,
            positionCustomer = order.position,
            modelCustomer = order.model,
            addOnCustomer = order.addon,
            price = 0,
            status = "Pesanan Di Batalkan",
            reason = reason
        )

        viewModelScope.launch(Dispatchers.IO) {
            history.insertBarHistory(historyData)
        }
    }

}