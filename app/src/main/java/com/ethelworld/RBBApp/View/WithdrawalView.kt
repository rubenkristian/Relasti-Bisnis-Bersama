package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.InfoWithdraw
import org.json.JSONObject

interface WithdrawalView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(data: JSONObject)
        suspend fun onLoadDone(infoWithdraw: InfoWithdraw)
    }

    interface Presenter {
        suspend fun getInfo()
        suspend fun submitWithdraw(totalWithdraw: String)
    }
}