package com.ethelworld.RBBApp.View

interface TokenView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(success: Boolean)
    }

    interface Presenter {
        suspend fun checkToken()
    }
}