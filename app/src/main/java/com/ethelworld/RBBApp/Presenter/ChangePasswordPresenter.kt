package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.ChangePasswordView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class ChangePasswordPresenter(
    private var view: ChangePasswordView.View?,
    private val context: Context?) : ChangePasswordView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object{
        const val TAG = "CHANGEPASSREQ"
    }

    override suspend fun changePassword(password: String, oldPassword: String) {
        view?.showLoading()

        if(password == oldPassword) {
            onError("Password baru dan lama tidak boleh sama.")
        }

        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()
        data["oldpass"] = oldPassword
        data["newpass"] = password

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}changepassword",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun onSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                view?.onSuccess(resjson.getString("msg"))
            } else {
                onError(resjson.getString("msg"))
            }
        }catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onError(error: VolleyError?) {
        view?.hideLoading()
        view?.showError(error.hashCode(), error?.message)
    }

    override suspend fun onError(code: Int, errmsg: String?) {
        view?.hideLoading()
        view?.showError(code, errmsg)
    }

    override suspend fun onError(errmsg: String?) {
        view?.hideLoading()
        view?.showError(500, errmsg)
    }

    fun onDestroy() {
        view = null

        api.cancelRequest(TAG)
    }
}