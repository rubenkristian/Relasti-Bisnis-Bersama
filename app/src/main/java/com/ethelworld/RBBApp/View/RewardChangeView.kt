package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.RewardChange
import org.json.JSONObject

interface RewardChangeView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(token: JSONObject)
        suspend fun onGetListSuccess(rewardChangeList: ArrayList<RewardChange>, countStar: Int)
    }

    interface Presenter {
        suspend fun getRewardChangeList()
        suspend fun submitChangeReward(idreward: Int, type: Int)
    }
}