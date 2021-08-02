package ru.zapashnii.weather.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.zapashnii.weather.BuildConfig
import ru.zapashnii.weather.domain.Weather

interface WeatherApi {

    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("q")     name: String,
        @Query("appid") api: String = BuildConfig.API_KEY,
    ): List<Weather>

    @GET("weather")
    suspend fun getWeatherByCityId(
        @Query("id")    city_id: String,
        @Query("appid") api: String = BuildConfig.API_KEY,
    ): List<Weather>
}