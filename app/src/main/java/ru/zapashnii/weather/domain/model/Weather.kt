package ru.zapashnii.weather.domain.model

import com.google.gson.annotations.SerializedName

/**
 * TODO
 *
 * @property base
 * @property clouds         Облачность
 * @property cod
 * @property coord          Координаты города
 * @property dt             Время расчета данных, unix, UTC
 * @property id             ID города
 * @property main
 * @property name           Название города
 * @property sys
 * @property timezone       Сдвиг в секундах от UTC
 * @property visibility
 * @property weather
 * @property wind
 */
data class Weather(
    @SerializedName("base")
    val base: String,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("cod")
    val cod: Int,
    @SerializedName("coord")
    val coord: Coord,
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: Main,
    @SerializedName("name")
    val name: String,
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("timezone")
    val timezone: Int,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("weather")
    val weather: List<WeatherX>,
    @SerializedName("wind")
    val wind: Wind,
)

/**
 * Облачность
 *
 * @property all    Облачность,%
 */
data class Clouds(
    @SerializedName("all")
    val all: Int,
)

/**
 * Координаты города
 *
 * @property lat    Географическое положение города, долгота
 * @property lon    Географическое положение города, широта
 */
data class Coord(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
)

/**
 * TODO
 *
 * @property feels_like     Температура. Этот температурный параметр объясняет человеческое восприятие погоды.
 * @property humidity       Влажность, %
 * @property pressure       Атмосферное давление (на уровне моря, если нет данных sea_level или grnd_level), гПа
 * @property temp           Температура
 * @property temp_max       Максимальная температура на данный момент
 * @property temp_min       Минимальная температура на данный момент
 */
data class Main(
    @SerializedName("feels_like")
    val feels_like: Double,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("temp_max")
    val temp_max: Double,
    @SerializedName("temp_min")
    val temp_min: Double,
)

/**
 * TODO
 *
 * @property country    Код страны
 * @property id
 * @property message
 * @property sunrise
 * @property sunset
 * @property type
 */
data class Sys(
    @SerializedName("country")
    val country: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: Double,
    @SerializedName("sunrise")
    val sunrise: Int,
    @SerializedName("sunset")
    val sunset: Int,
    @SerializedName("type")
    val type: Int,
)

/**
 * TODO
 *
 * @property description
 * @property icon
 * @property id
 * @property main
 */
data class WeatherX(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
)

/**
 * TODO
 *
 * @property deg    Направление ветра, градусы (метеорологические)
 * @property speed  Скорость ветра
 * @property gust   Порыв ветра
 */
data class Wind(
    @SerializedName("deg")
    val deg: Int,
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("gust")
    val gust: Double,
)