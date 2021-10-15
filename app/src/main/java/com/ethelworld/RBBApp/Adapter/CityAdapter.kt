package com.ethelworld.RBBApp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.ethelworld.RBBApp.Item.City
import java.util.*

@Suppress("UNCHECKED_CAST")
class CityAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val allCity: List<City?>) : ArrayAdapter<City>(context, layoutResource, allCity) {
    private var wholeCity: List<City?> = allCity

    override fun getCount(): Int {
        return wholeCity.size
    }

    override fun getItem(position: Int): City? {
        return wholeCity[position]
    }

    override fun getItemId(position: Int): Long {
        return wholeCity[position]?.id?.toLong()!!
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
            .inflate(layoutResource, parent, false) as TextView
        view.text = getItem(position)?.name
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                wholeCity = results?.values as List<City?>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.toLowerCase(Locale.ROOT)
                val filterResuts = FilterResults()
                filterResuts.values = if (queryString == null || queryString.isEmpty())
                    allCity
                else
                    allCity.filter {
                        it?.name?.toLowerCase(Locale.ROOT)!!.contains(queryString)
                    }
                return filterResuts
            }
        }
    }
}