package com.example.cepstun.data.local.room.barbershop

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cepstun.data.local.entity.barbershop.HistoryBarbershop

@Dao
interface HistoryBarbershopDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertHistoryBarbershop(history: HistoryBarbershop)

    @Query("SELECT * FROM HistoryBarbershop")
    fun getHistoryBarbershop(): LiveData<List<HistoryBarbershop>>

    @Query("DELETE FROM HistoryBarbershop")
    fun deleteHistoryBarbershop()

    @Query("DELETE FROM HistoryBarbershop WHERE id = :id")
    fun deleteHistoryBarbershopById(id: Int)
}