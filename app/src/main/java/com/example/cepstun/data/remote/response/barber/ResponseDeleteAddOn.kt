package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseDeleteAddOn (
    @field:SerializedName("status")
    val status: Boolean,

    @field:SerializedName("message")
    val message: String
)