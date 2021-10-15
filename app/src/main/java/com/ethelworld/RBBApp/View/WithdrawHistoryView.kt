package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.WithdrawHistory

interface WithdrawHistoryView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(generations: ArrayList<WithdrawHistory?>)

        suspend fun onTotalSuccess(total: Long)
    }

    interface Presenter {
        suspend fun getWithdrawHistory(search: String, page: Int, limit: Int)
        suspend fun getTotalWithdraw()
    }
}