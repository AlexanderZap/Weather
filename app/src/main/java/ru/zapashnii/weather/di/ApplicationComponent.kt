package ru.zapashnii.weather.di

import dagger.Component
import ru.zapashnii.weather.presentation.base_activity.BaseActivity
import javax.inject.Singleton

/** Граф зависимостей */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface ApplicationComponent {
    fun inject(baseActivity: BaseActivity)
}