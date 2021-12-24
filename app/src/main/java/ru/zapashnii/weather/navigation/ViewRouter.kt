package ru.zapashnii.weather.navigation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.app.ActivityCompat
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.domain.model.IListItemField
import ru.zapashnii.weather.domain.model.ItemListParams
import ru.zapashnii.weather.presentation.dialog.SingleDialog
import ru.zapashnii.weather.presentation.item_list.ItemListBottomSheetFragment
import ru.zapashnii.weather.presentation.ui.base_activity.BaseActivity
import ru.zapashnii.weather.utils.Utils
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** Код запроса */
const val PERMISSION_ID = 1

/** Клас навигации по приложению */
@Singleton
class ViewRouter @Inject constructor() {

    private var currentActivity: BaseActivity? = null

    var progressStack: Int = 0

    fun setCurrentActivity(activity: BaseActivity?) {
        currentActivity = activity
    }

    fun removeCurrentActivity(baseActivity: BaseActivity) {
        if (currentActivity == baseActivity) currentActivity = null
    }

    /**
     * Открыть экран покза погоды
     *
     * @param cityName      название города
     */
    fun openWeatherByCity(cityName: String) {
        startWeatherActivity(BaseActivity.SEARCH_WEATHER, cityName)
    }

    /**
     * Открыть экран покза погоды
     *
     * @param cityName      название города
     */
    private fun startWeatherActivity(type: String, cityName: String) {
        if (currentActivity != null) {
            val intent = Intent(currentActivity, BaseActivity::class.java)
            intent.putExtra(BaseActivity.TYPE_ACTIVITY, type)
            intent.putExtra(BaseActivity.CITY_NAME, cityName)
            currentActivity?.startActivity(intent)
        }
    }

    /** Запросить разрешения на получения местоположения */
    fun requestPermission() {
        currentActivity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
                PERMISSION_ID
            )
        }
    }

    /**
     * Открывает приложение, способное обработать url, иначе показывает уведомление.
     * @param url           ссылка
     * @param errorMessage  текст ошибку в случае неудачи
     */
    fun showLink(url: String, errorMessage: String? = null) {
        if (currentActivity == null) return
        val uri = Uri.parse(url)
        val intent = getLinkIntent(uri)
        if (isNotNeedCheckLink(uri) || intent.resolveActivity(
                currentActivity?.packageManager ?: return
            ) != null
        ) {
            currentActivity?.startActivity(intent)
        } else {
            showError(errorMessage = errorMessage ?: "Нет приложения способного открыть $url")
        }
    }

    /** Получить интент для открытия ссылки */
    private fun getLinkIntent(uri: Uri?) = Intent(Intent.ACTION_VIEW).apply { data = uri }

    /** Проверить, что ссылку можно открыть без проверки наличия приложения назначения */
    private fun isNotNeedCheckLink(uri: Uri?) = uri?.scheme == "https" || uri?.scheme == "http"

    fun showError(errorMessage: String) {
        Toast.makeText(MainApp.instance.applicationContext, errorMessage, LENGTH_SHORT).show()
    }

    /**
     * Показать BottomSheetDialogFragment со списком элементов
     * @param title                 id заголовка
     * @param items                 список элементов
     * @param isNeedSowSearchField  флаг необходимости отобразить поисковую строку
     * @param onCanceled            действие при отмене
     * @param onItemSelected        действие при нажатии на элемент списка
     */
    fun showItemList(
        title: Int,
        items: List<IListItemField>,
        isNeedSowSearchField: Boolean = false,
        onCanceled: (() -> Unit)? = null,
        onItemSelected: ((item: IListItemField) -> Unit)? = null
    ) {
        showItemList(
            Utils.getString(title),
            items,
            isNeedSowSearchField,
            onCanceled,
            onItemSelected
        )
    }

    /**
     * Показать BottomSheetDialogFragment со списком элементов
     * @param title                 заголовок
     * @param items                 список элементов
     * @param isNeedSowSearchField  флаг необходимости отобразить поисковую строку
     * @param onCanceled            действие при отмене
     * @param onItemSelected        действие при нажатии на элемент списка
     */
    fun showItemList(
        title: String,
        items: List<IListItemField>,
        isNeedSowSearchField: Boolean = false,
        onCanceled: (() -> Unit)? = null,
        onItemSelected: ((item: IListItemField) -> Unit)? = null
    ) {
        val params = ItemListParams(
            title = title,
            items = items,
            isNeedSowSearchField = isNeedSowSearchField,
            onCanceled = onCanceled,
            onItemSelected = onItemSelected
        )
        showItemList(params)
    }

    /**
     * Показать BottomSheetDialogFragment со списком элементов
     * @param params            набор параметров для отображения всплывающего меню
     * @param onItemSelected    действие нажатия на элемент списка
     */
    fun showItemList(
        params: ItemListParams,
        onItemSelected: ((item: IListItemField) -> Unit)? = null
    ) {
        onItemSelected?.let { params.onItemSelected = it }
        currentActivity?.showBottomSheet(ItemListBottomSheetFragment.newInstance(params))
    }

    suspend fun selectItemList(params: ItemListParams): IListItemField? {
        return suspendCoroutine { continuation ->
            params.onItemSelected = { continuation.resume(it) }
            params.onCanceled = { continuation.resume(null) }

            if (params.items.size < 15) {
                currentActivity?.showBottomSheet(ItemListBottomSheetFragment.newInstance(params))
            } else {
                //currentActivity?.addFragmentWithAnimation(ItemListFragment.newInstance(params))
            }
        }

    }

    private val singleDialog: SingleDialog by lazy { SingleDialog() }

    /**
     * Создать и показать диалоговое окно с сообщением и кнопкой Ок. Перед показом закрывает все предыдущие диалоговые окна
     *
     * @param message       текст сообщения
     */
    fun singleAlertDialog(message: String) {
        currentActivity?.let { singleDialog.singleAlertDialog(message, it) }
    }

    /** Назад на один экран */
    fun back() {
        currentActivity?.onBackPressed()
    }
}