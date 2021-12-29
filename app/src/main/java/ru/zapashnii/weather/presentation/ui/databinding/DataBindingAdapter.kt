package ru.zapashnii.weather.presentation.ui.databinding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.zapashnii.weather.utils.inputmask.MaskedTextChangedListener
import ru.zapashnii.weather.R
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.domain.model.ListItemField
import ru.zapashnii.weather.domain.model.Weather
import ru.zapashnii.weather.presentation.adapters.ItemListAdapter
import ru.zapashnii.weather.presentation.adapters.WeatherAdapter
import ru.zapashnii.weather.presentation.glide_image_view.GlideImageView
import ru.zapashnii.weather.utils.Utils
import ru.zapashnii.weather.utils.setCountdownOnClickListener
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
        ArrayAdapter(
            MainApp.instance.applicationContext,
            R.layout.list_item_for_cash_back,
            items ?: listOf()
        )
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
        view.startAnimation(
            AnimationUtils.loadAnimation(
                MainApp.instance.applicationContext,
                R.anim.rotete_loop
            )
        )
        view.alpha = 0.6F
        view.isEnabled = false
    } else {
        view.setImageResource(R.drawable.ic_share_white_24dp)
        view.startAnimation(
            AnimationUtils.loadAnimation(
                MainApp.instance.applicationContext,
                R.anim.icon_shaking
            )
        )
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

/**
 * Адаптер слушает прокрутку [RecyclerView] и выполняет методы
 *
 * @param view              [RecyclerView]
 * @param onTopScroll       прокрутка вверх
 * @param onBottomScroll    прокрутка вниз
 */
@BindingAdapter("bind:topScroll", "bind:bottomScroll", requireAll = true)
fun setScrollListener(view: RecyclerView, onTopScroll: () -> Unit, onBottomScroll: () -> Unit) {
    view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy < 0) {
                onTopScroll.invoke()
            } else if (dy > 0) {
                onBottomScroll.invoke()
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }
    })
}

/**
 * Адаптер скрывает FloatingActionButton в зависимости от [isVisible]
 *
 * @param view          [FloatingActionButton]
 * @param isVisible     TRUE - показать, FALSE - скрыть
 */
@BindingAdapter("bind:isVisible")
fun showFab(view: FloatingActionButton, isVisible: Boolean) {
    if (isVisible) view.show() else view.hide()
}

/**
 * Адаптер добаления маски ввода для [EditText]
 *
 * @param view      [EditText]
 * @param format    маска
 */
@BindingAdapter("bind:maskEditText")
fun mask(view: EditText, format: String) {
    val mask = MaskedTextChangedListener(format, view, null)
    view.addTextChangedListener(mask)
}

/**
 * Адаптер для биндинга в RecyclerView списка [ListItemField]
 * @param view      RecyclerView
 * @param items     список элементов [ListItemField] для отображения
 */
@BindingAdapter("bind:items")
fun setItems(view: RecyclerView, items: List<ListItemField>) {
    (view.adapter as ItemListAdapter).setData(items)
}

/**
 * Загрузить изображение в [GlideImageView] по сслыке
 * @param url ссылка на изображение
 */
@BindingAdapter("bind:imageUrl")
fun setImage(view: GlideImageView, url: String?) {
    url?.let { view.loadImage(it) }
}

/**
 * Установить видимость всей View.
 * @param isVisible     TRUE - видимо, FALSE - невидимо
 */
@BindingAdapter("bind:visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * Установить видимость всей View.
 * @param isVisible     TRUE - видимо, FALSE - невидимо
 */
@BindingAdapter("bind:alphaVisibility")
fun setAlphaVisibility(view: View, isVisible: Boolean) {

    val alpha = if (isVisible) 1f else 0f
    alphaAnimation(view, alpha)
}

fun alphaAnimation(view: View, targetAlpha: Float, duration: Long = 300L) {
    view.apply {
        animate().alpha(targetAlpha).setDuration(duration).start()
    }
}

/**
 * Слушатель с блокировкой двойного нажатия. Плюс прячет клавиатуру.
 * @param view      view, для которой устанавливается слушатель.
 * @param onClick   действие при нажатии.
 */
@BindingAdapter("bind:onCountdownClickAndHideKeyboard")
fun setCountdownOnClickAndHideKeyboard(view: View, onClick: () -> Unit) {
    view.setCountdownOnClickListener {
        Utils.hideSoftKeyboardFrom(view.context, view)
        onClick.invoke()
    }
}

/**
 * Установить текст ошибки в [TextInputLayout]
 * @param error     текст ошибки
 */
@BindingAdapter("bind:error")
fun setErrorText(view: TextInputLayout, error: String?) {
    view.error = error
}

/**
 * Установить текст подсказки в [TextInputLayout]
 * @param helperText     текст подсказки
 */
@BindingAdapter("bind:helperText")
fun setHelperText(view: TextInputLayout, helperText: String?) {
    view.helperText = helperText
}

/**
 * Установить текст с анимацией затухания и плавного появления.
 * @param text     устанавливаемый текст.
 */
@BindingAdapter("bind:textOrHideWithAnimation")
fun setTextOrHideWithAnimation(view: TextInputEditText, text: String?) {
    if (view.text.toString() == text) {
        // Чтобы работало для TextInputEditText оставить метод setText(String) (не использовать view.text = text)
        view.setText(text)
    } else {
        setViewContentWithAnimation(view) {
            // Чтобы работало для TextInputEditText оставить метод setText(String) (не использовать view.text = text))
            (view.parent as ViewGroup).visibility =
                if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            view.setText(text)
        }
    }
}

/**
 * Выполняет анимацию затухания выполняет переданную функцию и плавно отображает вью
 * @param view                  анимируемая вью
 * @param onHideAnimationEnd    действие выполняемое после затухания
 */
private fun setViewContentWithAnimation(view: View, onHideAnimationEnd: () -> Unit) {
    val fadeStart = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
        duration = 200L
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onHideAnimationEnd.invoke()
            }
        })
    }
    val fadeEnd = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
        duration = 200L
    }

    AnimatorSet().apply {
        play(fadeEnd).after(fadeStart)
        start()
    }
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
 * Установить видимость всей View анимированно.
 * @param isVisible     TRUE - видимо, FALSE - невидимо
 */
@BindingAdapter("bind:visibilityWithAnimation")
fun setVisibilityWithAnimation(view: View, isVisible: Boolean) {
    if (isVisible)
        showWithAnimation(view)
    else
        hideWithAnimation(view)

}

/**
 * Слушатель с блокировкой двойного нажатия.
 * @param view      view, для которой устанавливается слушатель.
 * @param action    действие при нажатии.
 */
@BindingAdapter("bind:countDownOnClick")
fun setCountDownOnClick(view: View, action: () -> Unit) {
    view.setCountdownOnClickListener { action.invoke() }
}