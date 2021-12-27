package ru.zapashnii.weather.domain.model

/**
 * Интерфейс списока элементов для отображения
 *
 * @property title              Заголовок
 * @property subtitle           Подзаголовок
 * @property iconRes            ID изображения
 * @property iconUrl            Url изображения
 * @property colorIcon          Цвет иконки
 * @property colorTitle         Цвет заголовка
 * @property colorSubtitle      Цвет подзаголовка
 */
data class ListItemField(
    val title: String,
    val subtitle: String? = null,
    val iconRes: Int? = null,
    val iconUrl: String? = null,
    val colorIcon: Int? = null,
    val colorTitle: Int? = null,
    val colorSubtitle: Int? = null,
)