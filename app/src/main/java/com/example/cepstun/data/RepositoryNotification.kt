package com.example.cepstun.data

import androidx.lifecycle.LiveData
import com.example.cepstun.data.local.entity.customer.NotificationCustomer
import com.example.cepstun.data.local.room.customer.NotificationCustomerDao

class RepositoryNotification(
    private val notCusDao: NotificationCustomerDao
) {

    fun getNotificationCus(): LiveData<List<NotificationCustomer>> = notCusDao.getNotificationCustomer()

    suspend fun insertNotificationHistory(notificationCustomer: NotificationCustomer) {
        notCusDao.insertNotificationCustomer(notificationCustomer)
    }

    suspend fun deleteNotificationCustomer() {
        notCusDao.deleteNotificationCustomer()
    }

    fun deleteNotificationCustomerById(id: Int) {
        notCusDao.deleteNotificationCustomerById(id)
    }


    companion object {
        @Volatile
        private var INSTANCE: RepositoryNotification? = null

        fun getInstance(
            notCusDao: NotificationCustomerDao
        ): RepositoryNotification {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryNotification(notCusDao)
                INSTANCE = instance
                instance
            }
        }
    }
}