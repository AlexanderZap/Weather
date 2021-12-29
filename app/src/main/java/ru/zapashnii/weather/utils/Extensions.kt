package ru.zapashnii.weather.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ru.zapashnii.weather.R
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.utils.inputmask.helper.Mask
import ru.zapashnii.weather.utils.inputmask.model.CaretString

/**
 * Проверка Доступны ли сервисы Google Play
 *
 * @return      true если доступны, false если нет
 */
fun Context.isGooglePlayServicesAvailable(): Boolean {
    val status = GoogleApiAvailability
        .getInstance()
        .isGooglePlayServicesAvailable(this)

    return status == ConnectionResult.SUCCESS
}

/**
 * Безопасно преобразовать [String] в [Double].
 * Удаляет лишние буквы и символы, заменяет запятую на точку.
 *
 * @return  число типа [Double]
 */
fun String.convertToDouble(): Double {
    // 1 - Убираем пробелы
    val noSpacesText = this.removeSpaces()

    // 2 - Находим индекс последней запятой или точки
    val lastDecimalSeparatorIndex = noSpacesText.indexOfLast { it == '.' || it == ',' }

    // 3 - Удаляем все запятые и точки, кроме последней
    val removedRedundantSymbolsText =
        noSpacesText.filterIndexed { index, c -> !((c == ',' || c == '.') && index != lastDecimalSeparatorIndex) }

    // 4 - Заменяем запятую на точку и удаляем все что не цифра или точка
    val onlyDigitsAndDotsText = removedRedundantSymbolsText
        .replace(',', '.')
        .replace("[^0-9.]".toRegex(), "")

    // 5 - Конвертируем в Double
    return try {
        onlyDigitsAndDotsText.toDouble()
    } catch (ex: NumberFormatException) {
        0.0
    }
}

/**
 * Предотвратить двойное нажатие
 *
 * @param timeout
 * @param action
 */
fun View.setCountdownOnClickListener(timeout: Long = 300L, action: (view: View?) -> Unit) {
    this.setOnClickListener(CountDownOnClickListener(timeout, action))
}

/**
 * Убрать пробелы
 *
 * @return          преобразованная строка
 */
fun String.removeSpaces(): String {
    return this.replace(" ", "").replace("\\s".toRegex(), "")
}

/**
 * Преобразовать Base64 строку в [Bitmap]
 */
fun String.toBitmapFromBase64(): Bitmap {
    val decodedString: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

/**
 * Копировать текст в буфер обмена. После копирования показывает уведомление в виде [Toast]
 * @param label видимый пользователю label скопированных данных
 */
fun String.copyInClipboard(label: String) {
    val context = MainApp.instance
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, this)
    clipboardManager.setPrimaryClip(clipData)

    Toast.makeText(
        context,
        context.getString(R.string.text_copied_to_clipboard),
        Toast.LENGTH_SHORT
    ).show()
}

/**
 * Установить текст и показать TextView, если текс не пустой и не null.
 *
 * @param text  текст, устанавливаемый в TextView.
 */
fun TextView.setTextOrHide(text: String?) {
    if (!text.isNullOrBlank()) {
        this.visibility = View.VISIBLE
        this.text = text
    } else {
        this.visibility = View.GONE
    }
}

/**
 * Применить оттенок к изображению
 *
 * @param color [ColorRes]
 */
fun ImageView.setTintColorRes(@ColorRes color: Int) {
    this.setTintColor(ContextCompat.getColor(this.context, color))
}

/**
 * Применить оттенок к изображению
 *
 * @param color [ColorInt]
 */
fun ImageView.setTintColor(@ColorInt color: Int) {
    this.imageTintList = ColorStateList.valueOf(color)
}

/**
 * Форматировать сумму в сумма + [currency]
 *
 * @param currency  валюта
 * @return          измененная строка
 */
fun String.formatMoney(currency: String): String {
    return if (this.isEmpty()) ""
    else "%,.2f $currency".format(this.convertToDouble())
}

/**  Добавляет TextWatcher для EditText и позволяет реализовать только нужные вызовы методоа */
fun EditText?.setTextChangedListener(
    beforeTextChanged: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null,
    onTextChanged: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null,
    afterTextChanged: ((Editable?) -> Unit)? = null
) {
    this?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged?.let { it.invoke(s, start, count, after) }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged?.let { it.invoke(s, start, before, count) }
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged?.let { it.invoke(editable) }
        }
    })
}

/**
 * Формотировать строку под телефон
 *
 * @param maskPhone маска по умолчанию "+[0] ([000]) [000]-[00]-[0000]"
 * @return          измененная строка
 */
fun String.formatPhone(maskPhone: Mask = Mask("+[0] ([000]) [000]-[00]-[0000]")): String {
    return maskPhone.apply(CaretString(this, this.length, CaretString.CaretGravity.FORWARD(true))).formattedText.string
}