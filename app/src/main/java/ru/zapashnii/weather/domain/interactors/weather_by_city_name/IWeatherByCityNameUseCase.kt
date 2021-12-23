package ru.zapashnii.weather.domain.interactors.weather_by_city_name

import ru.zapashnii.weather.domain.interactors.base.IDispatchUseCase
import ru.zapashnii.weather.domain.model.Weather

/** UseCase для получения информации о погоде */
interface IWeatherByCityNameUseCase : IDispatchUseCase {

    /**
     * Получить данные о погоде по названии города
     *
     * @param cityName      название города
     * @return              список погоды [Weather]
     */
    suspend fun getWeatherByCityName(cityName: String): Weather?
}