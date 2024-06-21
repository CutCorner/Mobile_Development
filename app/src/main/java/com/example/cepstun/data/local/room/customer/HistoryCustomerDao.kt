package com.example.cepstun.data.local.room.customer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cepstun.data.local.entity.customer.HistoryCustomer

@Dao
interface HistoryCustomerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertHistoryCustomer(history: HistoryCustomer)

    @Query("SELECT * FROM HistoryCustomer")
    fun getHistoryCustomer(): LiveData<List<HistoryCustomer>>

    @Query("DELETE FROM HistoryCustomer")
    fun deleteHistoryCustomer()

    @Query("DELETE FROM HistoryCustomer WHERE id = :id")
    fun deleteHistoryCustomerById(id: Int)
}