package ru.zapashnii.weather.presentation.base_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.zapashnii.weather.R
import ru.zapashnii.weather.navigation.ViewRouter
import ru.zapashnii.weather.presentation.search_weather.SearchWeatherFragment
import ru.zapashnii.weather.presentation.start_fragment.StartFragment
import ru.zapashnii.weather.utils.Utils
import javax.inject.Inject

/** Базовый экран на котором будут создоваться/подменяться фрагменты */
class BaseActivity : AppCompatActivity() {

    companion object {
        const val TYPE_ACTIVITY = "TYPE_ACTIVITY"

        const val SEARCH_WEATHER = "SEARCH_WEATHER"
        const val START_FRAGMENT = "START_FRAGMENT"
    }

    @Inject
    lateinit var viewRouter: ViewRouter

    private var fragmentsStack = 0
    private var tag = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        viewRouter.setCurrentActivity(this)

        when (intent.getStringExtra(TYPE_ACTIVITY)) {
            START_FRAGMENT -> addFragment(StartFragment.newInstance())
            SEARCH_WEATHER -> replaceFragment(SearchWeatherFragment.newInstance())
        }
    }

    /** Передаем навигатору viewRouter текущую активность */
    override fun onStart() {
        super.onStart()
        viewRouter.setCurrentActivity(this)
    }

    /** Передаем навигатору viewRouter текущую активность */
    override fun onResume() {
        super.onResume()
        viewRouter.setCurrentActivity(this)
    }

    /** Удаляем у навигатора viewRouter текущую активность */
    override fun onStop() {
        super.onStop()
        viewRouter.removeCurrentActivity(this)
    }

    /**
     * Подменить фрагмент
     *
     * @param fragment
     */
    fun replaceFragment(fragment: Fragment) {

        fragmentsStack++
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, fragment, tag + fragmentsStack)
            .addToBackStack(tag + fragmentsStack)
            .commitAllowingStateLoss()

    }

    /**
     * Создать фрагмент
     *
     * @param fragment
     */
    fun addFragment(fragment: Fragment) {
        Utils.hideSoftKeyboard(this)
        fragmentsStack++
        supportFragmentManager
            .beginTransaction()
            .add(R.id.mainContainer, fragment, tag + fragmentsStack)
            .addToBackStack(tag + fragmentsStack)
            .commit()
    }
}