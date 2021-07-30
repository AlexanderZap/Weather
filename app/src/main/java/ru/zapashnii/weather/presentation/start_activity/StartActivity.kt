package ru.zapashnii.weather.presentation.start_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ru.zapashnii.weather.R
import ru.zapashnii.weather.databinding.StartActivityBinding
import ru.zapashnii.weather.utils.appComponent
import javax.inject.Inject

/** Стартовая Activity */
class StartActivity : AppCompatActivity() {

    private lateinit var binding: StartActivityBinding

    @Inject
    lateinit var startActivityViewModel: StartActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.start_activity)
        binding.lifecycleOwner = this

        appComponent.inject(this)

        binding.viewModel = startActivityViewModel

    }
}