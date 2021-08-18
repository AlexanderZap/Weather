package ru.zapashnii.weather.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity

object Utils {

    fun hideSoftKeyboard(activity: Activity): Boolean {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        return imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideSoftKeyboard(fragmentActivity: FragmentActivity): Boolean {
        val imm = fragmentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = fragmentActivity.currentFocus
        if (view == null) {
            view = View(fragmentActivity)
        }
        return imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}