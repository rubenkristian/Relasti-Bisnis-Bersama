package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.Contact

interface ContactView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(contacts: ArrayList<Contact?>)
        suspend fun onSuccessLocal(contacts: ArrayList<Contact?>)
        suspend fun onContactLocalEnd()
    }

    interface Presenter {
        suspend fun syncContactFromServer()
        suspend fun listContactLocal(search: String?, page: Int)
    }
}