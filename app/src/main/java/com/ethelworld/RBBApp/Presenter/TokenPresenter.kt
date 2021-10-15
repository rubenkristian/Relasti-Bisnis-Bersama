package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.TokenView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class TokenPresenter(
    private var view: TokenView.View?,
    private var context: Context): TokenView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAG = "TOKENREQ"
    }

    override suspend fun checkToken() {
        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}check",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun onSuccess(result: String) {
        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")){
                val data = resjson.getBoolean("data")

                view?.onSuccess(data)
            }else{
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onError(error: VolleyError?) {
        view?.showError(error.hashCode(), error?.message)
    }

    override suspend fun onError(code: Int, errmsg: String?) {
        view?.showError(code, errmsg)
    }

    override suspend fun onError(errmsg: String?) {
        view?.showError(500, errmsg)
    }
}