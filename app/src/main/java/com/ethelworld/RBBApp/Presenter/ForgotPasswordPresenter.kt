package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request.Method
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.ForgotPasswordView
import com.ethelworld.RBBApp.tools.network.OnForgotPassword
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordPresenter(
    private var view: ForgotPasswordView.View?,
    context: Context): ForgotPasswordView.Presenter, OnStringListener, OnForgotPassword {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAGEMAIL      = "EMAILREQ"
        const val TAGCODE       = "CODEREQ"
        const val TAGRESEND     = "RESENDREQ"
        const val TAGPASSWORD   = "PASSREQ"
    }

    override suspend fun sendEmailChangePassword(email: String, username: String) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["email"]       = email
        data["username"]    = username

        val requestResult = api.requestAPI(
            TAGEMAIL,
            Method.POST,
            "${ReqAPI.url}account/sendemail",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onResultEmail(requestResult.response)
        }
    }

    override suspend fun resendCode(email: String, username: String) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["email"]       = email
        data["username"]    = username

        val requestResult = api.requestAPI(
            TAGRESEND,
            Method.POST,
            "${ReqAPI.url}account/resend",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onResultResend(requestResult.response)
        }
    }

    override suspend fun sendConfirmCode(code: String, email: String, hashId: String) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["code"]    = code
        data["hashid"]  = hashId
        data["email"]   = email

        val requestResult = api.requestAPI(
            TAGCODE,
            Method.POST,
            "${ReqAPI.url}account/confirmcode",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onResultConfirm(requestResult.response)
        }
    }

    override suspend fun sendChangePassword(
        newPassword: String,
        retypeNewPassword: String,
        email: String,
        code: String,
        hashId: String) {

        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["password"]    = newPassword
        data["re_password"] = retypeNewPassword
        data["hashid"]      = hashId
        data["email"]       = email
        data["code"]        = code

        val requestResult = api.requestAPI(
            TAGPASSWORD,
            Method.POST,
            "${ReqAPI.url}account/changepass",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onResultPassword(requestResult.response)
        }
    }

    override suspend fun onSuccess(result: String) {

    }

    override suspend fun onError(error: VolleyError?) {
        view?.hideLoading()

        println(error)

        view?.showError(error.hashCode(), error?.message)
    }

    override suspend fun onError(code: Int, errmsg: String?) {
        view?.hideLoading()

        println(errmsg)

        view?.showError(errmsg.hashCode(), errmsg)
    }

    override suspend fun onError(errmsg: String?) {
        view?.hideLoading()

        println(errmsg)

        view?.showError(errmsg.hashCode(), errmsg)
    }

    override suspend fun onResultEmail(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")

                view?.onSuccessSendEmail(data)
            }else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onResultResend(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")
                view?.onSuccessResendCode(data)
            }else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onResultConfirm(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)
            if(resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")
                view?.onSuccessConfirmCode(data)
            }else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onResultPassword(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")

                view?.onSuccessChangePassword(data)
            }else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    fun onDestroy() {
        view = null

        api.cancelRequest(TAGCODE)
        api.cancelRequest(TAGEMAIL)
        api.cancelRequest(TAGPASSWORD)
        api.cancelRequest(TAGRESEND)
    }
}