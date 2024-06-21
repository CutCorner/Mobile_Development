package com.example.cepstun.data

import androidx.lifecycle.LiveData
import com.example.cepstun.data.local.entity.barbershop.HistoryBarbershop
import com.example.cepstun.data.local.entity.customer.HistoryCustomer
import com.example.cepstun.data.local.room.barbershop.HistoryBarbershopDao
import com.example.cepstun.data.local.room.customer.HistoryCustomerDao

class RepositoryHistory(
    private val histCusDao: HistoryCustomerDao,
    private val histBarDao: HistoryBarbershopDao
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

    fun deleteCusHistoryById(id: Int) {
        histCusDao.deleteHistoryCustomerById(id)
    }




    // Barber
    /*
                                    History Barber
     */
    fun getBarHistory(): LiveData<List<HistoryBarbershop>> = histBarDao.getHistoryBarbershop()

    suspend fun insertBarHistory(historyBarbershop: HistoryBarbershop) {
        histBarDao.insertHistoryBarbershop(historyBarbershop)
    }

    suspend fun deleteBarHistory() {
        histBarDao.deleteHistoryBarbershop()
    }

    fun deleteBarHistoryById(id: Int) {
        histBarDao.deleteHistoryBarbershopById(id)
    }


    companion object {
        @Volatile
        private var INSTANCE: RepositoryHistory? = null

        fun getInstance(
            histCusDao: HistoryCustomerDao,
            histBarDao: HistoryBarbershopDao
        ): RepositoryHistory {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryHistory(histCusDao, histBarDao)
                INSTANCE = instance
                instance
            }
        }
    }
}