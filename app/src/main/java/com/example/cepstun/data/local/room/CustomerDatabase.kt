package com.example.cepstun.data.local.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cepstun.data.local.entity.HistoryCustomer

@Database(
    entities = [HistoryCustomer::class], version = 1, exportSchema = false
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//    ]
)
abstract class CustomerDatabase : RoomDatabase() {
    abstract fun historyCustomerDao(): HistoryCustomerDao

    companion object {
        @Volatile
        private var instance: CustomerDatabase? = null
        fun getInstance(context: Context): CustomerDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, CustomerDatabase::class.java, "CustomerHistory.db"
            ).build()
        }
    }
}