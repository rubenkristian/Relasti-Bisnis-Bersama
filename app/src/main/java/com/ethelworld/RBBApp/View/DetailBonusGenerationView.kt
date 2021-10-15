package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.BonusGenerationItem

interface DetailBonusGenerationView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(bonusGenerations: ArrayList<BonusGenerationItem?>)
    }

    interface Presenter {
        suspend fun getListDetialBonusGeneration(gen: Int, page: Int, limit: Int)
    }
}