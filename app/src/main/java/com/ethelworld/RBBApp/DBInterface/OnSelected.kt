package com.ethelworld.RBBApp.DBInterface

import com.ethelworld.RBBApp.Item.Contact

interface OnSelected {
    suspend fun onSelectedFinish(contacts: ArrayList<Contact?>)
    suspend fun onEnd()
}