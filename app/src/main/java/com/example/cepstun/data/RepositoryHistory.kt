package com.example.cepstun.data

import androidx.lifecycle.LiveData
import com.example.cepstun.data.local.entity.HistoryCustomer
import com.example.cepstun.data.local.room.HistoryCustomerDao

class RepositoryHistory(
    private val histCusDao: HistoryCustomerDao
) {


    // Customer
    /*
                                    History Customer
     */
    fun getCusHistory(): LiveData<List<HistoryCustomer>> = histCusDao.getHistoryCustomer()

    suspend fun insertCusHistory(historyCustomer: HistoryCustomer) {
        histCusDao.insertHistoryCustomer(historyCustomer)
    }

    suspend fun deleteCusHistory() {
        histCusDao.deleteHistoryCustomer()
    }




    // Barber



    companion object {
        @Volatile
        private var INSTANCE: RepositoryHistory? = null

        fun getInstance(
            histCusDao: HistoryCustomerDao
        ): RepositoryHistory {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryHistory(histCusDao)
                INSTANCE = instance
                instance
            }
        }
    }
}