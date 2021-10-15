package com.ethelworld.RBBApp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Item.RewardChange
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.tools.Constant
import com.google.android.material.button.MaterialButton
import java.lang.ref.WeakReference

class RewardChangeAdapter(
    private var context: WeakReference<Context>, private val listener: (RewardChange?) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    class RewardChangeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback : DiffUtil.ItemCallback<RewardChange?> =
        object : DiffUtil.ItemCallback<RewardChange?>() {
            override fun areItemsTheSame(oldItem: RewardChange, newItem: RewardChange): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RewardChange, newItem: RewardChange): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffCallback)

    fun updateRewardList(rewardChange: MutableList<RewardChange?>) {
        differ.submitList(rewardChange)
    }

    fun addLoadingView(rewardChange: MutableList<RewardChange?>) {
        differ.submitList(rewardChange)
    }

    fun removeLoadingView(rewardChange: MutableList<RewardChange?>) {
        differ.submitList(rewardChange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == Constant.VIEW_TYPE_ITEM) {
            val view = inflater.inflate(R.layout.reward_item, parent, false)
            RewardChangeViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_loader, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val cont = context.get()
            val reward = differ.currentList[position]

            holder.itemView.findViewById<TextView>(R.id.content).text = reward?.content
            holder.itemView.findViewById<TextView>(R.id.price).text =
                cont?.getString(R.string.star_needed, reward?.starneeded)

            holder.itemView.findViewById<MaterialButton>(R.id.request).setOnClickListener {
                listener(reward)
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