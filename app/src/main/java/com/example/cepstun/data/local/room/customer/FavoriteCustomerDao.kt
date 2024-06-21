package com.example.cepstun.data.local.room.customer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cepstun.data.local.entity.customer.FavoriteCustomer
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCustomerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFavoriteCustomer(favoriteCustomer: FavoriteCustomer)

    @Query("SELECT * FROM FavoriteCustomer")
    fun getFavoriteCustomers(): LiveData<List<FavoriteCustomer>>

    @Query("DELETE FROM FavoriteCustomer WHERE idBarber = :id")
    fun deleteByIdBarber(id: String)

    @Query("DELETE FROM FavoriteCustomer WHERE id = :id")
    fun deleteByIdFavorite(id: Int)


    @Query("DELETE FROM FavoriteCustomer")
    fun deleteAllFavoriteCustomers()

    @Query("SELECT EXISTS(SELECT * FROM FavoriteCustomer WHERE idBarber = :barberId)")
    fun isFavorite(barberId: String): Flow<Boolean>

}