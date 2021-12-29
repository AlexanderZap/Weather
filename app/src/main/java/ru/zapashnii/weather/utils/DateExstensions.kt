package ru.zapashnii.weather.utils

import android.os.Build
import java.util.*

/**
 * Получить номер деня месяца
 * @return  номер деня месяца
 */
fun Date.dayOfMonth(): Int {
    return DateUtils.getDayOfMonth(this)
}

/**
 * Получить номер месяца
 * @return  номер месяца
 */
fun Date.month(): Int {
    return DateUtils.getMonth(this)
}

/**
 * Сдвинуть месяц на один вперёд
 * @return  следующий месяц
 */
fun Date.monthShifted(): Int {
    return DateUtils.getMonth(this) + 1
}

/**
 * Получить номер года
 * @return  номер года
 */
fun Date.year(): Int {
    return DateUtils.getYear(this)
}

/**
 * Получить дату в виде строки
 * @param mask  маска форматирования даты
 * @return      строка с датой
 */
fun Date.format(mask: String): String {
    return DateUtils.formatDate(this, mask)
}

/**
 * Преобразовать строку в дату [Date]
 * Функция пытается распарсить строку в дату, используя сначала шаблон yyyy-MM-dd'T'HH:mm:ss.SSS,
 * а потом yyyy-MM-dd'T'HH:mm:ss.SSSXXX. После двух неудачных попыток возвращает null.
 *
 * @return  дата
 */
fun String?.parseToDate(): Date? {
    return try {
        java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).parse(this ?: "")
    } catch (e: Exception) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).parse(this ?: "")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}