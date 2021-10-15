package com.ethelworld.RBBApp.View

import org.json.JSONObject

interface ForgotPasswordView {
    interface View {
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)

        suspend fun onSuccessSendEmail(result: JSONObject)
        suspend fun onSuccessConfirmCode(result: JSONObject)
        suspend fun onSuccessChangePassword(result: JSONObject)
        suspend fun onSuccessResendCode(result: JSONObject)
    }

    interface Presenter {
        suspend fun sendEmailChangePassword(email: String, username: String)
        suspend fun resendCode(email: String, username: String)
        suspend fun sendConfirmCode(code: String, email: String, hashId: String)
        suspend fun sendChangePassword(newPassword: String, retypeNewPassword: String, email: String, code: String, hashId: String)
    }
}