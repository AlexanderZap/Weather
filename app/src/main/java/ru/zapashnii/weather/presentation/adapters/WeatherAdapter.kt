package ru.zapashnii.weather.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import ru.zapashnii.weather.R
import ru.zapashnii.weather.di.MainApp
import ru.zapashnii.weather.domain.model.Weather

class WeatherAdapter : PagerAdapter() {

    private var listItems = listOf<Weather>()
    private lateinit var layoutInflater: LayoutInflater

    override fun getCount(): Int = listItems.size

    override fun isViewFromObject(view: View, obj: Any): Boolean  = view == obj

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(MainApp.instance.applicationContext)
        val view = layoutInflater.inflate(R.layout.cv_item, container, false)

        view.setOnClickListener { }

        val ivCity = view.findViewById<ImageView>(R.id.ivCity)
        val tvCity = view.findViewById<TextView>(R.id.tvCity)
        val tvCountry = view.findViewById<TextView>(R.id.tvCountry)

        tvCity.text = listItems[position].name
        tvCountry.text = listItems[position].sys.country

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    fun setData(data: List<Weather>) {
        listItems = data

        notifyDataSetChanged()
    }
}