package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.PendingAccount

interface PendingAccountView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(generations: ArrayList<PendingAccount?>)
    }

    interface Presenter {
        suspend fun getPendingAccount(search: String, page: Int, limit: Int)
    }
}