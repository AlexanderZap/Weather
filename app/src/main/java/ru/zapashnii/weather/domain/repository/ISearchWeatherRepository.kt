package ru.zapashnii.weather.domain.repository

import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.domain.repository.base.ICleanableRepository

/** Репозиторий получения данных о погоде */
interface ISearchWeatherRepository : ICleanableRepository {

    /**
     * Получить данные о погоде по названии города
     *
     * @param cityName      название города
     * @return              список погоды [Weather]
     */
    suspend fun getWeatherByCityName(cityName: String): Weather?
}