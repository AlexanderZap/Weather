package ru.zapashnii.weather.domain.interactors.weather_by_city_name

import ru.zapashnii.weather.domain.interactors.base.UseCase
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.domain.repository.ISearchWeatherRepository
import javax.inject.Inject

/**
 * UseCase для получения данных о погоде
 *
 * @property repository     репозиторий с данными о погоде
 */
class WeatherByCityNameUseCase @Inject constructor(
    private val repository: ISearchWeatherRepository,
) : UseCase<Weather?, String>(), IWeatherByCityNameUseCase {

    override suspend fun run(params: String): Weather? {
        return repository.getWeatherByCityName(params)
    }

    override suspend fun getWeatherByCityName(cityName: String): Weather? {
        return run(cityName)
    }

    override fun dispatch() {
        repository.clear()
    }
}