package ru.zapashnii.weather.utils

import android.util.Log

/** Утилита для логирования */
object Logger {
    const val TAG = "TAG"

    /**
     * Залогировать [Log.ERROR]
     *
     * @param exception ошибка/исключение
     */
    fun logError(exception: Throwable? = null) {
        logError(TAG, exception?.localizedMessage ?: "Exception", exception)
    }

    /**
     * Залогировать [Log.ERROR]
     *
     * @param message   сообщение в логе
     * @param exception ошибка/исключение
     */
    fun logError(message: String, exception: Throwable? = null) {
        logError(TAG, message, exception)
    }

    /**
     * Залогировать [Log.ERROR]
     *
     * @param tag       TAG лога
     * @param message   сообщение в логе
     * @param exception ошибка/исключение
     */
    fun logError(tag: String, message: String, exception: Throwable? = null) {
        logToConsole(Log.ERROR, tag, message, exception)
    }

    /**
     * Залогировать [Log.DEBUG]
     *
     * @param message   сообщение в логе
     * @param exception ошибка/исключение
     */
    fun logDebug(message: String, exception: Throwable? = null) {
        logDebug(TAG, message, exception)
    }

    /**
     * Залогировать [Log.DEBUG]
     *
     * @param tag       TAG лога
     * @param message   сообщение в логе
     * @param exception ошибка/исключение
     */
    fun logDebug(tag: String, message: String, exception: Throwable? = null) {
        logToConsole(Log.DEBUG, tag, message, exception)
    }

    /**
     * Залогировать в Console
     *
     * @param priority  приоритет
     * @param tag       TAG лога
     * @param message   сообщение в логе
     * @param exception ошибка/исключение
     */
    private fun logToConsole(priority: Int, tag: String, message: String, exception: Throwable? = null) {
        when (priority) {
            Log.ERROR -> {
                Log.e(tag, message, exception)
            }
            Log.WARN -> {
                Log.w(tag, message, exception)
            }
            Log.INFO -> {
                Log.i(tag, message, exception)
            }
            Log.VERBOSE -> {
                Log.v(tag, message, exception)
            }
            Log.DEBUG -> {
                Log.d(tag, message, exception)
            }
            else -> {
                Log.d(tag, message, exception)
            }
        }
    }
}