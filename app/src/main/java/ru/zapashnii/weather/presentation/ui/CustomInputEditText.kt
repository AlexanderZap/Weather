package ru.zapashnii.weather.presentation.ui

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.zapashnii.weather.R

/** Кастомный EditText */
open class CustomInputEditText : TextInputEditText, View.OnFocusChangeListener, TextWatcher {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr)

    companion object {
        const val TAG = "CustomInputEditText"
        const val DEFAULT_PATTERN: String = "^.{0,32}\$"
        const val DEFAULT_MAX_LENGTH: Int = 32
    }

    /** Родительский TextInputLayout */
    private var parentTextInputLayout: TextInputLayout? = null

    /** Шаблон проверки при вводе */
    var inputPattern: String = DEFAULT_PATTERN

    /** Шаблон проверки введенного теста */
    var completePattern: String = DEFAULT_PATTERN

    /** Максимальный размер введенного текста */
    var maxLength: Int = DEFAULT_MAX_LENGTH

    /** Описание поля (Подсказка снизу) */
    var description: String = ""

    /** Ресур на текст ошибки */
    var error: Int = R.string.incorrect_value

    /** Фильтр при вводе */
    var inputFilter: InputFilter = getDefaultInputFilter()

    /**
     * Установить родительский TextInputLayout
     *
     * @param textInputLayout   родительский TextInputLayout
     */
    fun setParentTextInputLayout(textInputLayout: TextInputLayout?) {
        if (textInputLayout == null) {
            return
        }
        this.parentTextInputLayout = textInputLayout
    }

    override fun onAttachedToWindow() {
        filters = arrayOf(inputFilter)
        onFocusChangeListener = this
        addTextChangedListener(this)
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        filters = arrayOfNulls(0)
        onFocusChangeListener = null
        parentTextInputLayout = null
        inputPattern = DEFAULT_PATTERN
        description = ""
        maxLength = DEFAULT_MAX_LENGTH
        removeTextChangedListener(this)
        super.onDetachedFromWindow()
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            onFocusSet()
        } else {
            onFocusRemoved()
        }
    }

    /** Установлен фокус */
    private fun onFocusSet() {
        hideError()
        checkDescription()
        checkIcon()
        showCounter(maxLength)
    }

    /** Фокус убран */
    private fun onFocusRemoved() {
        checkIcon()
        checkDescription()
        checkError()
        hideCounter()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun afterTextChanged(s: Editable?) {
        checkAll(s.toString(), hasFocus())
    }

    /**
     * Начать проверки
     *
     * @param text      введенный текст
     * @param hasFocus  True, если есть фокус на EditText
     */
    private fun checkAll(text: String = this.text?.toString() ?: "", hasFocus: Boolean = hasFocus()) {
        checkIcon(text, hasFocus)
        checkDescription(text, hasFocus)
        if (!hasFocus()) {
            checkError(text)
        }
    }

    /**
     * Проверить введенный текст и показать нужную иконку
     *
     * @param text      введенный текст
     * @param hasFocus  True, если есть фокус на EditText
     */
    private fun checkIcon(text: String = this.text?.toString() ?: "", hasFocus: Boolean = hasFocus()) {
        when {
            isTextCorrect(text) -> setCorrectFieldIcon()
            !isTextCorrect(text) && !hasFocus -> setIncorrectFieldIcon()
            else -> setDefaultFieldIcon()
        }
    }

    /**
     * Проверка текста на правильность ввода
     *
     * @param text      введенный текст
     * @return          True, если корректно
     */
    private fun isTextCorrect(text: String): Boolean {
        return text.matches(completePattern.toRegex())
    }

    /**
     * Проверить необходимость показа описание поля (Подсказка снизу)
     *
     * @param text      введенный текст
     * @param hasFocus  True, если поле в фокусе
     */
    private fun checkDescription(
        text: String = this.text?.toString() ?: "",
        hasFocus: Boolean = hasFocus(),
    ) {
        if (isNeedShowDescription(text, hasFocus)) {
            showDescription(description)
        } else {
            hideDescription()
        }
    }

    /**
     * Показать описание поля (Подсказка снизу)
     *
     * @param description   описание поля (Подсказка снизу)
     */
    private fun showDescription(description: String) {
        parentTextInputLayout?.isErrorEnabled = false
        parentTextInputLayout?.helperText = description
        parentTextInputLayout?.isHelperTextEnabled = true
    }

    /** Скрыть описание поля (Подсказка снизу) */
    private fun hideDescription() {
        parentTextInputLayout?.isHelperTextEnabled = false
    }

    /**
     * Проверить необходимость показа описание поля (Подсказка снизу)
     *
     * @param text      введенный текст
     * @param hasFocus  True, если есть фокус на EditText
     * @return          True, если поле в фокусе
     */
    private fun isNeedShowDescription(
        text: String = this.text?.toString() ?: "",
        hasFocus: Boolean = hasFocus(),
    ): Boolean {
        return when {
            hasFocus -> true
            !hasFocus && isTextCorrect(text) -> true
            else -> false
        }
    }

    private fun checkError(text: String = this.text?.toString() ?: "") {
        when {
            //isRequiredFieldEmpty(text) -> showError(this.error)
            !isTextCorrect(text) -> showError(this.error)
            else -> hideError()
        }
    }

    /**
     * Показать тест ошибки
     *
     * @param error текст ошибки
     */
    fun showError(@StringRes error: Int) {
        showError(context?.getString(error) ?: "")
    }

    /**
     * Показать тест ошибки
     *
     * @param error текст ошибки
     */
    private fun showError(error: String) {
        //parentTextInputLayout?.isHelperTextEnabled  = false
        parentTextInputLayout?.error = error
        parentTextInputLayout?.isErrorEnabled = true
        checkCounter()
    }

    /** Проверить размер введенного текста */
    private fun checkCounter() {
        if (text?.length ?: 0 > maxLength) {
            showCounter(maxLength)
        } else {
            hideCounter()
        }
    }

    /** Показать счетчик символов*/
    private fun showCounter(maxLength: Int) {
        parentTextInputLayout?.counterMaxLength = maxLength
        parentTextInputLayout?.isCounterEnabled = true
    }

    /** Скрыть счетчик символов*/
    private fun hideCounter() {
        parentTextInputLayout?.isCounterEnabled = false
    }

    /** Скрыть текст ошибки */
    private fun hideError() {
        parentTextInputLayout?.isErrorEnabled = false
    }

    /** Поле коректно */
    private fun setCorrectFieldIcon() {
        setDrawableEnd(R.drawable.ic_check_circle_white_24dp)
        setDrawableColor(R.color.check_color_dark_green)
        val drawableEnd = compoundDrawablesRelative.getOrNull(2)
        drawableEnd?.alpha = 128
    }

    /** Поле некоректно */
    private fun setIncorrectFieldIcon() {
        setDrawableEnd(R.drawable.ic_error_outline)
        setDrawableColor(R.color.error)
    }

    /** Поле поумолчанию */
    private fun setDefaultFieldIcon() {
        setDrawableEnd(null)
        setDrawableColor(R.color.colorPrimary)
    }

    /**
     * Нарисовать изображение в конце поля
     *
     * @param drawableRes   ресур изображения
     */
    private fun setDrawableEnd(@DrawableRes drawableRes: Int?) {
        val drawableEnd = drawableRes?.let { context?.getDrawable(it) }
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawableEnd, null)
    }

    /**
     * Изменить оттенок изображение в конце поля
     *
     * @param color ресурс цвета
     */
    private fun setDrawableColor(@ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            compoundDrawableTintList = ContextCompat.getColorStateList(context, color)
        }
    }

    /**
     * Создать [InputFilter]
     *
     * @return  [InputFilter]
     */
    private fun getDefaultInputFilter(): InputFilter {
        return object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int,
            ): CharSequence? {
                val resultString = try {
                    dest?.replaceRange(dstart, dend, source.toString()) ?: ""
                } catch (ex: Exception) {
                    dest?.toString() + source?.toString()
                }

                val matcher = resultString.toString().matches(inputPattern.toRegex())
                if (resultString.isNotEmpty() && !matcher) {
                    startIncorrectFieldAnimation()
                    return try {
                        dest?.substring(dstart, dend)
                    } catch (ex: Exception) {
                        ""
                    }
                }
                return null
            }
        }
    }

    /** Показать анимацию при неверном вводе */
    private fun startIncorrectFieldAnimation() {
        try {
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.incorrect_edit_text))
        } catch (ex: Exception) {
        }
    }
}