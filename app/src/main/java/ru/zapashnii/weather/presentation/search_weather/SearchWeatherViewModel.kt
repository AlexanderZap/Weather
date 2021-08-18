package ru.zapashnii.weather.presentation.search_weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.zapashnii.weather.navigation.ViewRouter
import javax.inject.Inject

class SearchWeatherViewModel(
    private val viewRouter: ViewRouter,
) : ViewModel() {

    /** Фабрика [SearchWeatherViewModel] */
    class Factory @Inject constructor(
        private val viewRouter: ViewRouter,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchWeatherViewModel(
                viewRouter = viewRouter,
            ) as T
        }
    }
}