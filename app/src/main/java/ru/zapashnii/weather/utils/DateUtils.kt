package ru.zapashnii.weather.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    /**
     * Получить номер деня месяца
     *
     * @param date  дата [Date]
     * @return      номер деня месяца
     */
    fun getDayOfMonth(date: Date): Int {
        return withCalendar(date) { getDayOfMonth(it) }
    }

    /**
     * Получить номер деня месяца
     *
     * @param calendar  [Calendar]
     * @return          номер деня месяца
     */
    private fun getDayOfMonth(calendar: Calendar): Int {
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Получить номер месяца
     *
     * @param date  дата [Date]
     * @return      номер месяца
     */
    fun getMonth(date: Date): Int {
        return withCalendar(date) { getMonth(it) }
    }

    /**
     * Получить номер месяца
     *
     * @param calendar  [Calendar]
     * @return          номер месяца
     */
    private fun getMonth(calendar: Calendar): Int {
        return calendar.get(Calendar.MONTH)
    }

    /**
     * Получить номер года
     *
     * @param date  дата [Date]
     * @return      номер года
     */
    fun getYear(date: Date): Int {
        return withCalendar(date) { getYear(it) }
    }

    /**
     * Получить номер года
     *
     * @param calendar  [Calendar]
     * @return          номер года
     */
    private fun getYear(calendar: Calendar): Int {
        return calendar.get(Calendar.YEAR)
    }

    /**
     * Получить значение по [key] для определенного календарного поля
     *
     * @param date  дата [Date]
     * @param key   Номер поля для получения и установки с указанием: [Calendar.MINUTE], [Calendar.SECOND] и т.д.
     * @return      значение для данного календарного поля.
     */
    private fun getCalendarField(date: Date?, key: Int): Int {
        return withCalendar(date) { it.get(key) }
    }

    /**
     * Встроенная функция
     *
     * @param T         тип ожидаемого значения
     * @param date      дата [Date]
     * @param block     callback возвращающий [Calendar]
     * @return          тип возвращаемого значения [T]
     */
    inline fun <T> withCalendar(date: Date? = null, block: (calendar: Calendar) -> T): T {
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }

        return block.invoke(calendar)
    }

    /**
     * Формотирование даты
     *
     * @param date  дата [Date]
     * @param mask  маска форматирования даты
     * @return      измененная дата
     */
    fun formatDate(date: Date, mask: String? = null): String {
        return if (mask.isNullOrBlank()) {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat(mask, Locale.getDefault()).format(date)
        }
    }

    /**
     * Форматирует дату и время в формат вида:
     * пн, 20 апреля 1945, 09:41
     *
     * @param date  дата [Date]
     * @return      измененная дата
     */
    fun formatDateAndTimeLocalized(date: Date): String {
        val formattedDate = formatDateLocalized(date)
        val formattedTime = formatDate(date, "HH:mm")

        return "$formattedDate, $formattedTime"
    }

    /**
     * Форматирует дату и время в формат вида:
     * пн, 20 апреля 1945
     *
     * @param date  дата [Date]
     * @return      измененная дата
     */
    fun formatDateLocalized(date: Date): String {
        return formatDate(date, "EEE, dd MMMM yyyy")
    }

    /**
     * Форматирует дату и время в формат вида:
     * 20 апреля 1945
     *
     * @param date  дата [Date]
     * @return      измененная дата
     */
    fun formatDateLocalizedWithoutWeekday(date: Date): String {
        return formatDate(date, "dd MMMM yyyy")
    }

    /**
     * Форматирует дату и время в формат вида:
     * Только что, 09:41
     * Сегодня, 09:41
     * Вчера, 09:41
     * пн, 20 апреля, 09:41
     * пн, 20 апреля 1945, 09:41
     *
     * @param date  дата [Date]
     * @return      измененная дата
     */
    fun formatDateAndTimeLocalizedSimplified(date: Date): String {
        val formattedDate = formatDateLocalizedSimplified(date)
        val formattedTime = formatDate(date, "HH:mm")

        return "$formattedDate, $formattedTime"
    }

    /**
     * Форматирует дату в формат вида:
     * Только что
     * Сегодня
     * Вчера
     * пн, 20 апреля
     * пн, 20 апреля 1945
     *
     * @param date  дата [Date]
     * @return      измененная дата
     */
    fun formatDateLocalizedSimplified(date: Date): String {
        return when {
            isRightNow(date) -> "Только что"
            isTodayDate(date) -> "Сегодня"
            isYesterday(date) -> "Вчера"
            isCurrentMonth(date) -> {
                formatDate(date, "EEE, dd MMMM")
            }
            isCurrentYear(date) -> {
                formatDate(date, "EEE, dd MMMM yyyy")
            }
            else -> {
                formatDateLocalized(date)
            }
        }
    }

    /**
     * Проверка для получения текста "Только что"
     *
     * @param date  дата [Date]
     * @return
     */
    private fun isRightNow(date: Date): Boolean {
        val hours = getCalendarField(date, Calendar.HOUR_OF_DAY)
        val minutes = getCalendarField(date, Calendar.MINUTE)

        val currentHours = getCalendarField(null, Calendar.HOUR_OF_DAY)
        val currentMinutes = getCalendarField(null, Calendar.MINUTE)

        return isTodayDate(date) && currentHours == hours && (currentMinutes - minutes <= 10)
    }

    /**
     * Проверка для получения текста "Сегодня"
     *
     * @param date  дата [Date]
     * @return
     */
    private fun isTodayDate(date: Date): Boolean {
        val currentDay = withCalendar { getDayOfMonth(it) }
        val currentMonth = withCalendar { getMonth(it) }
        val currentYear = withCalendar { getYear(it) }

        val day = getDayOfMonth(date)
        val month = getMonth(date)
        val year = getYear(date)

        return currentDay == day && currentMonth == month && currentYear == year
    }

    /**
     * Проверка для получения текста "Вчера"
     *
     * @param date  дата [Date]
     * @return
     */
    private fun isYesterday(date: Date): Boolean {
        val currentDay = withCalendar { getDayOfMonth(it) }
        val currentMonth = withCalendar { getMonth(it) }
        val currentYear = withCalendar { getYear(it) }

        val day = getDayOfMonth(date)
        val month = getMonth(date)
        val year = getYear(date)

        return (currentDay - day == 1) && currentMonth == month && currentYear == year
    }

    /**
     * Проверка для получения текста "пн, 20 апреля"
     *
     * @param date  дата [Date]
     * @return
     */
    private fun isCurrentMonth(date: Date): Boolean {
        val currentMonth = withCalendar { getMonth(it) }

        val month = getMonth(date)

        return currentMonth == month && isCurrentYear(date)
    }

    /**
     * Проверка для получения текста "пн, 20 апреля 1945"
     *
     * @param date  дата [Date]
     * @return
     */
    private fun isCurrentYear(date: Date): Boolean {
        val currentYear = withCalendar { getYear(it) }
        val year = getYear(date)

        return currentYear == year
    }

    /**
     * Форматирование даты для вывода на экран справки
     * @param date  дата [Date]
     * @return      строковое представление даты с маской dd.MM.yyyy (день_месяца.месяц.год)
     */
    fun formatDate(date: Date): String {
        val simpleDate = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
        return simpleDate.format(date)
    }

    /**
     * Сравнивает две даты, прибавляя к первой delta дней.
     * Возвращает TRUE, если firstDate + deltaDay >= secondDate
     *
     * @param firstDate первая дата
     * @param secondDate вторая дата
     * @param deltaDay прибавляется к первой дате перед сравниванием
     *
     * @return TRUE, если firstDate + deltaDay >= secondDate
     */
    fun compareDateWithDelta(firstDate: Date, secondDate: Date, deltaDay: Int): Boolean {
        val instance = Calendar.getInstance()
        instance.time = firstDate //устанавливаем дату, с которой будет производить операции
        instance.add(Calendar.DAY_OF_MONTH, deltaDay) // прибавляем delta дней к установленной дате
        val newDate = instance.time // получаем измененную дату
        return newDate >= secondDate
    }

    /**
     * Проверяет попадает ли дата в указанный диапазон
     *
     * @param currentDate   текущая дата
     * @param endDate       дата окончания диапазоно дат
     * @param deltaMonth    прибавляется к дате окончания диапазона перед сравниванием
     *
     * @return TRUE, если startRangeDate >= currentDate <= endDate (currentDate попадает в диапазон)
     */
    fun compareDateInRange(currentDate: Date, endDate: Date, deltaMonth: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = endDate //устанавливаем дату, с которой будет производить операции
        calendar.add(Calendar.MONTH, deltaMonth) // прибавляем delta месяцев к установленной дате
        val startRangeDate = calendar.time // получаем измененную дату
        return startRangeDate >= currentDate && currentDate <= endDate
    }

    /**
     * Проверяет что дата больше или равна дате минус дельта в месяцах
     *
     * @param firstDate   проверяемая дата
     * @param secondDate  дата от которой прибавляется дельта
     * @param deltaMonth  дельта в месяцах, прибавляется ко второй дате
     *
     * @return TRUE, если newDate <= currentDate
     */
    fun compareDateWithMonthDelta(firstDate: Date, secondDate: Date, deltaMonth: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = secondDate //устанавливаем дату, с которой будет производить операции
        calendar.add(Calendar.MONTH, deltaMonth) // прибавляем delta месяцев к установленной дате
        val newDate = calendar.time // получаем измененную дату
        return newDate <= firstDate
    }
}