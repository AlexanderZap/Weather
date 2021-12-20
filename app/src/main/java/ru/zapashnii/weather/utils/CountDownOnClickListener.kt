package ru.zapashnii.weather.utils

import android.os.CountDownTimer
import android.view.View

class CountDownOnClickListener(timeout: Long = 300L, val action: (view: View?)-> Unit): View.OnClickListener {
    private var isClickAvailable = true

    private val countdown: CountDownTimer = object : CountDownTimer(timeout, 1L) {
        override fun onFinish() { isClickAvailable = true }
        override fun onTick(millisUntilFinished: Long) {}
    }

    override fun onClick(v: View?) {
        if (isClickAvailable) {
            isClickAvailable = false
            countdown.start()
            action(v)
        }
    }
}