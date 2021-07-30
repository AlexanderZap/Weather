package ru.zapashnii.weather.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import ru.zapashnii.weather.presentation.weather_activity.WeatherActivity
import javax.inject.Inject
import javax.inject.Singleton

/** Клас навигации по приложению */
//@Singleton
class ViewRouter @Inject constructor() {

    private var currentActivity: AppCompatActivity? = null

    /** Запускает Activity погаза погоды TODO с параметром = название города*/
    fun openWeatherByCity() {
            val intent = Intent(currentActivity, WeatherActivity::class.java)
        currentActivity?.startActivity(intent)
    }

    /** Запускает Activity погаза погоды TODO с параметром = гео локация*/
    fun openWeatherByGPS() {
        val intent = Intent(currentActivity, WeatherActivity::class.java)
        currentActivity?.startActivity(intent)
    }
}