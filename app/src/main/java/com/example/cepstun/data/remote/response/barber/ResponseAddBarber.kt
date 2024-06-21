package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseAddBarber (
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("result")
    val result: ResultAddBarber? = null
)

data class ResultAddBarber (

    @field:SerializedName("barberId")
    val barberId: String? = null

)