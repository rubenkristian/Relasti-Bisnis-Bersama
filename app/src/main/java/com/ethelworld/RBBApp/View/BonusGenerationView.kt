package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.BonusGeneration

interface BonusGenerationView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(bonusGenerations: ArrayList<BonusGeneration?>)

        suspend fun onTotalSuccess(total: Long)
    }

    interface Presenter {
        suspend fun getListBonusgeneration()

        suspend fun getTotalBonus()
    }
}