package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.Contact
import org.json.JSONObject

interface ContactDetailView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(msg: String?)
        suspend fun onSuccess(contact: Contact?)

        suspend fun onOthweInfoSuccess(result: JSONObject)
    }

    interface Presenter {
        suspend fun getContactDetail(id: Long?)
        suspend fun getOtherInfo(id: Long?)
    }
}