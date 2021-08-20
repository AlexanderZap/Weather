package ru.zapashnii.weather.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import ru.zapashnii.weather.data.network.SearchWeatherService
import ru.zapashnii.weather.data.repository.SearchWeatherRepository
import ru.zapashnii.weather.domain.interactors.weather_by_city_name.IWeatherByCityNameUseCase
import ru.zapashnii.weather.domain.interactors.weather_by_city_name.WeatherByCityNameUseCase
import ru.zapashnii.weather.domain.network.ISearchWeatherService
import ru.zapashnii.weather.domain.repository.ISearchWeatherRepository

/**
 * Dagger модуль для связывания интрефейсов с их реализациями уровня приложения
 */
@Module
interface AppBindsModule {
    /*
     * --------------------------------
     * Бинды для сервисов
     * --------------------------------
     */

    @Binds
    @Reusable
    fun provideSearchWeatherService(service: SearchWeatherService): ISearchWeatherService

    /*
     * --------------------------------
     * Бинды для репозиториев
     * --------------------------------
     */

    @Binds
    @Reusable
    fun provideSearchWeatherRepository(repository: SearchWeatherRepository): ISearchWeatherRepository

    /*
     * --------------------------------
     * Бинды для интеракторов (UseCase)
     * --------------------------------
     */

    @Binds
    @Reusable
    fun provideWeatherByCityNameUseCase(useCase: WeatherByCityNameUseCase): IWeatherByCityNameUseCase
}