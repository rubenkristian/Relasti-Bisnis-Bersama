package com.ethelworld.RBBApp.View

interface LoginView {
    interface View{
        suspend fun showLoading()
        suspend fun hideLoading()
        suspend fun showError(code: Int, msg: String?)
        suspend fun onSuccess(token: String)
    }

    interface Presenter {
        suspend fun authentication(username: String, password: String)
    }
}