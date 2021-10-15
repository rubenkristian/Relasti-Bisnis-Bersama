package com.ethelworld.RBBApp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.ethelworld.RBBApp.Item.Bank
import java.util.*

@Suppress("UNCHECKED_CAST")
class BankAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val allBank: List<Bank?>) : ArrayAdapter<Bank>(context, layoutResource, allBank) {
    private var wholeBank: List<Bank?> = allBank

    override fun getCount(): Int {
        return wholeBank.size
    }

    override fun getItem(position: Int): Bank? {
        return wholeBank[position]
    }

    override fun getItemId(position: Int): Long {
        return wholeBank[position]?.id?.toLong()?:0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView =
            convertView as TextView? ?: LayoutInflater
                .from(context)
                .inflate(layoutResource, parent, false) as TextView
        view.text = allBank[position]?.name
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                wholeBank = results?.values as List<Bank?>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.toLowerCase(Locale.ROOT)
                val filterResuts = FilterResults()
                filterResuts.values = if (queryString == null || queryString.isEmpty())
                    allBank
                else
                    allBank.filter {
                        it?.name?.toLowerCase(Locale.ROOT)!!.contains(queryString)
                    }
                return filterResuts
            }
        }
    }
}