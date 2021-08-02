package ru.zapashnii.weather.presentation.weather_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import ru.zapashnii.weather.R
import ru.zapashnii.weather.databinding.ActivityWeatherBinding
import ru.zapashnii.weather.databinding.StartActivityBinding
import ru.zapashnii.weather.presentation.start_activity.StartActivityViewModel
import ru.zapashnii.weather.utils.appComponent
import javax.inject.Inject

/** Activity погаза погоды */
class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding

    @Inject
    lateinit var weatherActivityViewModel: WeatherActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather)
        binding.lifecycleOwner = this

        appComponent.inject(this)

        binding.viewModel = weatherActivityViewModel
    }
}