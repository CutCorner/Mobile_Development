package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseDeleteModel (
    @field:SerializedName("status")
    val status: Boolean,

    @field:SerializedName("message")
    val message: String
)