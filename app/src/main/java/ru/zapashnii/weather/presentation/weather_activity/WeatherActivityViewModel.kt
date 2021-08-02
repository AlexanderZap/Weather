package ru.zapashnii.weather.presentation.weather_activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.zapashnii.weather.network.WeatherApi
import javax.inject.Inject

/** ViewModel для Activity погаза погоды */
class WeatherActivityViewModel @Inject constructor(
    private val weatherApi: WeatherApi,
) : ViewModel() {

    /**  */
    private var _lable = MutableLiveData<String>().apply { value = "start" }
    var lable: LiveData<String> = _lable

    /**
     * TODO
     *
     */
    fun getWeatherByCity() {
        _lable.value = "click"
        viewModelScope.launch {
            try {
                 weatherApi.getWeatherByCityName(
                    name = "Krasnodar"
                )
                _lable.value = "Successful"
            } catch (e: Exception) {
                Log.e("ERROR", "Exception during request -> ${e.localizedMessage}")
            }
        }
    }
}