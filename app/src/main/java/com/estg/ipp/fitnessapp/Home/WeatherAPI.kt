package com.estg.ipp.fitnessapp.Home

import com.estg.ipp.fitnessapp.Home.dataclass.WeatherDataClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("weather")
    fun getWeather(@Query("lat") lat:String , @Query("lon") lon:String) : Call<WeatherDataClass>

    companion object {
        private var BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private var API_KEY = "9d4331273e3d2d60e7c9210ab515766e"

        fun create(): WeatherAPI {
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

            val client: OkHttpClient.Builder = OkHttpClient().newBuilder()

            client.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url
                val url = originalHttpUrl.newBuilder().addQueryParameter(
                    "appid",
                    API_KEY
                ).build()
                request.url(url)

                return@addInterceptor chain.proceed(request.build())
            }

            client.addInterceptor(interceptor)

            val retrofit: Retrofit =
                Retrofit.Builder().baseUrl(BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(WeatherAPI::class.java)
        }
    }
}