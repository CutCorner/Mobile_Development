package com.example.cepstun.data.remote.retrofit.currency

import com.example.cepstun.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfigCurrency {

    companion object {
        fun getApiService(): ApiServiceCurrency {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor).build()
            val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL_CURRENCY)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
            return retrofit.create(ApiServiceCurrency::class.java)
        }
    }
}