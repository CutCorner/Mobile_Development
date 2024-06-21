package com.example.cepstun.data.local.room.customer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cepstun.data.local.entity.customer.NotificationCustomer

@Dao
interface NotificationCustomerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNotificationCustomer(history: NotificationCustomer)

    @Query("SELECT * FROM NotificationCustomer")
    fun getNotificationCustomer(): LiveData<List<NotificationCustomer>>

    @Query("DELETE FROM NotificationCustomer")
    fun deleteNotificationCustomer()

    @Query("DELETE FROM NotificationCustomer WHERE id = :id")
    fun deleteNotificationCustomerById(id: Int)
}