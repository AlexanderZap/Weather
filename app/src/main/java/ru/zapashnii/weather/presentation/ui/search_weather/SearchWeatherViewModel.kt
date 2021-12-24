package ru.zapashnii.weather.presentation.ui.search_weather

import androidx.annotation.MainThread
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.zapashnii.weather.const.METRIC
import ru.zapashnii.weather.const.RU
import ru.zapashnii.weather.domain.interactors.weather_by_city_name.IWeatherByCityNameUseCase
import ru.zapashnii.weather.domain.model.GetWeatherRequest
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.navigation.ViewRouter

class SearchWeatherViewModel(
    private val cityName: String,
    private val viewRouter: ViewRouter,
    private val weatherByCityNameUseCase: IWeatherByCityNameUseCase,
) : ViewModel() {

    /** Погода */
    private var _listWeather = MutableLiveData<Weather>()
    var listWeather: LiveData<Weather> = _listWeather

    /** Url изображения погодных условий */
    private var _imageUrl = MutableLiveData<String>()
    var imageUrl: LiveData<String> = _imageUrl

    /** Url изображения погодных условий */
    private var _tvCity = MutableLiveData("")
    var tvCity: LiveData<String> = _tvCity

    /** Url изображения погодных условий */
    private var _tvCountry = MutableLiveData("")
    var tvCountry: LiveData<String> = _tvCountry

    /** Загрузить всю необходимую информацию */
    @MainThread
    fun loadData() {
        viewModelScope.launch {
            viewRouter.showProgressAsync()
            _listWeather.value = weatherByCityNameUseCase.getWeatherByCityName(
                getWeatherRequest = GetWeatherRequest(
                    name = cityName,
                    units = METRIC,
                    lang = RU
                )
            )

            _imageUrl.value =
                "https://openweathermap.org/img/wn/${_listWeather.value?.weather?.get(0)?.icon}@2x.png"

            _tvCity.value = _listWeather.value?.name
            _tvCountry.value = when (_listWeather.value?.sys?.country) {
                "ru", "RU" -> "Россия"
                "us", "US" -> "Великобритания"
                else -> ""
            }

            viewRouter.hideProgressAsync()
        }
    }

    /** Нажатие на иконку инфо перенаправляющая на сайт openweathermap */
    fun clickInfo() {
        viewRouter.showLink("https://openweathermap.org")
    }

    /** Фабрика [SearchWeatherViewModel] */
    class Factory @AssistedInject constructor(
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
    }

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