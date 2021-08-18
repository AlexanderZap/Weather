package ru.zapashnii.weather.presentation.start_fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import ru.zapashnii.weather.R
import ru.zapashnii.weather.databinding.StartFragmentBinding
import javax.inject.Inject

/** Фрагмент стартового экрана */
class StartFragment : Fragment() {

    companion object {
        fun newInstance() = StartFragment()
    }

    @Inject
    lateinit var viewModelFactory: StartViewModel.Factory

    private var binding: StartFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.start_fragment, container, false)

        binding = DataBindingUtil.bind(view)

        binding?.viewModel =
            ViewModelProvider(this, viewModelFactory).get(StartViewModel::class.java)
        binding?.lifecycleOwner = viewLifecycleOwner

        return binding?.root
    }
}