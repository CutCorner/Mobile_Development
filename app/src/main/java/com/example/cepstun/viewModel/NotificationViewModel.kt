package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.RepositoryNotification
import com.example.cepstun.data.local.entity.customer.NotificationCustomer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notification: RepositoryNotification,
    application: Application
) :
    AndroidViewModel(application) {

    fun getNotification(): LiveData<List<NotificationCustomer>> = notification.getNotificationCus()

    fun deleteNotification(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            notification.deleteNotificationCustomerById(id)
        }
    }

}