package com.ethelworld.RBBApp.View

interface ContactRecoveryView {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun progressLoading(progress: Int?, status: String)
        fun showError(msg: String?)
        fun onSuccess()
    }

    interface Presenter {
        fun contactRecovery()
    }
}