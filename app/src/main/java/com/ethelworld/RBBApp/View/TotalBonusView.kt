package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.TotalBonus

interface TotalBonusView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(totalBonus: TotalBonus)
        suspend fun onRewardSuccess(total: Long, received: Long)
        suspend fun onCheckLastAdsWatched(status: Boolean, msg: String?)
    }

    interface Presenter {
        suspend fun getTotalBonus()
        suspend fun getReward()
        suspend fun checkLastTimeAdsWatch()
    }
}