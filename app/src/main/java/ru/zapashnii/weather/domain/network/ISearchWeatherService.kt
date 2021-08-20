package ru.zapashnii.weather.domain.network

import ru.zapashnii.weather.domain.model.Weather

/** Сервис получения данных о погоде */
interface ISearchWeatherService {

    /**
     * Получить данные о погоде по названии города
     *
     * @param cityName      название города
     * @return              список погоды [Weather]
     */
    suspend fun getWeatherByCityName(cityName: String): List<Weather>
}