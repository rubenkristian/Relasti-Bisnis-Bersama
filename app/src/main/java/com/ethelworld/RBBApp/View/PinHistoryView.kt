package com.ethelworld.RBBApp.View

import org.json.JSONArray

interface PinHistoryView {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(code: Int, msg: String?)
        fun onSuccess(token: JSONArray)
    }

    interface Presenter {
        suspend fun GetLastHistory()
        suspend fun GetListHistory(page: Int)
    }
}