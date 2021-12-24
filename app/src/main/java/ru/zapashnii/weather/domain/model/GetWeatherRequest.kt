package ru.zapashnii.weather.domain.model

/**
 * Параметры для получения информации о погоде
 *
 * @property name   название города
 * @property units  в каких единицах приходит температура, если null она приходит в Кельвинах
 * @property lang   на каком языке получить информации о погоде, если null на английском
 */
data class GetWeatherRequest(
    val name: String,
    val units: String? = null,
    val lang: String? = null,
)