package ru.zapashnii.weather.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.zapashnii.weather.R
import ru.zapashnii.weather.databinding.ItemListFieldBinding
import ru.zapashnii.weather.domain.model.ListItemField
import ru.zapashnii.weather.utils.Utils
import ru.zapashnii.weather.utils.setCountdownOnClickListener
import ru.zapashnii.weather.utils.setTintColor

/**
 * Адаптер для BottomSheetDialog
 *
 * @property onSelected     нажатие на элемет
 */
class ItemListAdapter(
    val onSelected: ((listItemField: ListItemField) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<ListItemField>()


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
    fun setData(data: List<ListItemField>) {
        items = data
        notifyDataSetChanged()
    }

    /** ViewHolder для ячейки с IListItemField */
    class ItemChooserViewHolder(private val binding: ItemListFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListItemField, onSelected: (listItemField: ListItemField) -> Unit) {
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
         * @param item  [ListItemField]
         */
        private fun setTitle(item: ListItemField) {
            binding.itemTitle.text = item.title
        }

        /**
         * Установить подзаголовок
         *
         * @param item  [ListItemField]
         */
        private fun setSubtitle(item: ListItemField) {
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
         * @param item  [ListItemField]
         */
        private fun setIcon(item: ListItemField) {
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
         * @param item  [ListItemField]
         */
        private fun setColor(item: ListItemField) {
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
         * @param item  [ListItemField]
         */
        private fun setOnClickListener(
            item: ListItemField,
            onSelected: (listItemField: ListItemField) -> Unit
        ) {
            binding.root.isClickable = true
            binding.root.setCountdownOnClickListener { onSelected(item) }
        }
    }
}