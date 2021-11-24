package ru.zapashnii.weather.presentation.ui.search_weather

import androidx.annotation.MainThread
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.zapashnii.weather.domain.interactors.weather_by_city_name.IWeatherByCityNameUseCase
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.navigation.ViewRouter
import javax.inject.Inject

class SearchWeatherViewModel(
    //private val cityName: String,
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

    /** Нажатие на иконку инфо перенаправляющая на сайт openweathermap */
    fun clickInfo() {
        viewRouter.showLink("https://openweathermap.org")
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
    //при пересоздании экрана может возникнуть ситуация когда параметры передоваемые в loadData() равны null и возможны проблемы
    //поэтому параметры передаем через IFactory с помощью Dagger2
    /** Фабрика [SearchWeatherViewModel] */
    /*class Factory @AssistedInject constructor(
        @Assisted("cityName") private val cityName: String,
        private val viewRouter: ViewRouter,
        private val weatherByCityNameUseCase: IWeatherByCityNameUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchWeatherViewModel(
                cityName = cityName,
                viewRouter = viewRouter,
                weatherByCityNameUseCase = weatherByCityNameUseCase
            ) as T
        }
    }*/

    /** Фабрика создания [SearchWeatherViewModel.Factory] генерируется даггером */
    @AssistedFactory
    interface IFactory {
        /**
         * Созать фабрику [SearchWeatherViewModel.Factory]
         * @param cityName    Название города
         * @return            фабрика [SearchWeatherViewModel.Factory]
         */
        fun create(
            @Assisted("cityName") cityName: String,
        ): Factory
    }
}