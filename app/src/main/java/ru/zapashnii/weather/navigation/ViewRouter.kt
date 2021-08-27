package ru.zapashnii.weather.navigation

import android.content.Intent
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.domain.model.IListItemField
import ru.zapashnii.weather.domain.model.ItemListParams
import ru.zapashnii.weather.presentation.ui.base_activity.BaseActivity
import javax.inject.Inject
import javax.inject.Singleton

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

    /** Запускает Activity погаза погоды TODO с параметром = название города*/
    fun openWeatherByCity(cityName: String) {
        startWeatherActivity(BaseActivity.SEARCH_WEATHER, cityName)
    }

    private fun startWeatherActivity(type: String, cityName: String) {
        if (currentActivity != null) {
            val intent = Intent(currentActivity, BaseActivity::class.java)
            intent.putExtra(BaseActivity.TYPE_ACTIVITY, type)
            intent.putExtra(BaseActivity.CITY_NAME, cityName)
            currentActivity?.startActivity(intent)
        }
    }

    /** Запускает Activity погаза погоды TODO с параметром = гео локация*/
/*    fun openWeatherByGPS() {
        val intent = Intent(currentActivity, WeatherActivity::class.java)
        currentActivity?.startActivity(intent)
    }*/

    /**
     * Показать BottomSheetDialogFragment со списком элементов
     * @param title                 id заголовка
     * @param items                 список элементов
     * @param isNeedSowSearchField  флаг необходимости отобразить поисковую строку
     * @param onCanceled            действие при отмене
     * @param onItemSelected        действие при нажатии на элемент списка
     */
/*    fun showItemList(title: Int, items: List<IListItemField>, isNeedSowSearchField: Boolean = false, onCanceled: (() -> Unit)? = null, onItemSelected: ((item: IListItemField) -> Unit)? = null) {
        showItemList(MainApp.getString(title), items, isNeedSowSearchField, onCanceled, onItemSelected)
    }*/

    /**
     * Показать BottomSheetDialogFragment со списком элементов
     * @param title                 заголовок
     * @param items                 список элементов
     * @param isNeedSowSearchField  флаг необходимости отобразить поисковую строку
     * @param onCanceled            действие при отмене
     * @param onItemSelected        действие при нажатии на элемент списка
     */
/*    fun showItemList(title: String, items: List<IListItemField>, isNeedSowSearchField: Boolean = false, onCanceled: (() -> Unit)? = null, onItemSelected: ((item: IListItemField) -> Unit)? = null) {
        val params = ItemListParams(
            title = title,
            items = items,
            isNeedSowSearchField = isNeedSowSearchField,
            onCanceled = onCanceled,
            onItemSelected = onItemSelected
        )
        showItemList(params)
    }*/

    /**
     * Показать BottomSheetDialogFragment со списком элементов
     * @param params            набор параметров для отображения всплывающего меню
     * @param onItemSelected    действие нажатия на элемент списка
     */
/*    fun showItemList(params: ItemListParams, onItemSelected: ((item: IListItemField) -> Unit)? = null) {
        onItemSelected?.let { params.onItemSelected = it }
        currentActivity?.showBottomSheet(ItemListBottomSheetFragment.newInstance(params))
    }*/
}