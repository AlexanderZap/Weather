package ru.zapashnii.weather.di

import dagger.Component
import ru.zapashnii.weather.presentation.ui.base_activity.BaseActivity
import ru.zapashnii.weather.presentation.ui.search_weather.SearchWeatherFragment
import ru.zapashnii.weather.presentation.ui.start_fragment.StartFragment
import javax.inject.Singleton

/** Граф зависимостей */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, AppBindsModule::class])
interface ApplicationComponent {
    fun inject(baseActivity: BaseActivity)
    fun inject(startFragment: StartFragment)
    fun inject(searchWeatherFragment: SearchWeatherFragment)
}