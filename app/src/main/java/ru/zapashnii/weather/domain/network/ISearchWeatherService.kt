package ru.zapashnii.weather.domain.network

import ru.zapashnii.weather.domain.model.GetWeatherRequest
import ru.zapashnii.weather.domain.model.Weather

/** Сервис получения данных о погоде */
interface ISearchWeatherService {

    /**
     * Получить данные о погоде по названии города
     *
     * @param getWeatherRequest     параметры: название города, едницы измерения температуры, язык
     * @return                      список погоды [Weather]
     */
    suspend fun getWeatherByCityName(getWeatherRequest: GetWeatherRequest): Weather
}