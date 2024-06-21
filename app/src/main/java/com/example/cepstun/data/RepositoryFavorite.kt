package com.example.cepstun.data

import androidx.lifecycle.LiveData
import com.example.cepstun.data.local.entity.customer.FavoriteCustomer
import com.example.cepstun.data.local.room.customer.FavoriteCustomerDao
import kotlinx.coroutines.flow.Flow

class RepositoryFavorite(
    private val favCusDao: FavoriteCustomerDao
) {

    fun getCusFavorite(): LiveData<List<FavoriteCustomer>> = favCusDao.getFavoriteCustomers()

    suspend fun insertFavorite(favCus: FavoriteCustomer) {
        favCusDao.addFavoriteCustomer(favCus)
    }

    fun deleteFavorite(idBarber: String) {
        favCusDao.deleteByIdBarber(idBarber)
    }

    suspend fun deleteFavoriteAll() {
        favCusDao.deleteAllFavoriteCustomers()
    }

    fun deleteFavoriteById(id: Int) {
        favCusDao.deleteByIdFavorite(id)
    }

    fun checkFavorite(idBarber: String): Flow<Boolean> = favCusDao.isFavorite(idBarber)

    companion object {
        @Volatile
        private var INSTANCE: RepositoryFavorite? = null

        fun getInstance(
            favCusDao: FavoriteCustomerDao
        ): RepositoryFavorite {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryFavorite(favCusDao)
                INSTANCE = instance
                instance
            }
        }
    }
}