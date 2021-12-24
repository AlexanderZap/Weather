package ru.zapashnii.weather.presentation.item_list

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.zapashnii.weather.R
import ru.zapashnii.weather.databinding.BottomSheetItemListBinding
import ru.zapashnii.weather.domain.model.IListItemField
import ru.zapashnii.weather.domain.model.ItemListParams
import ru.zapashnii.weather.presentation.adapters.ItemListAdapter
import ru.zapashnii.weather.utils.Utils

/**  Фрагмент BottomSheetDialog. Для создания необходим набор параметров [ItemListParams] */
class ItemListBottomSheetFragment : BottomSheetDialogFragment() {
    private var binding: BottomSheetItemListBinding? = null
    private var params: ItemListParams? = null

    private lateinit var dialog: BottomSheetDialog
    private lateinit var behavior: BottomSheetBehavior<View>

    private var adapter = ItemListAdapter(
        onSelected = { listItemField: IListItemField ->
            binding?.viewModel?.onSelected(listItemField)
            dismiss()
        }
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            sheet?.let { view ->
                behavior = BottomSheetBehavior.from(view)
                behavior.isHideable = false
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_item_list, container, false)

        binding = DataBindingUtil.bind(view)
        binding?.viewModel = ViewModelProvider(this).get(ItemListViewModel::class.java)
        binding?.lifecycleOwner = this

        settingAdjustResizeMode()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.title?.text = params?.title

        binding?.itemsList?.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding?.itemsList?.adapter = adapter

        params?.let { binding?.viewModel?.params = it }

        binding?.viewModel?.params?.let { listParams ->
            binding?.viewModel?.setItemListParams(listParams)
            showItems(listParams.items)
            setImage(listParams)
            setDescription(listParams.description)
            setColor(listParams.headerColor)
            setSearchField(listParams.isNeedSowSearchField)
        }

        binding?.itemsList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                /* Если RecyclerView в самой верхней позиции, то диалог можно закрыть смахиванием,
                иначе смахивание будет заблокировано, пока RecyclerView не окажется в самой верхней позиии */
                val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                behavior.isHideable = firstVisibleItemPosition == 0
            }
        })
    }

    /** Настройка повидения BottomSheetDialogFragment при появлении системной клавиатуры. */
    private fun settingAdjustResizeMode() {
        if (params?.isNeedSowSearchField != true) return
        /*
        * Этот код делает так, чтобы BottomSheetDialogFragment был всегда над системной клавиатурой
        * https://coderoad.ru/50223392/Android-%D0%BF%D0%BE%D0%BA%D0%B0%D0%B7%D0%B0%D1%82%D1%8C-BottomSheetDialogFragment-%D0%BD%D0%B0%D0%B4-%D0%BA%D0%BB%D0%B0%D0%B2%D0%B8%D0%B0%D1%82%D1%83%D1%80%D0%BE%D0%B9
        */
        if (requireDialog().window != null) {
            requireDialog().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        binding?.search?.setOnFocusChangeListener { _, hasFocus ->
            // Если фокус в поисковой строке, растянуть BottomSheetDialogFragment на весь экран.
            if (hasFocus) {
                setMinHeightForList(R.dimen.min_height_for_list_in_search_bottom_sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = binding?.root?.height ?: 80
                behavior.isHideable = false
            }
        }
    }

    /**
     * Установить минимальную высоту списка
     * @param minHeight     id ресурса с минимальной высотой
     */
    private fun setMinHeightForList(@DimenRes minHeight: Int) {
        binding?.itemsList?.minimumHeight =
            requireContext().resources.getDimension(minHeight).toInt()
    }

    /**
     * Установить видимость строки поиска.
     * @param needSowSearchField    флаг видимости строки поиска.
     */
    private fun setSearchField(needSowSearchField: Boolean) {
        binding?.searchContainer?.visibility = if (needSowSearchField) View.VISIBLE else View.GONE
    }

    /**
     * Установить изображения
     * @param params    набор параметров, содержащий id ресурсов изображения или url
     */
    private fun setImage(params: ItemListParams) {
        if (params.imageRes != null) {
            binding?.image?.visibility = View.VISIBLE
            binding?.image?.setImageResource(params.imageRes)
        } else if (!params.imageUrl.isNullOrBlank()) {
            binding?.image?.visibility = View.VISIBLE
            binding?.image?.loadImage(params.imageUrl)
        } else {
            binding?.image?.visibility = View.GONE
        }
    }

    /**
     * Установить текст описания
     * @param description   текст описания
     */
    private fun setDescription(description: String?) {
        binding?.description?.setTextOrHide(description)
    }

    /**
     * Установить оттенок цвета изображения в заголовке
     * @param colorRes  id цветового ресурса
     */
    private fun setColor(@ColorRes colorRes: Int?) {

        if (colorRes != null) {
            binding?.image?.setTintColorRes(colorRes)
            Utils.getColor(colorRes).let {
                binding?.title?.setTextColor(it)
                binding?.description?.setTextColor(it)
            }
        } else {
            Utils.getColor(R.color.black).let {
                binding?.title?.setTextColor(it)
                binding?.description?.setTextColor(it)
            }
        }
    }

    /**
     * Показать список элементов
     * @param items список элементов типа [IListItemField]
     */
    private fun showItems(items: List<IListItemField>) {
        adapter.setData(items)
    }

    companion object {
        /**
         * Создать экземпляр фрагмента
         * @param params    набор параметров необходимых для работы экрана
         * @return          фрагмент
         */
        fun newInstance(params: ItemListParams): ItemListBottomSheetFragment {
            return ItemListBottomSheetFragment().apply {
                this.params = params
            }
        }
    }
}