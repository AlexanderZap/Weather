package ru.zapashnii.weather.domain.model

/** Интерфейс списока элементов для отображения */
interface IListItemField {
    /** Заголовок */
    val title: String

    /** Подзаголовок */
    val subtitle: String?

    /** ID изображения */
    val iconRes: Int?

    /** Url изображения */
    val iconUrl: String?

    /** Цвет иконки */
    val colorIcon: Int?

    /** Цвет заголовка */
    val colorTitle: Int?

    /** Цвет подзаголовка */
    val colorSubtitle: Int?
}