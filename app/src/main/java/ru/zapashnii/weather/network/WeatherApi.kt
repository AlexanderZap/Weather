package ru.zapashnii.weather.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.zapashnii.weather.BuildConfig
import ru.zapashnii.weather.domain.Weather

interface WeatherApi {

    @GET("weather?q={name}&appid={api}")
    suspend fun getWeatherByCity(
        @Query("name") name: String,
        @Query("api")  api: String = BuildConfig.API_KEY,
    ): List<Weather>
}