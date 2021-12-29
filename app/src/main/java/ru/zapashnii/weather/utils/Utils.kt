package ru.zapashnii.weather.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.utils.inputmask.helper.Mask
import ru.zapashnii.weather.utils.inputmask.model.CaretString
import java.util.*

/** Утилиты */
object Utils {

    /**
     * Скрыть клавиатуру
     *
     * @param activity      текущий экран [activity]
     * @return              true, если скрыта
     */
    fun hideSoftKeyboard(activity: Activity): Boolean {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        return imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Скрыть клавиатуру
     *
     * @param fragmentActivity      текущий экран [FragmentActivity]
     * @return                      true, если скрыта
     */
    fun hideSoftKeyboard(fragmentActivity: FragmentActivity): Boolean {
        val imm = fragmentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = fragmentActivity.currentFocus
        if (view == null) {
            view = View(fragmentActivity)
        }
        return imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /** Перенаправить на Play Market или AppGallery */
    fun redirectToPlayMarket() {
        val context = MainApp.instance.applicationContext
        val packageName = context?.packageName ?: ""
        val intent = Intent(Intent.ACTION_VIEW)
        if (!context.isGooglePlayServicesAvailable()) {
            intent.data = Uri.parse("appmarket://details?id=$packageName")
        } else {
            intent.data = Uri.parse("market://details?id=$packageName")
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context?.startActivity(intent)
    }

    /**
     * Получить название города
     *
     * @param latitude          широта
     * @param longitude         долгота
     * @return                  название города или ""
     */
    fun getCityName(latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(MainApp.instance.applicationContext, Locale.getDefault())
        val address = geoCoder.getFromLocation(latitude, longitude, 3)

        return address[0].locality ?: ""
    }

    /**
     * Получить название страны
     *
     * @param latitude          широта
     * @param longitude         долгота
     * @return                  название страны или ""
     */
    fun getCountryName(latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(MainApp.instance.applicationContext, Locale.getDefault())
        val address = geoCoder.getFromLocation(latitude, longitude, 3)

        return address[0].countryName ?: ""
    }

    /**
     * Проверка разрешений на получения местоположения
     *
     * @return      True, если есть разрешения
     */
    fun checkPermission(): Boolean =
        ActivityCompat.checkSelfPermission(MainApp.instance.applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainApp.instance.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /**
     * Проверка включен ли GPS
     *
     * @return      True, если включен
     */
     fun isLocationEnabled(): Boolean {
        val locationManager =  MainApp.instance.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Получить текст по [StringRes]
     *
     * @param name  [StringRes]
     * @return      текст
     */
    fun getString(@StringRes name: Int): String {
        return MainApp.instance.getString(name)
    }

    /**
     * Получить цвет по [ColorRes]
     *
     * @param id    [ColorRes]
     * @return      цвет
     */
    fun getColor(@ColorRes id: Int): Int {
        return MainApp.instance.getColor(id)
    }

    /**
     * Преоброзовать номер телефона в маску +[0] ([000]) [000]-[00]-[00]
     *
     * @param phoneNumber   номер телефона
     * @return              измененная строка
     */
    fun formatPhoneNumber(phoneNumber: String): String {
        return formatText(phoneNumber, Mask("+[0] ([000]) [000]-[00]-[00]"))
    }

    /**
     * Преоброзовать строку [text] в маску [mask]
     *
     * @param text      строка
     * @param mask      маска
     * @return          измененная строка
     */
    fun formatText(text: String, mask: Mask): String {
        return try {
            val caretString = CaretString(text, text.length, CaretString.CaretGravity.FORWARD(true))
            val result = mask.apply(caretString)
            val output = result.formattedText.string

            output
        } catch (ex: Exception) {
            text
        }
    }

    /**
     * Спрятать клавиатуру
     *
     * @param context   [Context]
     * @param view      [View]
     */
    fun hideSoftKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}