package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.Rating
import kotlinx.coroutines.launch

class RatingViewModel (
    private val barberApi: RepositoryBarberApi,
application: Application
) : AndroidViewModel(application) {

    private val _listRating = MutableLiveData<List<Rating>>()
    val listRating: LiveData<List<Rating>> = _listRating

    fun getRating(barberId: String) {
        // get API
//        viewModelScope.launch {
//            val response = barberApi.getReviewBarber(barberId)
//            if (!response.result.isNullOrEmpty()){
//                _listRating.value = response.result.map {
//                    Rating(
//                        id = it.id_order,
//                        name = it.user,
//                        image = it.user_picture.toString(),
//                        review = it.comment,
//                        ratingScore = it.rating,
//                        date = "",
//                        model = it.model,
//                        addOns = it.addon
//                    )
//                }
//            }
//        }

        // get List dummy
        BarberDataList.barberDataValue.filter {it.id == barberId}.map {
            _listRating.value = it.rating
        }
    }
}
