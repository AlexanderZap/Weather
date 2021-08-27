package ru.zapashnii.weather.presentation.ui.start_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.zapashnii.weather.navigation.ViewRouter
import javax.inject.Inject

class StartViewModel(
    private val viewRouter: ViewRouter,
) : ViewModel() {

    fun clickFindByGPS() {
        //viewRouter.openWeatherByGPS()
    }

    fun clickFindByCity() {
        viewRouter.openWeatherByCity("London")
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