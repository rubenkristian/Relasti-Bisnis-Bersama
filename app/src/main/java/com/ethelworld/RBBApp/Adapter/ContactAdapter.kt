package com.ethelworld.RBBApp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethelworld.RBBApp.Item.Contact
import com.ethelworld.RBBApp.R
import com.ethelworld.RBBApp.tools.Constant
import java.util.*

class ContactAdapter(private val listener: (Contact?) -> Unit):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback : DiffUtil.ItemCallback<Contact?> =
        object : DiffUtil.ItemCallback<Contact?>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.phoneNumber == newItem.phoneNumber
            }
        }

    private val differ : AsyncListDiffer<Contact?> = AsyncListDiffer(this, diffCallback)

    fun updateContact(contact: MutableList<Contact?>) {
        differ.submitList(contact)
    }

    fun addLoadingView(listContacts: MutableList<Contact?>) {
        differ.submitList(listContacts)
    }

    fun removeLoadingView(listContacts: MutableList<Contact?>) {
        differ.submitList(listContacts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == Constant.VIEW_TYPE_ITEM) {
            val view = inflater.inflate(R.layout.contact_item, parent, false)
            ContactViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.contact_loader, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val contact: Contact? = differ.currentList[position]
            val name = contact?.name?.capitalize(Locale.getDefault())
            val address: String = contact?.province + ", " + contact?.city

            holder.itemView.findViewById<TextView>(R.id.nameInitial).text = name?.get(0)?.toString()
            holder.itemView.findViewById<TextView>(R.id.fullName).text = name
            holder.itemView.findViewById<TextView>(R.id.address).text = address
            holder.itemView.setOnClickListener{
                listener(contact)
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
