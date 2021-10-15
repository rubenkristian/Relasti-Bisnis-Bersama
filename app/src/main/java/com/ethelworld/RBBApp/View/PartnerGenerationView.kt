package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.Generation

interface PartnerGenerationView {
    interface View{
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(generations: ArrayList<Generation?>)
    }

    interface Presenter {
        suspend fun getGenerationInfo()
    }
}