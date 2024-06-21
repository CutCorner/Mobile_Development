package com.example.cepstun.data.local.room.barbershop

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cepstun.data.local.entity.barbershop.HistoryBarbershop


@Database(
    entities = [HistoryBarbershop::class], version = 1, exportSchema = false
)
abstract class BarbershopDatabase : RoomDatabase() {

    abstract fun historyBarbershopDao(): HistoryBarbershopDao

    companion object {
        @Volatile
        private var instance: BarbershopDatabase? = null
        fun getInstance(context: Context): BarbershopDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, BarbershopDatabase::class.java, "BarbershopHistory.db"
            ).build()
        }
    }

}