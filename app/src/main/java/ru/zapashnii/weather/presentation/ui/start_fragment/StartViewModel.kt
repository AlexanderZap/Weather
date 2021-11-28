package ru.zapashnii.weather.presentation.ui.start_fragment

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.navigation.ViewRouter
import javax.inject.Inject

class StartViewModel(
    private val viewRouter: ViewRouter,
) : ViewModel() {

    /** Метка(Флаг), который говорит Fragment получить местоположение */
    private var _isGetLastLocation = MutableLiveData(false)
    var isGetLastLocation: LiveData<Boolean> = _isGetLastLocation

    /** Нажатие на кнопку поиск погоды по GPS */
    fun clickFindByGPS() {
        _isGetLastLocation.value = true
    }

    /** Нажатие на кнопку поиск погоды по названию города */
    fun clickFindByCity() {
        openWeatherByCity("Krasnodar")
    }

    /**
     * Метод для навигации и открытия экрана поиска погоды по назвнию города
     *
     * @param city      название города
     */
    fun openWeatherByCity(city: String) {
        viewRouter.openWeatherByCity(city)
    }

    /** Метод для навигации запрос разрешения на получения места положения */
    fun requestPermission() {
        viewRouter.requestPermission()
    }

    /** Показать окно с просьбой включения GPS */
    fun requestLocationEnabled() {
        //TODO вынести в viewRouter
        Toast.makeText(MainApp.instance.applicationContext, "включи GPS pls", LENGTH_SHORT).show()
    }

    /** Фабрика [StartViewModel] */
    class Factory @Inject constructor(
        private val viewRouter: ViewRouter,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return StartViewModel(
                viewRouter = viewRouter,
            ) as T
        }
    }
}