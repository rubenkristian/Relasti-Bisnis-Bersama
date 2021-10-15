package com.ethelworld.RBBApp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Item.WithdrawHistory
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.tools.Constant
import java.text.NumberFormat
import java.util.*

class WithdrawHistoryAdapter(
    private val listener: (WithdrawHistory)-> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback : DiffUtil.ItemCallback<WithdrawHistory?> =
        object : DiffUtil.ItemCallback<WithdrawHistory?>() {
            override fun areItemsTheSame(
                oldItem: WithdrawHistory,
                newItem: WithdrawHistory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: WithdrawHistory,
                newItem: WithdrawHistory): Boolean {
                return oldItem == newItem
            }
        }

    val differ : AsyncListDiffer<WithdrawHistory?> = AsyncListDiffer(this, diffCallback)

    val localCurr = Locale("in", "ID")
    private var rpFormat: NumberFormat

    init {
        rpFormat = NumberFormat.getCurrencyInstance(localCurr)
        rpFormat.maximumFractionDigits = 0
    }

    fun updateWithdrawHistory(withdrawHistory: MutableList<WithdrawHistory?>) {
        differ.submitList(withdrawHistory)
    }

    fun addLoadingView(withdrawHistory: MutableList<WithdrawHistory?>) {
        differ.submitList(withdrawHistory)
    }

    fun removeLoadingView(withdrawHistory: MutableList<WithdrawHistory?>) {
        differ.submitList(withdrawHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == Constant.VIEW_TYPE_ITEM) {
            val view = inflater
                .inflate(R.layout.withdraw_history_item, parent, false)
            ContactViewHolder(view)
        } else {
            val view = LayoutInflater
                .from(parent.context).inflate(R.layout.loading_dialog, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val withdrawHistory = differ.currentList[position]
            holder.itemView
                .findViewById<TextView>(R.id.date_withdraw).text = withdrawHistory?.date
            holder.itemView
                .findViewById<TextView>(R.id.total_withdraw).text = withdrawHistory?.cash
            holder.itemView
                .findViewById<TextView>(R.id.status).text = when {
                withdrawHistory?.success!! -> {
                    "terbayar"
                }
                withdrawHistory.verified -> {
                    "diproses"
                }
                else -> {
                    "belum terverifikasi"
                }
            }
            holder.itemView.setOnClickListener {
                listener(withdrawHistory)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return if(differ.currentList[position] == null) {
            Constant.VIEW_TYPE_LOADING
        } else {
            Constant.VIEW_TYPE_ITEM
        }
    }
}