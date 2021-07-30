package ru.zapashnii.weather.di

import dagger.Component
import ru.zapashnii.weather.navigation.ViewRouter
import ru.zapashnii.weather.presentation.start_activity.StartActivity
import ru.zapashnii.weather.presentation.weather_activity.WeatherActivity

/** Граф зависимостей */
@Component(modules = [AppModule::class])
interface ApplicationComponent {
    fun inject(activity: StartActivity)
    fun inject(activity: WeatherActivity)
    fun viewRouter(): ViewRouter
}