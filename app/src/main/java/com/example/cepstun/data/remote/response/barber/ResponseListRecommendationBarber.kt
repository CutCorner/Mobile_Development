package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseListRecommendationBarber(
    @field:SerializedName("Rating")
    val rating: Double,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("owner")
    val owner: String? = "abc",

    @field:SerializedName("barberId")
    val barberId: String? = "abc",

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("img_src")
    val imgSrc: String,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("long")
    val lon: Double,

    @field:SerializedName("location")
    val location: String,

    @field:SerializedName("thumbnailBarber")
    val thumbnailBarber: List<ThumbnailBarberSearch>? = null

)