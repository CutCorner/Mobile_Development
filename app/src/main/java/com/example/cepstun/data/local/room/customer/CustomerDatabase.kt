package com.example.cepstun.data.local.room.customer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cepstun.data.local.entity.customer.FavoriteCustomer
import com.example.cepstun.data.local.entity.customer.HistoryCustomer
import com.example.cepstun.data.local.entity.customer.NotificationCustomer

@Database(
    entities = [HistoryCustomer::class, FavoriteCustomer::class, NotificationCustomer::class], version = 1, exportSchema = false
)
abstract class CustomerDatabase : RoomDatabase() {
    abstract fun historyCustomerDao(): HistoryCustomerDao
    abstract fun favoriteCustomerDao(): FavoriteCustomerDao
    abstract fun notificationCustomerDao(): NotificationCustomerDao

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