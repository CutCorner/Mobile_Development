package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseAddModelBarber (
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,
)