package com.ethelworld.RBBApp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Item.PendingAccount
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.tools.Constant

class PendingAccountAdapter(
    private val listener: (PendingAccount?)-> Unit,
    private val wabutton: (String?) -> Unit
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback : DiffUtil.ItemCallback<PendingAccount?> =
        object : DiffUtil.ItemCallback<PendingAccount?>() {
            override fun areItemsTheSame(
                oldItem: PendingAccount,
                newItem: PendingAccount): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PendingAccount,
                newItem: PendingAccount): Boolean {
                return oldItem == newItem
            }
        }

    val differ : AsyncListDiffer<PendingAccount?> = AsyncListDiffer(this, diffCallback)

    fun updatePendingAccount(pendingAccount: MutableList<PendingAccount?>) {
        differ.submitList(pendingAccount)
    }

    fun addLoadingView(pendingAccount: MutableList<PendingAccount?>) {
        differ.submitList(pendingAccount)
    }

    fun removeLoadingView(pendingAccount: MutableList<PendingAccount?>) {
        differ.submitList(pendingAccount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == Constant.VIEW_TYPE_ITEM) {
            val view = inflater.inflate(R.layout.pending_item, parent, false)
            ContactViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loading_dialog, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val pendingAccount = differ.currentList[position]
            holder.itemView.findViewById<TextView>(R.id.whatsapp_number).text = pendingAccount?.wa
            holder.itemView.findViewById<TextView>(R.id.name_account).text = pendingAccount?.name
            holder.itemView.findViewById<ImageView>(R.id.wa_send).setOnClickListener {
                wabutton(pendingAccount?.wa)
            }
            holder.itemView.setOnClickListener {
                listener(pendingAccount)
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