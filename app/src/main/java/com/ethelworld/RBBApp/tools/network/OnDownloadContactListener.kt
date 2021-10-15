package com.ethelworld.RBBApp.tools.network

import com.ethelworld.RBBApp.Item.UserContact

interface OnDownloadContactListener {
    fun updateProgress(progress: Int?, status: Int?)
    fun addContact(contact: UserContact)
    fun onSuccess(filename: String?)
}