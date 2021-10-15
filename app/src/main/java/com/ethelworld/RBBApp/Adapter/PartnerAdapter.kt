package com.ethelworld.RBBApp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Item.Partner
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.tools.Constant
import java.util.*

class PartnerAdapter(private val listener: (Partner?) -> Unit):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback : DiffUtil.ItemCallback<Partner?> =
        object : DiffUtil.ItemCallback<Partner?>() {
            override fun areItemsTheSame(oldItem: Partner, newItem: Partner): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Partner, newItem: Partner): Boolean {
                return oldItem.phoneNumber == newItem.phoneNumber
            }
        }

    val differ = AsyncListDiffer(this, diffCallback)

    fun updatePartner(partners: MutableList<Partner?>) {
        differ.submitList(partners)
    }

    fun addLoadingView(partners: MutableList<Partner?>) {
        differ.submitList(partners)
    }

    fun removeLoadingView(partners: MutableList<Partner?>) {
        differ.submitList(partners)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == Constant.VIEW_TYPE_ITEM) {
            val view = inflater.inflate(R.layout.partner_item, parent, false)
            ContactViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_loader, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val partner = differ.currentList[position]
            val name = partner?.name?.capitalize(Locale.ROOT)

            holder.itemView
                .findViewById<TextView>(R.id.nameInitial).text = name?.get(0).toString()
            holder.itemView
                .findViewById<TextView>(R.id.fullName).text = name
            holder.itemView
                .findViewById<TextView>(R.id.idmember).text = partner?.idMember?.toUpperCase(Locale.ROOT)
            holder.itemView
                .findViewById<TextView>(R.id.phoneNumber).text = partner?.phoneNumber
            holder.itemView.setOnClickListener{
                listener(partner)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(differ.currentList[position] == null) {
            Constant.VIEW_TYPE_LOADING
        } else {
            Constant.VIEW_TYPE_ITEM
        }
    }
}
