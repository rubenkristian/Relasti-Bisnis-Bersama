package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.WithdrawConfirmView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class WithdrawConfirmPresenter(
    private var view: WithdrawConfirmView.View?,
    private val context: Context) : WithdrawConfirmView.Presenter,  OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object{
        const val TAG = "CODESUBMIT"
    }

    override suspend fun submitCodeConfirm(id: Int?, code: String) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        val idauth = Authentication(context).getValueInt(Authentication.ID).toString()

        data["id"]          = idauth
        data["id_withdraw"] = id.toString()
        data["code"]        = code

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}confirmCodeWithdraw", data)

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
        } catch (e: JSONException) {
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