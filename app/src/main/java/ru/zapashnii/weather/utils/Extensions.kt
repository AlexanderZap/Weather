package ru.zapashnii.weather.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
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