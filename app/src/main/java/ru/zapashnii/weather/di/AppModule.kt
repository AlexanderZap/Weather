package ru.zapashnii.weather.di

import dagger.Module
import dagger.Provides
import ru.zapashnii.weather.navigation.ViewRouter
import ru.zapashnii.weather.presentation.start_activity.StartActivityViewModel
import ru.zapashnii.weather.presentation.weather_activity.WeatherActivityViewModel

@Module
class AppModule {

    @Provides
    fun provideStartActivityViewModel(): StartActivityViewModel {
        return StartActivityViewModel()
    }

    @Provides
    fun provideWeatherActivityViewModel(): WeatherActivityViewModel {
        return WeatherActivityViewModel()
    }
}