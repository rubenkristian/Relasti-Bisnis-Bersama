package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.PendingAccount
import com.ethelworld.RBBApp.View.PendingAccountView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class PendingAccountPresenter(
    private var view: PendingAccountView.View?,
    private val context: Context): PendingAccountView.Presenter,
    OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAG = "PENDINGREQ"
    }

    override suspend fun getPendingAccount(search: String, page: Int, limit: Int) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        data["id"]      = id
        data["search"]  = search

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}pendingaccount?index=$page&limit=$limit",
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
                val pendings    = resjson.getJSONArray("accounts")
                val pendingList = ArrayList<PendingAccount?>()

                for(i in 0 until pendings.length()) {
                    val pending = pendings.getJSONObject(i)

                    pendingList.add(
                        PendingAccount(
                            pending.getInt("id"),
                            pending.getString("wa"),
                            pending.getString("fullname")))
                }

                view?.onSuccess(pendingList)
            } else {
                onError(resjson.getString("msg"))
            }
        }catch (e: JSONException){
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