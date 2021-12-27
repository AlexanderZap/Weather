package ru.zapashnii.weather.presentation.ui.start_fragment

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.domain.model.ItemListParams
import ru.zapashnii.weather.domain.model.ListItemField
import ru.zapashnii.weather.navigation.ViewRouter
import javax.inject.Inject

class StartViewModel(
    private val viewRouter: ViewRouter,
) : ViewModel() {

    /** Метка(Флаг), который говорит Fragment получить местоположение */
    private var _isGetLastLocation = MutableLiveData(false)
    var isGetLastLocation: LiveData<Boolean> = _isGetLastLocation

    /** Url изображения погодных условий */
    private var _tvCity = MutableLiveData("Выберите город")
    var tvCity: LiveData<String> = _tvCity

    /** Нажатие на кнопку поиск погоды по GPS */
    fun clickFindByGPS() {
        _isGetLastLocation.value = true
    }

    /** Нажатие на выбор города */
    fun clickFindByCity() {
        viewModelScope.launch {
            val city = selectSharingType() ?: return@launch
            openWeatherByCity(city)
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
        Toast.makeText(MainApp.instance.applicationContext, "включите GPS пожалуйста", LENGTH_SHORT).show()
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