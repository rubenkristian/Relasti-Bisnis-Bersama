package com.ethelworld.RBBApp.View

import org.json.JSONObject

interface PaymentInfoView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(data: JSONObject)
    }

    interface Presenter {
        suspend fun getPaymentInfo(id: Long)
    }
}