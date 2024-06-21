package com.example.cepstun.data.remote.retrofit.barber

import com.example.cepstun.data.remote.dataClass.AddOnRequest
import com.example.cepstun.data.remote.dataClass.DeleteAddOnRequest
import com.example.cepstun.data.remote.dataClass.DeleteModelRequest
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiServiceBarber {

    @POST("api/auth/register")
    suspend fun registerBarber(
        @Body registerRequest: RegisterRequest
    ): ResponseRegisterBarber

    @Multipart
    @POST("api/admin/create_store_barber")
    suspend fun createStoreBarber(
        @Header("Authorization")
        token: String,

        @Part("store_name")
        storeName: RequestBody,

        @Part("lat")
        lat: RequestBody,

        @Part("long")
        long: RequestBody,

        @Part("location")
        location: RequestBody,

        @Part logo: MultipartBody.Part,

        @Part thumbnail: List<MultipartBody.Part>
    ): ResponseAddBarber

    @Multipart
    @POST("api/admin/add_list_model")
    suspend fun addListModel(
        @Header("Authorization")
        token: String,

        @Header("BarberId")
        barberId: String,

        @Part("name")
        name: RequestBody,

        @Part("price")
        price: RequestBody,

        @Part image: MultipartBody.Part
    ): ResponseAddModelBarber

    @POST("api/admin/addon")
    suspend fun addListAddOn(
        @Header("Authorization")
        token: String,

        @Header("BarberId")
        barberId: String,

        @Body addOnRequest: AddOnRequest
    ): ResponseAddAddOn



    @GET("api/user/account")
    suspend fun getAccount(
        @Header("Authorization")
        token: String
    ): ResponseAccountBarber

    @GET("api/user/getDetailsBarber")
    suspend fun getDetailBarber(
        @Query("id")
        id: String
    ): ResponseDetailBarber

    @GET("/api/user/getReviewBarber")
    suspend fun getReviewBarber(
        @Query("id")
        id: String
    ): ResponseReview

    @GET("/recommend")
    suspend fun getRecommendations(
        @Query("lat")
        lat: Double,

        @Query("lon")
        lon: Double,

        @Query("k")
        k: Int
    ): List<ResponseListRecommendationBarber>

    @GET("/api/user/getBarberStore")
    suspend fun searchBarberStore(
        @Query("q")
        query: String,

        @Query("pages")
        pages: Int,

        @Query("limits")
        limits: Int
    ): ResponseSearchBarber



    @DELETE("api/admin/delete_list_model")
    suspend fun deleteModel(
        @Header("Authorization")
        token: String,

        @Header("BarberId")
        barberId: String,

        @Body deleteModelRequest: DeleteModelRequest
    ): ResponseDeleteModel

    @DELETE("api/admin/delete_addon")
    suspend fun deleteAddOn(
        @Header("Authorization")
        token: String,

        @Header("BarberId")
        barberId: String,

        @Body deleteAddOnRequest: DeleteAddOnRequest
    ): ResponseDeleteAddOn


}