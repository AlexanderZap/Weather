package ru.zapashnii.weather.presentation.weather_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.zapashnii.weather.R
import ru.zapashnii.weather.presentation.start_activity.StartActivityViewModel
import javax.inject.Inject

/** Activity погаза погоды */
class WeatherActivity : AppCompatActivity() {

    @Inject
    lateinit var weatherActivityViewModel: WeatherActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
    }
}