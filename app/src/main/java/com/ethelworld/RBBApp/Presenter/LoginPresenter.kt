package com.ethelworld.RBBApp.Presenter

import android.content.Context
//import android.util.Log
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.LoginView
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException

import com.ethelworld.RBBApp.tools.auth.Authentication as authenticationLocal

import org.json.JSONObject

class LoginPresenter(
    private var view: LoginView.View?,
    private var context: Context?) : LoginView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAG = "AUTHREQ"
    }

    override suspend fun authentication(username: String, password: String) {
        view?.showLoading()

        val requestResult = api.authentication(username, password)

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

            val auth = resjson.getBoolean("auth")

            if (!auth) {
                onError(resjson.getString("msg"))
                return
            }

            val authLocal   = authenticationLocal(context)
            val name        = resjson.getString("name")

            authLocal.save(authenticationLocal.NAME, name)

            val wa = resjson.getString("wa")
            authLocal.save(authenticationLocal.WA, wa)

            val type = resjson.getInt("type")
            authLocal.save(authenticationLocal.TYPE, type)

            val star = resjson.getLong("star")
            authLocal.save(authenticationLocal.STARITEM, star)

            val id = resjson.getInt("id")
            authLocal.save(authenticationLocal.ID, id)

            val tac = resjson.getBoolean("tac")
            authLocal.save(authenticationLocal.TAC, tac)

            val referal = resjson.getString("referal")
            authLocal.save(authenticationLocal.REFERAL, referal)

            val occupation = resjson.getString("occupation")
            authLocal.save(authenticationLocal.OCCUPATION, occupation)

            val address = resjson.getString("address")
            authLocal.save(authenticationLocal.ADDRESS, address)

            val token = resjson.getString("token")

            view?.onSuccess(token)

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
        context = null
        view    = null

        api.cancelAuth()
    }
}