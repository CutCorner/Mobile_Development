package com.example.cepstun.data.remote.response.currency

import com.google.gson.annotations.SerializedName

data class ResponseCurrencyLive(
	@field:SerializedName("usd")
	val usd: Usd
)

data class Usd(

	@field:SerializedName("idr")
	val idr: Float
)