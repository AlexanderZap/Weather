package ru.zapashnii.weather.presentation.ui.base_activity

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import ru.zapashnii.weather.R

/** Базовый fragment */
abstract class BaseFragment : Fragment() {

    var toolBar: Toolbar? = null

    /**
     * Установить заголовок в Toolbar
     *
     * @param title             [StringRes] текста заголовка
     * @param withBackButton    True, если нужна кнопка назад
     * @param view              экран c Toolbar
     */
    protected fun setToolbar(@StringRes title: Int, withBackButton: Boolean, view: View) {
//        val collapsingToolbarLayout = view.findViewById(R.id.collapsing) as? CollapsingToolbarLayout
//        collapsingToolbarLayout?.isTitleEnabled = false

        toolBar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(title)
        if (withBackButton) {
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setDisplayShowHomeEnabled(true)
            toolBar?.setNavigationOnClickListener { activity?.onBackPressed() }
            actionBar?.setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_back_arrow_white
                )
            )
        }
    }

    /**
     * Установить заголовок в Toolbar
     *
     * @param title             текст заголовка
     * @param withBackButton    True, если нужна кнопка назад
     * @param view              экран c Toolbar
     */
    protected fun setToolbar(title: String, withBackButton: Boolean, view: View) {
//        val collapsingToolbarLayout = view.findViewById(R.id.collapsing) as? CollapsingToolbarLayout
//        collapsingToolbarLayout?.isTitleEnabled = false

        toolBar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(title)
        if (withBackButton) {
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setDisplayShowHomeEnabled(true)
            toolBar?.setNavigationOnClickListener { activity?.onBackPressed() }
            actionBar?.setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_back_arrow_white
                )
            )
        }
    }

    override fun onDestroy() {
        toolBar?.setNavigationOnClickListener(null)
        super.onDestroy()
    }
}