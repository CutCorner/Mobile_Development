package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseSearchBarber(
    @field:SerializedName("status")
    val status: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("result")
    val result: Result
)

data class Result(
    @field:SerializedName("data")
    val data: List<Data>,

    @field:SerializedName("totalData")
    val totalData: Int,

    @field:SerializedName("page")
    val page: Int,

    @field:SerializedName("hasNext")
    val hasNext: Boolean
)

data class Data(
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

    @field:SerializedName("thumbnailBarber")
    val thumbnailBarber: List<ThumbnailBarberSearch>
)

data class ThumbnailBarberSearch(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("barberId")
    val barberId: String,

    @field:SerializedName("img_src")
    val imgSrc: String
)