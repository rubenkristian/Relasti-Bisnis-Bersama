package com.ethelworld.RBBApp.DBInterface

import com.ethelworld.RBBApp.Item.Contact

interface OnContactSelected {
    suspend fun onContactSelected(contact: Contact)
    suspend fun onErrorContact(errmsg: String)
}