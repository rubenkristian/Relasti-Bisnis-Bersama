package com.ethelworld.RBBApp.View

import com.ethelworld.RBBApp.Item.Partner

interface PartnerAreaView {
    interface View{
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(partners: ArrayList<Partner?>)
    }

    interface Presenter {
        suspend fun getPartnerArea(gen: Int, search: String, page: Int, limit: Int)
    }
}