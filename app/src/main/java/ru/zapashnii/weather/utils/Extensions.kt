package ru.zapashnii.weather.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
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
import java.lang.NumberFormatException

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

    Toast.makeText(context, context.getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT).show()
}

/**
 * Установить текст и показать TextView, если текс не пустой и не null.
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