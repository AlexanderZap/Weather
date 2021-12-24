package ru.zapashnii.weather.presentation.itemList

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.zapashnii.weather.domain.model.IListItemField
import ru.zapashnii.weather.domain.model.ItemListParams

/**
 * ViewModel для ItemListBottomSheetFragment и ItemListFragment
 */
class ItemListViewModel : ViewModel() {
    private val paramsLD = MutableLiveData<ItemListParams?>(null)

    /** Используется для фильтрации списка элементов */
    val queryLD = MutableLiveData("")

    var params: ItemListParams? = null
    private var onItemSelected = params?.onItemSelected
    private var onCancelled = params?.onCanceled

    /**
     * Подписаться на список элементов. Можно использовать в связке с BindingAdapter("bind:items")
     * @return  LiveData со списком элементов
     */
    fun getItemList(): LiveData<List<IListItemField>> {
        val result = MediatorLiveData<List<IListItemField>>()

        val onChanged = {
            val queryStr = queryLD.value?.trim()
            val items = paramsLD.value?.items ?: listOf()

            result.value = if (queryStr.isNullOrEmpty()) items else {
                items.filter {
                    "${it.title}${it.subtitle ?: ""}".contains(queryStr, true)
                }
            }
        }

        result.addSource(paramsLD) { onChanged.invoke() }
        result.addSource(queryLD) { onChanged.invoke() }

        return result
    }

    /**
     * Выбор элемента из списка
     *
     * @param item  выбранный элемент
     */
    fun onSelected(item: IListItemField) {
        params?.onCanceled = null
        params?.onItemSelected?.invoke(item)
    }

    override fun onCleared() {
        params?.onCanceled?.invoke()
        params = null
        paramsLD.value = null
        queryLD.value = null
        super.onCleared()
    }

    @MainThread
    fun setItemListParams(itemListParams: ItemListParams) {
        paramsLD.value = itemListParams
    }
}