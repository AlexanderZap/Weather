package ru.zapashnii.weather.presentation.ui.base_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.zapashnii.weather.R
import ru.zapashnii.weather.navigation.ViewRouter
import ru.zapashnii.weather.presentation.ui.search_weather.SearchWeatherFragment
import ru.zapashnii.weather.presentation.ui.start_fragment.StartFragment
import ru.zapashnii.weather.utils.Utils
import ru.zapashnii.weather.utils.appComponent
import javax.inject.Inject

/** Базовый экран на котором будут создоваться/подменяться фрагменты */
class BaseActivity : AppCompatActivity() {

    companion object {
        const val TYPE_ACTIVITY = "TYPE_ACTIVITY"

        const val SEARCH_WEATHER = "SEARCH_WEATHER"
        const val CITY_NAME = "CITY_NAME"
        const val START_FRAGMENT = "START_FRAGMENT"
    }

    private var progress: MaterialDialog? = null

    @Inject
    lateinit var viewRouter: ViewRouter

    private var fragmentsStack = 0
    private var tag = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        appComponent.inject(this)

        viewRouter.setCurrentActivity(this)

        addFragment(StartFragment.newInstance())

        progress = MaterialDialog(this).customView(R.layout.dialog_progress, scrollable = false)
            .cancelable(false)

        when (intent.getStringExtra(TYPE_ACTIVITY)) {
            SEARCH_WEATHER -> replaceFragmentWithAnimation(
                SearchWeatherFragment.newInstance(
                    cityName = intent.getStringExtra(CITY_NAME) ?: ""
                )
            )
        }
    }

    /** Передаем навигатору viewRouter текущую активность */
    override fun onStart() {
        super.onStart()
        viewRouter.setCurrentActivity(this)
        progress?.hide()
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

    override fun onDestroy() {
        super.onDestroy()
        if (progress != null) {
            progress?.dismiss()
            progress = null
        }
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

    /**
     * Создать фрагмент с анимцией движения(смахивания) слева направо
     *
     * @param fragment
     */
    fun addFragmentWithAnimation(fragment: Fragment) {
        Utils.hideSoftKeyboard(this)
        fragmentsStack++
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.enter, 0, 0, R.anim.pop_exit)
            .add(R.id.mainContainer, fragment, tag + fragmentsStack)
            .addToBackStack(tag + fragmentsStack)
            .commitAllowingStateLoss()

    }

    /**
     * Подменить фрагмент с анимацией движения(смахивания) слева направо
     *
     * @param fragment
     */
    fun replaceFragmentWithAnimation(fragment: Fragment) {
        fragmentsStack++
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.enter, 0, 0, R.anim.pop_exit)
            .replace(R.id.mainContainer, fragment, tag + fragmentsStack)
            .addToBackStack(tag + fragmentsStack)
            .commitAllowingStateLoss()
    }

    /**
     * Подменить фрагмент с анимацией движения(смахивания) сверху вниз
     *
     * @param fragment
     */
    fun addFragmentWithSlideAnimation(fragment: Fragment) {
        Utils.hideSoftKeyboard(this)
        fragmentsStack++
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.enter_up, 0, 0, R.anim.exit_down)
            .add(R.id.mainContainer, fragment, tag + fragmentsStack)
            .addToBackStack(tag + fragmentsStack)
            .commitAllowingStateLoss()
    }

    /**
     * Подменить фрагмент с анимацией движения(смахивания) сверху вниз
     *
     * @param fragment
     */
    fun replaceFragmentWithSlideAnimation(fragment: Fragment) {
        Utils.hideSoftKeyboard(this)
        fragmentsStack++
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.enter_up, 0, 0, R.anim.exit_down)
            .replace(R.id.mainContainer, fragment, tag + fragmentsStack)
            .addToBackStack(tag + fragmentsStack)
            .commitAllowingStateLoss()
    }

    /**
     * Показать BottomSheetDialogFragment со списком элементов
     *
     * @param fragment      [BottomSheetDialogFragment]
     */
    fun showBottomSheet(fragment: BottomSheetDialogFragment) {
        fragment.show(supportFragmentManager, null)
    }

    override fun onBackPressed() {
        fragmentsStack--
        Utils.hideSoftKeyboard(this)
        super.onBackPressed()
    }

    /** Показать индикатор загрузки */
    fun showProgress() {
        progress?.show()
    }

    /** Скрыть индикатор загрузки */
    fun hideProgress() {
        progress?.cancel()
    }
}