package com.estg.ipp.fitnessapp.Locations.GeopifyAPI

import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.DataClasses.Data
import com.estg.ipp.fitnessapp.Locations.GeopifyAPI.dataclasses_route.DataRoute
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface GeopifyAPI {

    @GET("/v2/places")
    fun getPlaces(@Query("categories")cats :String,@Query("filter")area :String,
                  @Query("bias")bias:String,
                  @Query("limit")limit :String): Call<Data>

    @GET("/v1/routing")
    fun getRoute(@Query("waypoints")waypoints :String,@Query("mode")mode: String = "walk"): Call<DataRoute>


    companion object{
        private var BASE_URL = "https://api.geoapify.com"
        private var API_KEY = "b254b37f33e44841a99cf3b6f69c82d5"

        fun create(): GeopifyAPI{
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

            val client: OkHttpClient.Builder = OkHttpClient().newBuilder()

            client.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url
                val url = originalHttpUrl.newBuilder().addQueryParameter("apiKey", API_KEY).build()
                request.url(url)

                return@addInterceptor chain.proceed(request.build())
            }

            client.addInterceptor(interceptor)

            val retrofit: Retrofit =
                Retrofit.Builder().baseUrl(BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(GeopifyAPI::class.java)
        }
    }
}