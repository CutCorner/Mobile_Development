package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseDetailBarber (
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("result")
    val result: ResultDetailBarber? = null
)

data class ResultDetailBarber (
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("owner")
    val owner: String? = null,

    @field:SerializedName("barberId")
    val barberId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("img_src")
    val imgSrc: String? = null,

    @field:SerializedName("lat")
    val lat: Double? = null,

    @field:SerializedName("long")
    val long: Double? = null,

    @field:SerializedName("location")
    val location: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("addons")
    val addons: List<AddOn>? = null,

    @field:SerializedName("modelsHairs")
    val modelsHairs: List<ModelHair>? = null,

    @field:SerializedName("thumbnailBarber")
    val thumbnailBarber: List<ThumbnailBarber>? = null
)

data class AddOn (
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("barberId")
    val barberId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("price")
    val price: Double? = null
)

data class ModelHair (
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("barberId")
    val barberId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("price")
    val price: Double? = null,

    @field:SerializedName("img_src")
    val imgSrc: String? = null
)

data class ThumbnailBarber (
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("barberId")
    val barberId: String? = null,

    @field:SerializedName("img_src")
    val imgSrc: String? = null
)