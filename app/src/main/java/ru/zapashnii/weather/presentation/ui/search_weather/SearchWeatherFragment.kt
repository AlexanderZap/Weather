package ru.zapashnii.weather.presentation.ui.search_weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.zapashnii.weather.R
import ru.zapashnii.weather.databinding.SearchWeatherFragmentBinding
import ru.zapashnii.weather.utils.appComponent
import javax.inject.Inject

/** Фрагмент погаза погоды */
class SearchWeatherFragment : Fragment() {

    companion object {
        fun newInstance(cityName: String): SearchWeatherFragment {
            return SearchWeatherFragment().apply {
                this.cityName = cityName
            }
        }
    }

    private lateinit var cityName: String

    @Inject
    lateinit var factory: SearchWeatherViewModel.IFactory

    private val viewModel: SearchWeatherViewModel by viewModels { factory.create(cityName) }

    override fun onCreate(savedInstanceState: Bundle?) {
        requireContext().appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.search_weather_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadData()
        setHasOptionsMenu(true)

        /* Fragment + Jetpack compose
        view.findViewById<ComposeView>(R.id.container).setContent {
            KubanKreditAppTheme {
                val cvvCard by viewModel.cvvCard.observeAsState()
                val requisitesCard by viewModel.requisitesCard.observeAsState()
                Вызываем Compose fun
                Form(
                    numberCard = numberCard,
                    cvvCard = cvvCard,
                    requisitesCard = requisitesCard,
                    onBackClick = viewModel::back,
                )
            }
        } */
    }
}