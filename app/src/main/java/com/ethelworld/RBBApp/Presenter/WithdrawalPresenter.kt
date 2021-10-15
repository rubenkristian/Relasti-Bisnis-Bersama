package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.InfoWithdraw
import com.ethelworld.RBBApp.View.WithdrawalView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnInfoWithdrawListener
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class WithdrawalPresenter(
    private var view: WithdrawalView.View?,
    private val context: Context) :
    WithdrawalView.Presenter,
    OnInfoWithdrawListener,
    OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object{
        const val TAG       = "WITHDRAWREQ"
        const val TAGINFO   = "INFOREQ"
    }

    override suspend fun getInfo() {
        view?.showLoading()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        val data: HashMap<String, String> = HashMap()

        val requestResult = api.requestAPI(
            TAGINFO,
            Request.Method.GET,
            "${ReqAPI.apiPoint}infowithdraw?id=${id}",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onInfoWithdrawSuccess(requestResult.response)
        }
    }

    override suspend fun submitWithdraw(totalWithdraw: String) {
        view?.showLoading()

        if(totalWithdraw.toInt() > 0) {
            val data: HashMap<String, String> = HashMap()

            val id = Authentication(context).getValueInt(Authentication.ID).toString()

            data["id"]              = id
            data["totalwithdraw"]   = totalWithdraw

            val requestResult = api.requestAPI(
                TAG,
                Request.Method.POST,
                "${ReqAPI.apiPoint}requestwithdraw",
                data)

            if(requestResult.error!= null){
                onError(requestResult.error)
            } else {
                onSuccess(requestResult.response)
            }
        } else {
            onError("total withdraw harus diisi")
        }
    }

    override suspend fun onInfoWithdrawSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if (resjson.getBoolean("status")) {
                val data                = resjson.getJSONObject("data")
                val username            = data.getString("username")
                val bonusTotal          = data.getLong("bonus")
                val bank                = data.getString("bank")
                val minimum             = data.getLong("minimum")
                val bankNumberAccount   = data.getString("bankAccountNumber")
                val bankNameAccount     = data.getString("bankAccountName")
                val withdrawalInfo      = data.getString("withdrawalInfo")

                val infoWithdraw        = InfoWithdraw(
                                                        username,
                                                        bonusTotal,
                                                        bank,
                                                        minimum,
                                                        bankNumberAccount,
                                                        bankNameAccount,
                                                        withdrawalInfo)

                view?.onLoadDone(infoWithdraw)
            }else {
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
                view?.onSuccess(resjson.getJSONObject("data"))
            }else {
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
        api.cancelRequest(TAGINFO)
    }
}