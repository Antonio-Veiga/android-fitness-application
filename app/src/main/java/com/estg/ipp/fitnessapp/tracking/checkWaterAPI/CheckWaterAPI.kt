package com.estg.ipp.fitnessapp.tracking.checkWaterAPI

import com.estg.ipp.fitnessapp.tracking.checkWaterAPI.dataclasses.Data
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface CheckWaterAPI {
    @GET("{lat},{lon}")
    fun isOnWater(@Path("lat")lat:String,@Path("lon")lon:String): Call<Data>

    companion object{
        private var BASE_URL = "https://api.onwater.io/api/v1/results/"

        fun create(): CheckWaterAPI {
            val client: OkHttpClient.Builder = OkHttpClient().newBuilder()

            val retrofit: Retrofit =
                Retrofit.Builder().baseUrl(BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(CheckWaterAPI::class.java)
        }
    }
}