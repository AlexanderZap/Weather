package ru.zapashnii.weather.domain.model

/**
 * Набор параметров для настройки отображения BottomSheetDialogFragment
 * @property title                      заголовок
 * @property description                подзаголовок
 * @property imageRes                   id изображения в заголовке
 * @property imageUrl                   url изображения в заголовке
 * @property headerColor                оттенок изображения в заголовке
 * @property isNeedSowSearchField       флаг необходимости отображения поисковой строки
 * @property items                      список элементов для отображения
 * @property onCanceled                 действие при отмене
 * @property onItemSelected             действие при нажатии на элемент списка
 */
data class ItemListParams(
    val title: String,
    val description: String? = null,
    val imageRes: Int? = null,
    val imageUrl: String? = null,
    val headerColor: Int? = null,
    val isNeedSowSearchField: Boolean = false,
    val items: List<ListItemField>,
    var onCanceled: (() -> Unit)? = null,
    var onItemSelected: ((item: ListItemField) -> Unit)? = null,
)