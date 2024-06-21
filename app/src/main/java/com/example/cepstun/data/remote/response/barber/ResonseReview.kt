package com.example.cepstun.data.remote.response.barber

import com.google.gson.annotations.SerializedName

data class ResponseReview(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("result")
    val result: List<ReviewResult>?
)

data class ReviewResult(
    @field:SerializedName("id_order")
    val id_order: String,

    @field:SerializedName("model")
    val model: String,

    @field:SerializedName("addon")
    val addon: String,

    @field:SerializedName("comment")
    val comment: String,

    @field:SerializedName("rating")
    val rating: Double,

    @field:SerializedName("user")
    val user: String,

    @field:SerializedName("user_picture")
    val user_picture: String?,

    @field:SerializedName("date")
    val date: String
)