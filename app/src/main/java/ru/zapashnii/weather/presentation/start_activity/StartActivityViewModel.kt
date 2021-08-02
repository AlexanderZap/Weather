package ru.zapashnii.weather.presentation.start_activity

import androidx.lifecycle.ViewModel
import ru.zapashnii.weather.navigation.ViewRouter
import javax.inject.Inject

/** ViewModel стартовой Activity*/
class StartActivityViewModel @Inject constructor(
    private val viewRouter: ViewRouter,
) : ViewModel() {

    /**
     * TODO Показать поле для ввода города
     * Открывает Activity показа погоды по введенному городу
     */
    fun clickFindByCity() {
        viewRouter.openWeatherByCity()
    }

    /**
     * TODO запросить разрешение на GPS
     * Открывает Activity показа погоды по GPS
     */
    fun clickFindByGPS() {
        viewRouter.openWeatherByGPS()
    }
}