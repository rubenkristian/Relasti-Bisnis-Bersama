package com.ethelworld.RBBApp.View

import org.json.JSONObject

interface PinTransferView {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(code: Int, msg: String?)
        fun onSuccess(token: JSONObject)
    }

    interface Presenter {
        suspend fun GetLastTransfer()
        suspend fun GetListTransfer(page: Int)
    }
}