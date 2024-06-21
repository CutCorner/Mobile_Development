package com.example.cepstun.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.local.AddOn
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.Model
import kotlinx.coroutines.launch

class ChooseModelViewModel(
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application) {

    private val _models = MutableLiveData<List<Model>>()
    val models: LiveData<List<Model>> = _models

    fun getModels(barberId: String){
        // get API
//        viewModelScope.launch {
//            val response = barberApi.getDetailBarberShop(barberId)
//            if (!response.result?.modelsHairs.isNullOrEmpty()){
//                _models.value = response.result?.modelsHairs?.map {
//                    Model(
//                        id = it.id.toString(),
//                        name = it.name.toString(),
//                        price = it.price ?: 0.0,
//                        image = it.imgSrc.toString()
//                    )
//                }
//            }
//        }

        // get List dummy
        BarberDataList.barberDataValue.filter {it.id == barberId}.map {
            _models.value = it.model
        }
    }

}