package ru.zapashnii.weather.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Информация о погоде
 *
 * @property base           Внутренний параметр
 * @property clouds         Облачность
 * @property cod            Внутренний параметр
 * @property coord          Координаты города
 * @property dt             Время расчета данных, unix, UTC
 * @property id             ID города
 * @property main           Информация о температуре
 * @property name           Название города
 * @property sys            Информация о стране
 * @property timezone       Сдвиг в секундах от UTC
 * @property visibility     Видимость, метр
 * @property weather        Информация о погоде
 * @property wind           Информация о ветре
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
 * Информация о температуре
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
 * Информация о стране
 *
 * @property country    Код страны
 * @property id         Внутренний параметр
 * @property message    Внутренний параметр
 * @property sunrise    Время восхода, unix, UTC
 * @property sunset     Время заката, unix, UTC
 * @property type       Внутренний параметр
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
 * Информация о погоде
 *
 * @property description    Идентификатор значка погоды
 * @property icon           Погодные условия в группе.
 * @property id             Идентификатор погодных условий
 * @property main           Группа погодных параметров (Дождь, Снег, Экстрим и др.)
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
 * Информация о ветре
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