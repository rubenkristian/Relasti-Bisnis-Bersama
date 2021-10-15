package com.ethelworld.RBBApp.View

import org.json.JSONArray

interface HomeView {
    interface View{
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(images: JSONArray)
        suspend fun onBonusSuccess(reward: Int, msg: String?)
        suspend fun onClaimBonusSuccess(msg: String?)
    }

    interface Presenter {
        suspend fun getImageAssetSlider()
        suspend fun getBonusReward()
        suspend fun claimBonusReward(reward: Int)
    }
}