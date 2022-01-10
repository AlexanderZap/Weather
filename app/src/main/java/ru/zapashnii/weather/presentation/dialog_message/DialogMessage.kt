package ru.zapashnii.weather.presentation.dialog_message

import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ru.zapashnii.weather.R
import ru.zapashnii.weather.di.MainApp
import java.lang.ref.WeakReference

/**
 * Класс показа [AlertDialog]
 *
 * @property activity   [AppCompatActivity]
 */
class DialogMessage(private var activity: WeakReference<AppCompatActivity?>) {
    private var currentDialog: AlertDialog? = null

    /** LifecycleObserver при вызове ON_DESTROY удаляет слушателя и очищает этот объект */
    private val activityLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onActivityDestroy() {
            activity.get()?.lifecycle?.removeObserver(this)
            activity.clear()
        }
    }

    init {
        activity.get()?.lifecycle?.removeObserver(activityLifecycleObserver)
        activity.get()?.lifecycle?.addObserver(activityLifecycleObserver)
    }

    /**
     * Подготовить и показать [Snackbar]
     *
     * @param message               сообщение
     * @param backgroundColorRes    цвет фона
     * @param textColorRes          цвет текста
     * @param actionColorRes        цвет действия
     * @param actionText            текст действия
     * @param action                нажатие на действие
     */
    fun showSnackBar(
        message: String,
        @ColorRes backgroundColorRes: Int? = null,
        @ColorRes textColorRes: Int? = null,
        @ColorRes actionColorRes: Int? = null,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        val currentFragment = activity.get()?.supportFragmentManager?.fragments?.last()

        val view = if (currentFragment != null && currentFragment is DialogFragment) {
            currentFragment.view?.rootView
        } else {
            activity.get()?.findViewById<ViewGroup?>(android.R.id.content)?.getChildAt(0)
        }

        val snackBar = view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT) }

        backgroundColorRes?.let { snackBar?.view?.background?.setTint(MainApp.instance.getColor(it)) }
        textColorRes?.let { snackBar?.setTextColor(MainApp.instance.getColor(it)) }

        if (action != null && !actionText.isNullOrBlank()) {
            actionColorRes?.let { snackBar?.setActionTextColor(MainApp.instance.getColor(it)) }
            snackBar?.setAction(actionText) { action.invoke() }
        }

        snackBar?.show()
    }

    /**
     * Подготовить и показать [AlertDialog]
     *
     * @param messageConfig         Класс с параметрами(настройками) необходимыми для создания [AlertDialog]
     * @param isNeedOnNegative      True, если негативная кнопка нужна
     */
    fun showDialogMessage(messageConfig: MessageConfig, isNeedOnNegative: Boolean = true) {
        val activity = activity.get() ?: return

        activity.runOnUiThread {
            val builder = MaterialAlertDialogBuilder(activity)

            builder.let { dialog ->
                dialog.setOnDismissListener { currentDialog = null }

                messageConfig.icon?.let { icon -> dialog.setIcon(icon) }
                dialog.setCancelable(messageConfig.isCancelable)

                setTitle(dialog, messageConfig)
                setMessage(dialog, messageConfig)
                setPositiveButton(dialog, messageConfig)
                if (isNeedOnNegative)
                    setNegativeButton(dialog, messageConfig)
            }

            currentDialog = builder.create()
            if (!activity.isFinishing) {
                currentDialog?.show()
            }
        }
    }

    /**
     * Установить заголовок [AlertDialog]
     *
     * @param dialog            [MaterialAlertDialogBuilder]
     * @param messageConfig     Класс с параметрами(настройками) необходимыми для создания [AlertDialog]
     */
    private fun setTitle(dialog: MaterialAlertDialogBuilder, messageConfig: MessageConfig) {
        val dialogTitle = getStringOrRes(messageConfig.title, messageConfig.titleRes)

        dialogTitle?.let { dialog.setTitle(it) }
    }

    /**
     * Установить сообщение [AlertDialog]
     *
     * @param dialog            [MaterialAlertDialogBuilder]
     * @param messageConfig     Класс с параметрами(настройками) необходимыми для создания [AlertDialog]
     */
    private fun setMessage(dialog: MaterialAlertDialogBuilder, messageConfig: MessageConfig) {
        val dialogMessage = getStringOrRes(messageConfig.message, messageConfig.messageRes)

        dialogMessage?.let { dialog.setMessage(it) }
    }

    /**
     * Установить позитивную кнопку [AlertDialog]
     *
     * @param dialog            [MaterialAlertDialogBuilder]
     * @param messageConfig     Класс с параметрами(настройками) необходимыми для создания [AlertDialog]
     */
    private fun setPositiveButton(
        dialog: MaterialAlertDialogBuilder,
        messageConfig: MessageConfig
    ) {
        val dialogPositiveText =
            getStringOrRes(messageConfig.positiveText, messageConfig.positiveTextRes)

        if (dialogPositiveText != null) {
            dialog.setPositiveButton(dialogPositiveText) { _, _ -> messageConfig.onPositive?.invoke() }
        } else {
            dialog.setPositiveButton(R.string.ok) { _, _ -> messageConfig.onPositive?.invoke() }
        }
    }


    /**
     * Установить негативную кнопку [AlertDialog]
     * Если передан negativeText ИЛИ negativeTextRes ИЛИ назначено действие onPositive(), то появится вторая кнопка
     *
     * @param dialog            [MaterialAlertDialogBuilder]
     * @param messageConfig     Класс с параметрами(настройками) необходимыми для создания [AlertDialog]
     */
    private fun setNegativeButton(
        dialog: MaterialAlertDialogBuilder,
        messageConfig: MessageConfig
    ) {
        val hasNegativeText =
            !messageConfig.negativeText.isNullOrBlank() || messageConfig.negativeTextRes != null
        val hasOnPositive = messageConfig.onPositive != null

        if (hasNegativeText || hasOnPositive) {
            val negativeText: String =
                getStringOrRes(messageConfig.negativeText, messageConfig.negativeTextRes)
                    ?: MainApp.instance.getString(R.string.cancel)

            if (messageConfig.onNegative != null) {
                dialog.setNegativeButton(negativeText) { _, _ -> messageConfig.onNegative.invoke() }
            } else {
                dialog.setNegativeButton(negativeText) { dialogInterface, _ -> dialogInterface.dismiss() }
            }
        }
    }

    /**
     * Получить текст заголовка
     *
     * @param string        заголовок
     * @param stringRes     [DrawableRes] заголовка
     * @return              текст заголовка
     */
    private fun getStringOrRes(string: String?, @StringRes stringRes: Int?): String? {
        return if (!string.isNullOrBlank()) {
            string
        } else {
            stringRes?.let { MainApp.instance.getString(it) }
        }
    }

    /**
     * Класс с параметрами(настройками) необходимыми для создания [AlertDialog]
     *
     * @property icon               [DrawableRes] иконки
     * @property isCancelable       можно отменить
     * @property title              заголовок
     * @property titleRes           [DrawableRes] заголовка
     * @property message            сообщение
     * @property messageRes         [DrawableRes] сообщения
     * @property negativeText       текст для отрицательной кнопки
     * @property negativeTextRes    [DrawableRes] текста для отрицательной кнопки
     * @property onNegative         нажатие на отрицательнуюд кнопку
     * @property positiveText       текст для положительной кнопки
     * @property positiveTextRes    [DrawableRes] текста для положительной кнопки
     * @property onPositive         нажатие на положительную кнопку
     */
    data class MessageConfig(
        @DrawableRes var icon: Int? = null,
        var isCancelable: Boolean = true,
        var title: String = "", @StringRes var titleRes: Int? = null,
        val message: String? = null, @StringRes val messageRes: Int? = null,
        val negativeText: String? = null, @StringRes val negativeTextRes: Int? = null,
        val onNegative: (() -> Unit)? = null,
        val positiveText: String? = null, @StringRes val positiveTextRes: Int = R.string.ok,
        val onPositive: (() -> Unit)? = null
    )
}