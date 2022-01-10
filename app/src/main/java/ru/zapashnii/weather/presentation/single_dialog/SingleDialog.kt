package ru.zapashnii.weather.presentation.single_dialog

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.zapashnii.weather.R

/** Класс создания и показа диалогового окна */
class SingleDialog {

    private lateinit var singleDialog: AlertDialog

    /**
     * Создать и показать диалоговое окно с сообщением и кнопкой Ок. Перед показом закрывает все предыдущие диалоговые окна
     *
     * @param message       текст сообщения
     */
    fun singleAlertDialog(message: String, currentActivity: AppCompatActivity) {
        currentActivity.runOnUiThread {
            if (!::singleDialog.isInitialized || !singleDialog.isShowing) {

                val builder = MaterialAlertDialogBuilder(currentActivity)
                builder.let { dialog ->
                    dialog.setMessage(message)
                    dialog.setTitle(R.string.error)
                    dialog.setPositiveButton(R.string.ok) { _, _ -> }
                }
                singleDialog = builder.create()
                singleDialog.show()
            }
        }
    }
}
