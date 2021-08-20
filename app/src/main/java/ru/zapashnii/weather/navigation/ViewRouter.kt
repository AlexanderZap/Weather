package ru.zapashnii.weather.navigation

import android.content.Intent
import ru.zapashnii.weather.presentation.base_activity.BaseActivity
import javax.inject.Inject
import javax.inject.Singleton

/** Клас навигации по приложению */
@Singleton
class ViewRouter @Inject constructor() {

    private var currentActivity: BaseActivity? = null

    var progressStack: Int = 0

    fun setCurrentActivity(activity: BaseActivity?) {
        currentActivity = activity
    }

    fun removeCurrentActivity(baseActivity: BaseActivity) {
        if (currentActivity == baseActivity) currentActivity = null
    }

    /** Запускает Activity погаза погоды TODO с параметром = название города*/
    fun openWeatherByCity(cityName: String) {
        startWeatherActivity(BaseActivity.SEARCH_WEATHER, cityName)
    }

    private fun startWeatherActivity(type: String, cityName: String) {
        if (currentActivity != null) {
            val intent = Intent(currentActivity, BaseActivity::class.java)
            intent.putExtra(BaseActivity.TYPE_ACTIVITY, type)
            intent.putExtra(BaseActivity.CITY_NAME, cityName)
            currentActivity?.startActivity(intent)
        }
    }

    /** Запускает Activity погаза погоды TODO с параметром = гео локация*/
/*    fun openWeatherByGPS() {
        val intent = Intent(currentActivity, WeatherActivity::class.java)
        currentActivity?.startActivity(intent)
    }*/
}