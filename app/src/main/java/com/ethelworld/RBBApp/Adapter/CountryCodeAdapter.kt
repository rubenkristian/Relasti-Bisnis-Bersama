package com.ethelworld.RBBApp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Item.CountryCode
import com.ethelworld.RBBApp.R
import java.util.*

class CountryCodeAdapter(
    private val context: Context,
    private val countryCodeList: ArrayList<CountryCode>,
    private val listener: (CountryCode) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class CountryCodeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
//    @SuppressLint("ViewHolder")
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val rowView = inflater.inflate(R.layout.country_list_item, parent, false)
//
//        val name = rowView.findViewById<TextView>(R.id.txtViewCountryName)
//        val flag = rowView.findViewById<ImageView>(R.id.imgViewFlag)
//
//        val g = values[position].split(",")
//        name.text = getCountryZipCode(g[1].trim())
//        val png = g[1].trim().toLowerCase(Locale.ROOT)
//        flag.setImageResource(
//            context.resources.getIdentifier(
//                "drawable/$png",
//                null,
//                context.packageName))
//        return rowView
//    }

    private fun getCountryZipCode(ssid: String): String {
        val loc = Locale("", ssid)
        return loc.displayCountry.trim()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.country_list_item, parent, false)
        return CountryCodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val countryCode = countryCodeList[position]
        val name = holder.itemView.findViewById<TextView>(R.id.txtViewCountryName)
        val flag = holder.itemView.findViewById<ImageView>(R.id.imgViewFlag)

        val png = countryCode.code.trim().toLowerCase(Locale.ROOT)
        name.text = getCountryZipCode(countryCode.code)
        flag.setImageResource(context.resources.getIdentifier(
            "drawable/$png",
            null,
            context.packageName))
    }

    override fun getItemCount(): Int = countryCodeList.size
}