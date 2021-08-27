package ru.zapashnii.weather.presentation.ui.databinding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import ru.zapashnii.weather.R
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.presentation.adapters.WeatherAdapter
import ru.zapashnii.weather.utils.toBitmapFromBase64

/**
 * ------------------------------------------------------------------------------------------------
 * Клас для датабиндинг адаптеров.
 * Здесь описываются наиболее общие адаптеры, которые можно переиспользовать.
 * Спецыфические адаптеры описывать в том же пакете, где лежит ViewModel.
 * ------------------------------------------------------------------------------------------------
 */

/**
 *  Адаптер для биндинга в ViewPager списка [Weather]
 *
 * @param view              ViewPager
 * @param data              список элементов [Weather] для отображения
 */
@BindingAdapter("bind:itemsWeatherViewPagerAdapter")
fun setItemsInRecyclerView(view: ViewPager, data: List<Weather>) {
    (view.adapter as? WeatherAdapter)?.setData(data)
}

/**
 * Адаптер для биндинга в RecyclerView списка [Weather]
 * @param view     RecyclerView
 * @param data     список элементов [Weather] для отображения
 */
/*@BindingAdapter("bind:itemsWeatherRecyclerViewAdapter")
fun setItemsInRecyclerView(view: RecyclerView, data: List<Weather?>?) {
    (view.adapter as? CashBackAdapter)?.setData(data)
}*/

/**
 * Адаптер для биндинга в TextInputLayout списка [String]
 * @param view         TextInputLayout
 * @param items        список элементов [String] для отображения
 */
@BindingAdapter("bind:itemsSpinnerWeather")
fun setItemsInSpinner(view: TextInputLayout, items: List<String>?) {
    val adapter =
        ArrayAdapter(MainApp.instance.applicationContext, R.layout.list_item_for_cash_back, items ?: listOf())
    (view.editText as? AutoCompleteTextView)?.setAdapter(adapter)
}


@BindingAdapter("bind:clickItemSpinner")
fun setScrollListener(view: AutoCompleteTextView, onClick: (value: String) -> Unit) {
    view.setOnItemClickListener { parent, _, position, _ ->
        onClick.invoke(parent.getItemAtPosition(position).toString())
    }
}

/**
 * Адаптер показа анимации загрузки FloatingActionButton в зависимости от [isDownloadingState]
 *
 * @param view                      [FloatingActionButton]
 * @param isDownloadingState        ели TRUE показать анимацию, если FALSE убрать анимацию
 */
@BindingAdapter("bind:isDownloadingState")
fun observeDownloadingState(view: FloatingActionButton, isDownloadingState: Boolean) {
    if (isDownloadingState) {
        view.setImageResource(R.drawable.ic_round_sync_24)
        view.startAnimation(AnimationUtils.loadAnimation(MainApp.instance.applicationContext,
            R.anim.rotete_loop))
        view.alpha = 0.6F
        view.isEnabled = false
    } else {
        view.setImageResource(R.drawable.ic_share_white_24dp)
        view.startAnimation(AnimationUtils.loadAnimation(MainApp.instance.applicationContext,
            R.anim.icon_shaking))
        view.alpha = 1.0F
        view.isEnabled = true
    }

    /**
     * Анимированно показать View
     * @param view          анимируемое View
     * @param duration      длительность анимации
     */
    fun showWithAnimation(view: View, duration: Long = 300L) {
        if (view.visibility == View.VISIBLE) return
        view.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        visibility = View.VISIBLE
                    }
                })
                .start()
        }
    }

    /**
     * Анимированно спрятать View
     * @param view          анимируемое View
     * @param duration      длительность анимации
     */
    fun hideWithAnimation(view: View, duration: Long = 300L) {
        if (view.visibility == View.GONE) return
        view.apply {
            alpha = 1f
            animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        visibility = View.GONE
                    }
                })
                .start()
        }
    }

    /**
     * Установить изображение в ImageView
     * @param base64     изображение в формате Base64
     */
    @BindingAdapter("bind:base64")
    fun setImageFromBase64(view: ImageView, base64: String?) {
        val picture = if (base64.isNullOrEmpty()) {
            hideWithAnimation(view)
            null
        } else {
            showWithAnimation(view)
            base64.toBitmapFromBase64()
        }
        Glide.with(view.context.applicationContext)
            .load(picture)
            .into(view)
    }
}
