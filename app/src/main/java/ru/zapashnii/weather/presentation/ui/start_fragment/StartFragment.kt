package ru.zapashnii.weather.presentation.ui.start_fragment

import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import ru.zapashnii.weather.R
import ru.zapashnii.weather.databinding.StartFragmentBinding
import ru.zapashnii.weather.utils.Utils
import ru.zapashnii.weather.utils.appComponent
import javax.inject.Inject

/** Фрагмент стартового экрана */
class StartFragment : Fragment() {

    companion object {
        fun newInstance() = StartFragment()
    }

    @Inject
    lateinit var viewModelFactory: StartViewModel.Factory

    private var binding: StartFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requireContext().appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.start_fragment, container, false)

        binding = DataBindingUtil.bind(view)

        binding?.viewModel =
            ViewModelProvider(this, viewModelFactory).get(StartViewModel::class.java)
        binding?.lifecycleOwner = viewLifecycleOwner

        binding?.viewModel?.isGetLastLocation?.observe(viewLifecycleOwner, { if (it) getLastLocation() })
        return binding?.root
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /** Получить местоположение */
    private fun getLastLocation() {
        fusedLocationProviderClient =  FusedLocationProviderClient(activity)
        if (Utils.checkPermission()) {
            if (Utils.isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {

                        val locationRequest = LocationRequest.create().apply {
                            interval = 0
                            fastestInterval = 0
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            numUpdates = 1
                        }
                        fusedLocationProviderClient =
                            LocationServices.getFusedLocationProviderClient(activity)

                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )
                    } else {
                        binding?.viewModel?.openWeatherByCity(Utils.getCityName(location.latitude,
                            location.longitude))
                    }
                }
            } else {
                binding?.viewModel?.requestLocationEnabled()
            }
        } else {
            binding?.viewModel?.requestPermission()
        }
    }

    /** Результат получения места положения */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            binding?.viewModel?.openWeatherByCity(Utils.getCityName(lastLocation.latitude,
                lastLocation.longitude))
        }
    }
}