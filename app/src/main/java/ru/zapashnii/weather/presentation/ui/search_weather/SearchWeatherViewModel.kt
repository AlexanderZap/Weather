package ru.zapashnii.weather.presentation.ui.search_weather

import androidx.annotation.MainThread
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.zapashnii.weather.R
import ru.zapashnii.weather.const.METRIC
import ru.zapashnii.weather.const.RU
import ru.zapashnii.weather.domain.interactors.weather_by_city_name.IWeatherByCityNameUseCase
import ru.zapashnii.weather.domain.model.GetWeatherRequest
import ru.zapashnii.weather.domain.model.ListItemField
import ru.zapashnii.weather.domain.model.ItemListParams
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.navigation.ViewRouter

class SearchWeatherViewModel(
    private val cityName: String,
    private val viewRouter: ViewRouter,
    private val weatherByCityNameUseCase: IWeatherByCityNameUseCase,
) : ViewModel() {

    /** Погода */
    private var _weather = MutableLiveData<Weather>()
    var weather: LiveData<Weather> = _weather

    /** Url изображения погодных условий */
    private var _imageUrl = MutableLiveData<String>()
    var imageUrl: LiveData<String> = _imageUrl

    /** Url изображения погодных условий */
    private var _tvCity = MutableLiveData("")
    var tvCity: LiveData<String> = _tvCity

    /** Url изображения погодных условий */
    private var _tvCountry = MutableLiveData("")
    var tvCountry: LiveData<String> = _tvCountry

    /** Описание погоды */
    private var _tvDescription = MutableLiveData("")
    var tvDescription: LiveData<String> = _tvDescription

    /** Температура */
    private var _tvTemp = MutableLiveData("")
    var tvTemp: LiveData<String> = _tvTemp

    /** Ощущается как */
    private var _tvFeelsLike = MutableLiveData("")
    var tvFeelsLike: LiveData<String> = _tvFeelsLike

    /** Загрузить всю необходимую информацию */
    @MainThread
    fun loadData() {
        viewModelScope.launch {
            getWeatherByCityName(
                getWeatherRequest = GetWeatherRequest(
                    name = cityName,
                    units = METRIC,
                    lang = RU
                )
            )
        }
    }

    /**
     * Получить погоду
     *
     * @param getWeatherRequest Параметры для получения информации о погоде
     */
    private suspend fun getWeatherByCityName(getWeatherRequest: GetWeatherRequest) {
        viewRouter.showProgressAsync()

        _weather.value = weatherByCityNameUseCase.getWeatherByCityName(
            getWeatherRequest = getWeatherRequest
        )

        _imageUrl.value =
            "https://openweathermap.org/img/wn/${_weather.value?.weather?.get(0)?.icon}@2x.png"

        _tvCity.value = _weather.value?.name
        _tvCountry.value = when (_weather.value?.sys?.country) {
            "ru", "RU" -> "Россия"
            "us", "US" -> "Великобритания"
            else -> ""
        }

        _tvTemp.value = "${_weather.value?.main?.temp} °C"

        _tvFeelsLike.value = "Ощущается как ${_weather.value?.main?.feels_like} °C,"

        _tvDescription.value = _weather.value?.weather?.get(0)?.description

        viewRouter.hideProgressAsync()
    }

    /** Нажатие на выбор города */
    fun onChoiceCityClicked() {
        viewModelScope.launch {
            val city = selectSharingType() ?: return@launch
            getWeatherByCityName(
                getWeatherRequest = GetWeatherRequest(
                    name = city,
                    units = METRIC,
                    lang = RU
                )
            )
        }
    }

    /**
     * Подготавливает параметры для отображения всплывающего меню BottomSheetDialogFragment
     *
     * @return      Возвращает номер выбранного элемента
     */
    private suspend fun selectSharingType(): String? {
        val city1 = ListItemField(
            title = "Москва",
        )

        val city2 = ListItemField(
            title = "Краснодар",
        )

        val items = listOf(city1, city2)

        val params = ItemListParams(
            title = "Города",
            items = items
        )

        return viewRouter.selectItemList(params)?.title
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