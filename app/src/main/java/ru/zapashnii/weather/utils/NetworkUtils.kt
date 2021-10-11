package ru.zapashnii.weather.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/** Сетевые утилиты */
object NetworkUtils {

    /**
     * Проверка наличия интернет-соединения
     *
     * @param context       context
     * @return              True, если есть интернет-соединение
     */
    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true// Wi-Fi
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true// Сотовая связь (мобильные данные, 3G / 4G / LTE, что угодно)
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    when (type) {
                        ConnectivityManager.TYPE_WIFI -> true // Wi-Fi
                        ConnectivityManager.TYPE_MOBILE -> true // Сотовая связь (мобильные данные, 3G / 4G / LTE, что угодно)
                        else -> false
                    }
                }
            }
        }
        return result ?: false
    }
}