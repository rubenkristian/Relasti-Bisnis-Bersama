package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.WithdrawHistory
import com.ethelworld.RBBApp.View.WithdrawHistoryView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class WithdrawHistoryPresenter(
    private var view: WithdrawHistoryView.View?,
    private val context: Context): WithdrawHistoryView.Presenter, OnStringListener {

    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAG       = "WITHDRAWHISTORYREQ"
        const val TAGTOTAL  = "TOTALWITHDRAWREQ"
    }

    override suspend fun getWithdrawHistory(search: String, page: Int, limit: Int) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        data["id"]      = id
        data["search"]  = search

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}withdrawhistory?index=$page&limit=$limit",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun getTotalWithdraw() {
        val data: HashMap<String, String> = HashMap()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            TAGTOTAL,
            Request.Method.GET,
            "${ReqAPI.apiPoint}totalwithdraw?id=$id",
            data)

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onTotalSuccess(requestResult.response)
        }
    }

    suspend fun onTotalSuccess(result: String) {
        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                view?.onTotalSuccess(resjson.getLong("total"))
            } else {
                onError(resjson.getString("msg"))
            }
        }catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val histories   = resjson.getJSONArray("histories")
                val historyList = ArrayList<WithdrawHistory?>()

                for(i in 0 until histories.length()) {
                    val history = histories.getJSONObject(i)

                    historyList.add(
                        WithdrawHistory(
                            history.getInt("id"),
                            history.getString("date_created"),
                            history.getString("cash"),
                            history.getInt("is_verified") == 1,
                            history.getInt("confirm") == 1))
                }

                view?.onSuccess(historyList)
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