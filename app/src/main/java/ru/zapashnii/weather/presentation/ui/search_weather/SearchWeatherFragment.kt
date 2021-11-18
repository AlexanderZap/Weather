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

/*  Способы привязать viewModel к Fragment
    @Inject
    lateinit var factory: SearchWeatherViewModel.IFactory

    private val viewModel: SearchWeatherViewModel by viewModels { factory.create(cityName) }
    и теперь можно вызывать методы viewModel
    viewModel.loadData()

    @Inject
    lateinit var factory: SearchWeatherViewModel.Factory

    private val viewModel: SearchWeatherViewModel by viewModels { factory }
    и теперь можно вызывать методы viewModel
    viewModel.loadData()
    */

    @Inject
    lateinit var viewModelFactory: SearchWeatherViewModel.Factory

    private var binding: SearchWeatherFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requireContext().appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.search_weather_fragment, container, false)

        binding = DataBindingUtil.bind(view)

        binding?.viewModel =
            ViewModelProvider(this, viewModelFactory).get(SearchWeatherViewModel::class.java)
        binding?.lifecycleOwner = viewLifecycleOwner

        return binding?.root

        // Fragment + Jetpack compose
        // return inflater.inflate(R.layout.search_weather_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.viewModel?.loadData(cityName)
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
        }

         */
    }
}