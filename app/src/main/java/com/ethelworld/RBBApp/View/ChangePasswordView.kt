package com.ethelworld.RBBApp.View

interface ChangePasswordView {
    interface View{
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(token: String)
    }

    interface Presenter {
        suspend fun changePassword(password: String, oldPassword: String)
    }
}