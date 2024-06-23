package com.example.cepstun.data

import com.example.cepstun.data.remote.dataClass.AddOnRequest
import com.example.cepstun.data.remote.dataClass.RegisterRequest
import com.example.cepstun.data.remote.response.barber.ResponseAccountBarber
import com.example.cepstun.data.remote.response.barber.ResponseAddAddOn
import com.example.cepstun.data.remote.response.barber.ResponseAddBarber
import com.example.cepstun.data.remote.response.barber.ResponseAddModelBarber
import com.example.cepstun.data.remote.response.barber.ResponseDeleteAddOn
import com.example.cepstun.data.remote.response.barber.ResponseDeleteModel
import com.example.cepstun.data.remote.response.barber.ResponseDetailBarber
import com.example.cepstun.data.remote.response.barber.ResponseListRecommendationBarber
import com.example.cepstun.data.remote.response.barber.ResponseRegisterBarber
import com.example.cepstun.data.remote.response.barber.ResponseReview
import com.example.cepstun.data.remote.response.barber.ResponseSearchBarber
import com.example.cepstun.data.remote.retrofit.barber.ApiServiceBarber
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RepositoryBarberApi private constructor(private val apiService: ApiServiceBarber) {

    suspend fun registerAccountBarber(name: String, uid: String): ResponseRegisterBarber{
        val registerRequest = RegisterRequest(name, uid)
        return apiService.registerBarber(registerRequest)
    }

    suspend fun createBarbershop(token: String, nameBarber: String, lat: Double, lon: Double, location: String, phone: String, logoBarber: File, listImageBarberFiles: List<File>): ResponseAddBarber {
        val storeName = nameBarber.toRequestBody("text/plain".toMediaType())
        val latPart = lat.toString().toRequestBody("text/plain".toMediaType())
        val longPart = lon.toString().toRequestBody("text/plain".toMediaType())
        val locationPart = location.toRequestBody("text/plain".toMediaType())
        val phonePart = phone.toRequestBody("text/plain".toMediaType())
        val logoPart = MultipartBody.Part.createFormData("logo", logoBarber.name, logoBarber.asRequestBody("image/jpg".toMediaTypeOrNull()))
        val thumbnailParts = listImageBarberFiles.map {
            MultipartBody.Part.createFormData("thumbnail", it.name, it.asRequestBody("image/jpg".toMediaTypeOrNull()))
        }

        return apiService.createStoreBarber(token, storeName, latPart, longPart, locationPart, phonePart, logoPart, thumbnailParts)
    }

    suspend fun addModel(barberId: String, token: String, name: String, price: Double, image: File): ResponseAddModelBarber {
        val namePart = name.toRequestBody("text/plain".toMediaType())
        val pricePart = price.toString().toRequestBody("text/plain".toMediaType())
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            image.name,
            image.asRequestBody("image/jpg".toMediaType())
        )

        return apiService.addListModel(token, barberId, namePart, pricePart, imagePart)
    }

    suspend fun addAddOn(idBarber: String, token: String, name: String, price: Double): ResponseAddAddOn{
        val addOnRequest = AddOnRequest(name, price.toString())
        return apiService.addListAddOn(token, idBarber, addOnRequest)
    }

    suspend fun getAccountBarber(token: String): ResponseAccountBarber{
        return apiService.getAccount(token)
    }

    suspend fun getRecommendation(lat: Double, long: Double, k: Int): List<ResponseListRecommendationBarber> {
        return apiService.getRecommendations(lat, long, k)
    }

    suspend fun getDetailBarberShop(id: String): ResponseDetailBarber{
        return apiService.getDetailBarber(id)
    }

    suspend fun getReviewBarber(id: String): ResponseReview{
        return apiService.getReviewBarber(id)
    }

    suspend fun searchBarber(name: String): ResponseSearchBarber{
        return apiService.searchBarberStore(name, 1, 5)
    }

    suspend fun deleteModelBarber(idBarber: String, token: String, name: String): ResponseDeleteModel{
        return apiService.deleteModel(token, idBarber, name)
    }

    suspend fun deleteAddOnBarber(idBarber: String, token: String, name: String): ResponseDeleteAddOn{
        return apiService.deleteAddOn(token, idBarber, name)
    }

    suspend fun updateBarbershop(token: String, idBarber: String, nameBarber: String, lat: Double, lon: Double, location: String, phone: String, logoBarber: File, listImageBarberFiles: List<File>): ResponseAddBarber {
        val storeName = nameBarber.toRequestBody("text/plain".toMediaType())
        val latPart = lat.toString().toRequestBody("text/plain".toMediaType())
        val longPart = lon.toString().toRequestBody("text/plain".toMediaType())
        val locationPart = location.toRequestBody("text/plain".toMediaType())
        val phonePart = phone.toRequestBody("text/plain".toMediaType())
        val logoPart = MultipartBody.Part.createFormData(
            "logo", logoBarber.name, logoBarber.asRequestBody("image/jpg".toMediaTypeOrNull())
        )
        val thumbnailParts = listImageBarberFiles.map {
            MultipartBody.Part.createFormData("thumbnail", it.name, it.asRequestBody("image/jpg".toMediaTypeOrNull()))
        }

        return apiService.updateStoreBarber(token, idBarber, storeName, latPart, longPart, locationPart, phonePart, logoPart, thumbnailParts)
    }

    companion object {
        @Volatile
        private var INSTANCE: RepositoryBarberApi? = null

        fun clearInstance(){
            INSTANCE = null
        }

        fun getInstance(apiService: ApiServiceBarber): RepositoryBarberApi {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryBarberApi(apiService)
                INSTANCE = instance
                instance
            }
        }
    }
}