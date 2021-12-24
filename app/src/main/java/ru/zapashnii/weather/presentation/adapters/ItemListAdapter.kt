package ru.zapashnii.weather.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.zapashnii.weather.R
import ru.zapashnii.weather.databinding.ItemListFieldBinding
import ru.zapashnii.weather.domain.model.IListItemField
import ru.zapashnii.weather.utils.Utils
import ru.zapashnii.weather.utils.setCountdownOnClickListener
import ru.zapashnii.weather.utils.setTintColor

/**
 * Адаптер для BottomSheetDialog
 *
 * @property onSelected     нажатие на элемет
 */
class ItemListAdapter(
    val onSelected: ((listItemField: IListItemField) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<IListItemField>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_field, parent, false)
        val binding = ItemListFieldBinding.bind(view)

        return ItemChooserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as ItemChooserViewHolder).bind(items[position], onSelected)

    override fun getItemCount(): Int = items.size

    /**
     * Получить и обновить список с данными
     *
     * @param data          список с данными
     */
    fun setData(data: List<IListItemField>) {
        items = data
        notifyDataSetChanged()
    }

    /** ViewHolder для ячейки с IListItemField */
    class ItemChooserViewHolder(private val binding: ItemListFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: IListItemField, onSelected: (listItemField: IListItemField) -> Unit) {
            item.let {
                setTitle(it)
                setSubtitle(it)
                setIcon(it)
                setColor(it)
                setOnClickListener(it, onSelected)
            }
        }

        /**
         * Установить заголовок
         *
         * @param item  [IListItemField]
         */
        private fun setTitle(item: IListItemField) {
            binding.itemTitle.text = item.title
        }

        /**
         * Установить подзаголовок
         *
         * @param item  [IListItemField]
         */
        private fun setSubtitle(item: IListItemField) {
            if (!item.subtitle.isNullOrBlank()) {
                binding.itemSubtitle.text = item.subtitle
                binding.itemSubtitle.visibility = View.VISIBLE
            } else {
                binding.itemSubtitle.text = ""
                binding.itemSubtitle.visibility = View.GONE
            }
        }

        /**
         * Установить иконку
         *
         * @param item  [IListItemField]
         */
        private fun setIcon(item: IListItemField) {
            when {
                item.iconRes != null -> item.iconRes?.let { binding.itemIcon.setImageResource(it) }
                !item.iconUrl.isNullOrBlank() -> item.iconUrl?.let { binding.itemIcon.loadImage(it) }
                item.iconRes == null && item.iconUrl == null -> {
                    binding.iconContainer.visibility = View.GONE
                }
                else -> binding.itemIcon.visibility = View.INVISIBLE
            }
        }

        /**
         * Установить цвет
         *
         * @param item  [IListItemField]
         */
        private fun setColor(item: IListItemField) {
            val colorIcon = item.colorIcon ?: return
            val colorTitle = item.colorTitle ?: return
            val colorSubtitle = item.colorSubtitle ?: return

            Utils.getColor(colorIcon).let { color ->
                binding.itemIcon.setTintColor(color)
            }

            Utils.getColor(colorTitle).let { color ->
                binding.itemTitle.setTextColor(color)
            }

            Utils.getColor(colorSubtitle).let { color ->
                binding.itemSubtitle.setTextColor(color)
            }
        }

        /**
         * Добавить слушатель нажатия
         *
         * @param item  [IListItemField]
         */
        private fun setOnClickListener(
            item: IListItemField,
            onSelected: (listItemField: IListItemField) -> Unit
        ) {
            binding.root.isClickable = true
            binding.root.setCountdownOnClickListener { onSelected(item) }
        }
    }
}