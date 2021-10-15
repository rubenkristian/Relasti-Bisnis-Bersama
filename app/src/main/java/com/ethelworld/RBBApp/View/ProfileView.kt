package com.ethelworld.RBBApp.View

import org.json.JSONObject

interface ProfileView {
    interface View{
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(token: JSONObject)
        suspend fun onUpdateSuccess(token: JSONObject)
        suspend fun onBank(data: JSONObject)
        suspend fun onUpdateBank(data: JSONObject)
    }

    interface Presenter {
        suspend fun getProfile()
        suspend fun updateInfo(facebook: String, instagram: String, olshop: String, tiktok: String, youtube: String)

        suspend fun getBank()
        suspend fun saveBank(
            bank: Int?,
            bankNumber: String,
            bankName: String)
    }
}