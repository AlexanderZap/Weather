package ru.zapashnii.weather.presentation.ui.search_weather

import androidx.annotation.MainThread
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.zapashnii.weather.domain.interactors.weather_by_city_name.IWeatherByCityNameUseCase
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.navigation.ViewRouter
import javax.inject.Inject

class SearchWeatherViewModel(
    private val viewRouter: ViewRouter,
    private val weatherByCityNameUseCase: IWeatherByCityNameUseCase,
) : ViewModel() {

    /** Список Погоды */
    private var _listWeather = MutableLiveData<List<Weather>>(emptyList())
    var listWeather: LiveData<List<Weather>> = _listWeather

    /** Загрузить всю необходимую информацию */
    @MainThread
    fun loadData(cityName: String) {
        viewModelScope.launch {
            _listWeather.value = weatherByCityNameUseCase.getWeatherByCityName(cityName)
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