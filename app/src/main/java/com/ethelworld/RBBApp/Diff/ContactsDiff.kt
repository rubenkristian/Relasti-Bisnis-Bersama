package com.ethelworld.RBBApp.Diff

import androidx.recyclerview.widget.DiffUtil
import com.ethelworld.RBBApp.Item.Contact

class ContactsDiff(private val oldContact: ArrayList<Contact?>, private val newContact: ArrayList<Contact?>): DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldContact[oldItemPosition]?.id == newContact[newItemPosition]?.id
    }

    override fun getOldListSize(): Int {
        return oldContact.size
    }

    override fun getNewListSize(): Int {
        return newContact.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldContact[oldItemPosition] == newContact[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}