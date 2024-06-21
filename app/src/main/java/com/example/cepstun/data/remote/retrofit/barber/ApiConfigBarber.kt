package com.example.cepstun.data.remote.retrofit.barber

import com.example.cepstun.BuildConfig
import com.example.cepstun.data.remote.retrofit.barber.ApiServiceBarber
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfigBarber {

    companion object {
        fun getApiService(): ApiServiceBarber {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor).build()
            val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL_BARBER)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
            return retrofit.create(ApiServiceBarber::class.java)
        }
    }
}