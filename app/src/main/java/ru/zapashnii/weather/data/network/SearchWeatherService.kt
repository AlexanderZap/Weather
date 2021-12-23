package ru.zapashnii.weather.data.network

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
     * @param cityName          название города
     * @return                  список с погодой [Weather]
     */
    override suspend fun getWeatherByCityName(cityName: String): Weather {
        return api.getWeatherByCityName(cityName, null, null)
    }
}