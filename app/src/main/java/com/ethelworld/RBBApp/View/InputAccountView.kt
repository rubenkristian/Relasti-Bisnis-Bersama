package com.ethelworld.RBBApp.View

import org.json.JSONObject

interface InputAccountView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(data: JSONObject)
        suspend fun onProvince(data: JSONObject)
        suspend fun onCity(data: JSONObject)
        suspend fun onBank(data: JSONObject)
    }

    interface Presenter {
        suspend fun postAccount(name: String,
                                phone: String,
                                email: String,
                                gender: Int?,
                                occupation: String,
                                company: String,
                                province: Int?,
                                city: Int?,
                                bank: Int?,
                                bankNumber: String,
                                bankName: String,
                                facebook: String,
                                instagram: String,
                                tiktok: String,
                                olshop: String,
                                youtube: String,
                                password: String,
                                confirmPassword: String)

        suspend fun getProvince()

        suspend fun getCity(provinceId: Int?)

        suspend fun getBank()
    }
}