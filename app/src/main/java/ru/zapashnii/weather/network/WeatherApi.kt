package ru.zapashnii.weather.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.zapashnii.weather.BuildConfig
import ru.zapashnii.weather.domain.model.Weather

interface WeatherApi {

    /**
     * Получить погоду по названию города
     *
     * @param name          название города
     * @param api           уникальный ключ
     * @return              список с погодой
     */
    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("q") name: String,
        @Query("appid") api: String = BuildConfig.API_KEY,
    ): List<Weather>

    /**
     * Получить погоду по id города
     *
     * @param city_id       id города
     * @param api           уникальный ключ
     * @return              список с погодой
     */
    @GET("weather")
    suspend fun getWeatherByCityId(
        @Query("id") city_id: String,
        @Query("appid") api: String = BuildConfig.API_KEY,
    ): List<Weather>
}