package ru.zapashnii.weather.utils

import android.content.Context
import ru.zapashnii.weather.di.ApplicationComponent
import ru.zapashnii.weather.di.MainApp

/**
 * Специальное расширение для получения AppComponent в любом месте, где у вас есть доступ к Context.
 * Позволяет избегать статического хранения ссылки на ваш [Application] класс
 */
val Context.appComponent: ApplicationComponent
    get() = when (this) {
        is MainApp -> applicationComponent
        else -> applicationContext.appComponent
    }