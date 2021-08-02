package ru.zapashnii.weather.di

import dagger.Component
import ru.zapashnii.weather.navigation.ViewRouter
import ru.zapashnii.weather.presentation.start_activity.StartActivity
import ru.zapashnii.weather.presentation.weather_activity.WeatherActivity
import javax.inject.Singleton

/** Граф зависимостей */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface ApplicationComponent {
    fun inject(activity: StartActivity)
    fun inject(activity: WeatherActivity)
}