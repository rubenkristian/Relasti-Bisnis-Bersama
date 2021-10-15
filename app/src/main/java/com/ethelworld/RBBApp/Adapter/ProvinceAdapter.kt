package com.ethelworld.RBBApp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.ethelworld.RBBApp.Item.Province
import java.util.*

@Suppress("UNCHECKED_CAST")
class ProvinceAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val allProvince: List<Province?>)
    : ArrayAdapter<Province>(context, layoutResource, allProvince), Filterable {
    private var wholeProvince: List<Province?> = listOf()

    override fun getCount(): Int {
        return wholeProvince.size
    }

    override fun getItem(position: Int): Province? {
        return wholeProvince[position]
    }

    override fun getItemId(position: Int): Long {
        return wholeProvince[position]?.id?.toLong()!!
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView =
            convertView as TextView? ?: LayoutInflater
                .from(context)
                .inflate(layoutResource, parent, false) as TextView

        val name = getItem(position)?.name
        view.text = String.format("%s", name)
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                wholeProvince = results?.values as List<Province?>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.toLowerCase(Locale.ROOT)
                val filterResult = FilterResults()
                filterResult.values = if(queryString == null || queryString.isEmpty())
                    allProvince
                else
                    allProvince.filter {
                        it?.name?.toLowerCase(Locale.ROOT)!!.contains(queryString)
                    }
                return filterResult
            }
        }
    }
}