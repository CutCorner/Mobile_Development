package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryFavorite
import com.example.cepstun.data.RepositorySharedPreference
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.Image
import com.example.cepstun.data.local.Model
import com.example.cepstun.data.local.Rating
import com.example.cepstun.data.local.entity.customer.FavoriteCustomer
import com.example.cepstun.data.remote.response.barber.ResultDetailBarber
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarbershopViewModel (
    private val database: RepositoryDatabase,
    private val favorite: RepositoryFavorite,
    setting: RepositorySharedPreference,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    
    private val _message = MutableLiveData<String>()
    val message: MutableLiveData<String> = _message

    private val _allQueue = MutableLiveData<Long>()
    val allQueue: LiveData<Long> = _allQueue

    private val _isAvailable = MutableLiveData<Boolean>()
    val isAvailable: LiveData<Boolean> = _isAvailable

    private val _isOpened = MutableLiveData<Boolean>()
    val isOpened: LiveData<Boolean> = _isOpened

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _barberData = MutableLiveData<BarberData>()
    val barberData: LiveData<BarberData> = _barberData


    fun cekBarberQueueAndOpened(barberId: String){
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            checkFavoriteAvailable(barberId)
        }

        val dbRef = database.getDatabase()

        val barberRef = dbRef.child("users").child(barberId).child("open")

        barberRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    _isOpened.value = dataSnapshot.getValue(Boolean::class.java) ?: false
                } else {
                    _isOpened.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _message.value = error.message
            }
        })

        val barberRef2 = dbRef.child("queue").child(barberId)

        barberRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    _allQueue.value = dataSnapshot.childrenCount
                    _isLoading.value = false
                } else {
                    _allQueue.value = 0
                    _isLoading.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _message.value = error.message
                _isLoading.value = false
            }
        })
    }

    fun addFavorite(fav: FavoriteCustomer){
        viewModelScope.launch(Dispatchers.IO) {
            val isFavorite = checkFavoriteAvailable(fav.idBarber)
            if(isFavorite) {
                favorite.deleteFavorite(fav.idBarber)
                withContext(Dispatchers.Main) {
                    _isAvailable.value = false
                    _message.value =
                        context.getString(R.string.barbershop_successfully_removed_from_favourites)
                }
            }else{
                favorite.insertFavorite(fav)
                withContext(Dispatchers.Main) {
                    _isAvailable.value = true
                    _message.value =
                        context.getString(R.string.barbershop_successfully_added_to_favourites)
                }
            }
        }
    }

    private suspend fun checkFavoriteAvailable(barberId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val isFavorite = favorite.checkFavorite(barberId).first()
            withContext(Dispatchers.Main) {
                _isAvailable.value = isFavorite
            }
            isFavorite
        }
    }

    fun setMapStyle(mMap: GoogleMap) {
        _isLoading.value = true
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplication(), R.raw.map_style))
            if (!success) {
                _message.postValue(context.getString(R.string.style_parsing_failed))
            }
        } catch (exception: Resources.NotFoundException) {
            _message.postValue(context.getString(R.string.can_t_find_style_error, exception.message))
        }
        _isLoading.value = false
    }

    val themeSetting: LiveData<Boolean?> = setting.getThemeSetting().asLiveData()

    fun getDetailBarbershop(barberId: String){
        // get API
//        viewModelScope.launch {
//            val response = barberApi.getDetailBarberShop(barberId)
//            val response2 = barberApi.getReviewBarber(barberId)
//            if (response.result != null && !response2.result.isNullOrEmpty()){
//                response.result.let {
//                    _barberData.value = BarberData(
//                        id = it.id.toString(),
//                        name = it.name.toString(),
//                        logo = it.imgSrc.toString(),
//                        image = it.thumbnailBarber!!.map { thumb ->
//                            Image(
//                                id = thumb.id.toString(),
//                                picture = thumb.imgSrc.toString()
//                            )
//                        },
//                        lat = it.lat ?: 0.0,
//                        lon = it.long ?: 0.0,
//                        location = it.location.toString(),
//                        model = it.modelsHairs!!.map { model->
//                            Model(
//                                id = model.id.toString(),
//                                name = model.name.toString(),
//                                price = model.price ?: 0.0,
//                                image = model.imgSrc.toString()
//                            )
//                        },
//                        rate = response2.result.map {score-> score.rating }.average(),
//                        rating = response2.result.map {rating->
//                            Rating(
//                                id = rating.id_order,
//                                name = rating.user,
//                                image = rating.user_picture ?: "",
//                                ratingScore = rating.rating,
//                                review = rating.comment,
//                                model = rating.model,
//                                addOns = rating.addon,
//                                date = rating.date,
//                            )
//                        }
//                    )
//                }
//            }
//        }

        // get List dummy
        BarberDataList.barberDataValue.filter {it.id == barberId}.map {
            _barberData.value = it
        }
    }
}