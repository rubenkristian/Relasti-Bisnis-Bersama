package com.ethelworld.RBBApp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Item.BonusGenerationItem
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.tools.Constant

class DetailBonusAdapter(
    private val listener: (BonusGenerationItem?) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback : DiffUtil.ItemCallback<BonusGenerationItem?> =
        object : DiffUtil.ItemCallback<BonusGenerationItem?>() {
            override fun areItemsTheSame(
                oldItem: BonusGenerationItem,
                newItem: BonusGenerationItem): Boolean {
                return oldItem.index == newItem.index
            }

            override fun areContentsTheSame(
                oldItem: BonusGenerationItem,
                newItem: BonusGenerationItem): Boolean {
                return oldItem == newItem
            }
        }

    val differ : AsyncListDiffer<BonusGenerationItem?> = AsyncListDiffer(this, diffCallback)

    fun updateBonusGeneration(bonusGenerationItems: MutableList<BonusGenerationItem?>) {
        differ.submitList(bonusGenerationItems)
    }

    fun addLoadingView(bonusGenerationItems: MutableList<BonusGenerationItem?>) {
        differ.submitList(bonusGenerationItems)
    }

    fun removeLoadingView(bonusGenerationItems: MutableList<BonusGenerationItem?>) {
        differ.submitList(bonusGenerationItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == Constant.VIEW_TYPE_ITEM) {
            val view = inflater
                .inflate(R.layout.detail_bonus_item, parent, false)
            ContactViewHolder(view)
        } else {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.loading_dialog, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val bonusGenerationItem: BonusGenerationItem? = differ.currentList[position]
            holder.itemView
                .findViewById<TextView>(R.id.date_bonus).text = bonusGenerationItem?.date
            holder.itemView
                .findViewById<TextView>(R.id.bonus_from).text = bonusGenerationItem?.from
            holder.itemView
                .findViewById<TextView>(R.id.bonus_cash).text = bonusGenerationItem?.cash

            holder.itemView.setOnClickListener {
                listener(bonusGenerationItem)
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