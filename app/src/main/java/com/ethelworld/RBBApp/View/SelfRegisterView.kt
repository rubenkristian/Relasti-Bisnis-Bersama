package com.ethelworld.RBBApp.View

import org.json.JSONObject

interface SelfRegisterView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(data: JSONObject)

        suspend fun onProvince(data: JSONObject)
        suspend fun onCity(data: JSONObject)
        suspend fun onBank(data: JSONObject)

        suspend fun onAccountIsAvaiable(msg: String, id: Long)
    }

    interface Presenter {
        suspend fun postAccount(referal: String,
                                name: String,
                                phone: String,
                                email: String,
                                gender: Int?,
                                occupation: String,
                                company: String,
                                province: Int?,
                                city: Int?,
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