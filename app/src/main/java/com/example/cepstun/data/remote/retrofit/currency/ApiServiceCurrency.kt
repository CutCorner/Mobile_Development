package com.example.cepstun.data.remote.retrofit.currency


import com.example.cepstun.data.remote.response.currency.ResponseCurrencyLive
import retrofit2.http.GET

interface ApiServiceCurrency {
    @GET("v1/currencies/usd.json")
    suspend fun getLatestCurrency(): ResponseCurrencyLive
}