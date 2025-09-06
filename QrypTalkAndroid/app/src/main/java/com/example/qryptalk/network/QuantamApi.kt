package com.example.qryptalk.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuantumApi {
    @GET("quantum/generate-keys")
    fun getKeys(@Query("n") n: Int): Call<KeysResponse>
}

data class KeysResponse(
    val status: String,
    val keys: List<Int>
)
