package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.RepositoryFavorite
import com.example.cepstun.data.local.entity.customer.FavoriteCustomer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favorite: RepositoryFavorite,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    fun getFavBarber(): LiveData<List<FavoriteCustomer>> = favorite.getCusFavorite()

    fun deleteCusHistory(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            favorite.deleteFavoriteById(id)
        }
    }

}