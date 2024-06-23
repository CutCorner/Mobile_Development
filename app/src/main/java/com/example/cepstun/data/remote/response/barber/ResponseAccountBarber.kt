package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseAccountBarber (
    @field:SerializedName("status")
    val status: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("result")
    val result: ResultData
)

data class ResultData (
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("uid")
    val uid: String,

    @field:SerializedName("role")
    val role: String,

    @field:SerializedName("fullName")
    val fullName: String,

    @field:SerializedName("picture")
    val picture: String?,

    @field:SerializedName("store")
    val store: List<StoreData>
)

data class StoreData (
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("owner")
    val owner: String,

    @field:SerializedName("barberId")
    val barberId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("img_src")
    val imgSrc: String,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("long")
    val long: Double,

    @field:SerializedName("location")
    val location: String,

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("model")
    val model: List<ModelAccount>,

    @field:SerializedName("addons")
    val addons: List<AddonsAccount>,

    @field:SerializedName("thumbnail")
    val thumbnail: List<ThumbnailAccount>? = null
)

data class ModelAccount(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("barberId")
    val barberId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("price")
    val price: Double? = null,

    @field:SerializedName("img_src")
    val imgSrc: String? = null
)

data class AddonsAccount(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("barberId")
    val barberId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("price")
    val price: Double? = null,
)

data class ThumbnailAccount (
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("barberId")
    val barberId: String? = null,

    @field:SerializedName("img_src")
    val imgSrc: String? = null
)