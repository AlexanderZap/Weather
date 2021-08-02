package ru.zapashnii.weather.presentation.weather_activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.zapashnii.weather.network.WeatherApi
import javax.inject.Inject

/** ViewModel для Activity погаза погоды */
class WeatherActivityViewModel @Inject constructor(
    private val weatherApi: WeatherApi,
) : ViewModel() {

    /**  */
    private var _endDate = MutableLiveData<String>().apply { value = "start" }
    var endDate: LiveData<String> = _endDate

    /**
     * TODO
     *
     */
    fun getWeatherByCity() {
        _endDate.value = "click"
        viewModelScope.launch {
            try {
                 weatherApi.getWeatherByCity(
                    name = "Krasnodar"
                )
                _endDate.value = "Successful"
            } catch (e: Exception) {
                Log.e("ERROR", "Exception during request -> ${e.localizedMessage}")
            }
        }
    }
}