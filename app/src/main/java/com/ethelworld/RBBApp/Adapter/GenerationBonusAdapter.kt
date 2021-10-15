package com.ethelworld.RBBApp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Item.BonusGeneration
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.tools.Constant
import java.lang.StringBuilder
import java.text.NumberFormat
import java.util.*

class GenerationBonusAdapter (private val listener: (BonusGeneration?) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback : DiffUtil.ItemCallback<BonusGeneration?> =
        object : DiffUtil.ItemCallback<BonusGeneration?>() {
            override fun areItemsTheSame(
                oldItem: BonusGeneration,
                newItem: BonusGeneration): Boolean {
                return oldItem.generationIndex == newItem.generationIndex
            }

            override fun areContentsTheSame(
                oldItem: BonusGeneration,
                newItem: BonusGeneration): Boolean {
                return oldItem == newItem
            }
        }

    val differ : AsyncListDiffer<BonusGeneration?> = AsyncListDiffer(this, diffCallback)

    val localCurr = Locale("in", "ID")
    private var rpFormat: NumberFormat

    init {
        rpFormat = NumberFormat.getCurrencyInstance(localCurr)
        rpFormat.maximumFractionDigits = 0
    }

    fun updateBonusGeneration(bonusGenerations: MutableList<BonusGeneration?>) {
        differ.submitList(bonusGenerations)
    }

    fun addLoadingView(bonusGenerations: MutableList<BonusGeneration?>) {
        differ.submitList(bonusGenerations)
    }

    fun removeLoadingView(bonusGenerations: MutableList<BonusGeneration?>) {
        differ.submitList(bonusGenerations)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == Constant.VIEW_TYPE_ITEM) {
            val view = inflater
                .inflate(R.layout.generation_bonus_item, parent, false)
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
            val bonusGeneration: BonusGeneration? = differ.currentList[position]
            holder.itemView.findViewById<TextView>(R.id.generation_name).text =
                StringBuilder("Keturunan ").append(bonusGeneration?.generationIndex)
            val bonusTotalGeneration =
                holder.itemView.findViewById<Button>(R.id.generation_bonus_total)
            bonusTotalGeneration.text =
                rpFormat.format(bonusGeneration?.bonusTotal?.toLong())
            bonusTotalGeneration.setOnClickListener {
                listener(bonusGeneration)
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