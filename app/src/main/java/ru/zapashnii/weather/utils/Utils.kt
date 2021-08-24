package ru.zapashnii.weather.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import ru.zapashnii.weather.di.MainApp

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

    /** Перенаправить на Play Market */
    fun redirectToPlayMarket() {
        val context = MainApp.instance.applicationContext
        val packageName = context?.packageName ?: ""
        val intent = Intent(Intent.ACTION_VIEW)
        if (!context.isGooglePlayServicesAvailable()) {
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        } else {
            intent.data = Uri.parse("market://details?id=$packageName")
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context?.startActivity(intent)
    }
}