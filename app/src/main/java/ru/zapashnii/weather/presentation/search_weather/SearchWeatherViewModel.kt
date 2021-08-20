package ru.zapashnii.weather.presentation.search_weather

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.zapashnii.weather.domain.interactors.weather_by_city_name.IWeatherByCityNameUseCase
import ru.zapashnii.weather.navigation.ViewRouter
import javax.inject.Inject

class SearchWeatherViewModel(
    private val viewRouter: ViewRouter,
    private val weatherByCityNameUseCase: IWeatherByCityNameUseCase,
) : ViewModel() {

    /** Загрузить всю необходимую информацию */
    @MainThread
    fun loadData(cityName: String) {
        viewModelScope.launch {
            weatherByCityNameUseCase.getWeatherByCityName(cityName)
        }
    }

    /** Фабрика [SearchWeatherViewModel] */
    class Factory @Inject constructor(
        private val viewRouter: ViewRouter,
        private val weatherByCityNameUseCase: IWeatherByCityNameUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchWeatherViewModel(
                viewRouter = viewRouter,
                weatherByCityNameUseCase = weatherByCityNameUseCase
            ) as T
        }
    }
}