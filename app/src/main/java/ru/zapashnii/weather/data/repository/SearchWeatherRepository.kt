package ru.zapashnii.weather.data.repository

import ru.zapashnii.weather.domain.model.GetWeatherRequest
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.domain.network.ISearchWeatherService
import ru.zapashnii.weather.domain.repository.ISearchWeatherRepository
import javax.inject.Inject

/**
 * Получить и закэшировать данные о погоде
 *
 * @property service        сервис для получения данных о погоде
 */
class SearchWeatherRepository @Inject constructor(
    private val service: ISearchWeatherService,
) : ISearchWeatherRepository {

    private var cache: HashMap<GetWeatherRequest, Weather?> = hashMapOf()

    override suspend fun getWeatherByCityName(getWeatherRequest: GetWeatherRequest): Weather? {
        return if (cache[getWeatherRequest] == null) {
            service.getWeatherByCityName(getWeatherRequest).apply {
                cache[getWeatherRequest] = this
            }
        } else {
            cache[getWeatherRequest]
        }
    }

    override fun clear() {
        cache.clear()
    }
}