package ru.zapashnii.weather.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.zapashnii.weather.BuildConfig
import ru.zapashnii.weather.navigation.ViewRouter
import ru.zapashnii.weather.network.WeatherApi
import ru.zapashnii.weather.presentation.start_activity.StartActivityViewModel
import ru.zapashnii.weather.presentation.weather_activity.WeatherActivityViewModel
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideStartActivityViewModel(
        viewRouter: ViewRouter,
    ): StartActivityViewModel =
        StartActivityViewModel(viewRouter)


    @Provides
    fun provideWeatherActivityViewModel(
        weatherApi: WeatherApi,
    ): WeatherActivityViewModel =
        WeatherActivityViewModel(weatherApi)


    @Provides
    fun provideViewRouter(): ViewRouter =
        ViewRouter()
}