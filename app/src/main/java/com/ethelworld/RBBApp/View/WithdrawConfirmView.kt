package com.ethelworld.RBBApp.View

interface WithdrawConfirmView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(msg: String)
    }

    interface Presenter {
        suspend fun submitCodeConfirm(id: Int?, code: String)
    }
}