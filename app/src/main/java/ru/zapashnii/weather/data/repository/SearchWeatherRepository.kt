package ru.zapashnii.weather.data.repository

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

    private var cache: HashMap<String, List<Weather>?> = hashMapOf()

    override suspend fun getWeatherByCityName(cityName: String): List<Weather>? {
        return if (cache[cityName] == null) {
            service.getWeatherByCityName(cityName).apply {
                cache[cityName] = this
            }
        } else {
            cache[cityName]
        }
    }

    override fun clear() {
        cache.clear()
    }
}