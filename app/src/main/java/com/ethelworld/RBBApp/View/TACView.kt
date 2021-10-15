package com.ethelworld.RBBApp.View

interface TACView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(success: Boolean)
    }

    interface Presenter {
        suspend fun sendTAC()
    }
}