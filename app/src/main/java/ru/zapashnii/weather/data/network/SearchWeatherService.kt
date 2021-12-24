package ru.zapashnii.weather.data.network

import ru.zapashnii.weather.domain.model.GetWeatherRequest
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.domain.network.ISearchWeatherService
import ru.zapashnii.weather.network.WeatherApi
import javax.inject.Inject

/**
 * Сервис для получения данных о погоде
 *
 * @property api            интерфейс с договорами(контрактами) получения данных с сервера
 */
class SearchWeatherService @Inject constructor(
    private val api: WeatherApi,
) : ISearchWeatherService {

    /**
     * Получить данные о погоде
     *
     * @param getWeatherRequest     параметры: название города, едницы измерения температуры, язык
     * @return                      список погоды [Weather]
     */
    override suspend fun getWeatherByCityName(getWeatherRequest: GetWeatherRequest): Weather {
        return api.getWeatherByCityName(
            name = getWeatherRequest.name,
            units = getWeatherRequest.units,
            lang = getWeatherRequest.lang
        )
    }
}