package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.PaymentInfoView
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PaymentInfoPresenter(
    private var view: PaymentInfoView.View?,
    context: Context): PaymentInfoView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAG = "PAYMENTINFOREQ"
    }

    override suspend fun getPaymentInfo(id: Long) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.GET,
            "${ReqAPI.apiPoint}getpaymentinfo?id=$id",
            data,
            TimeUnit.MILLISECONDS.convert(
                30,
                TimeUnit.SECONDS))

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
                view?.onSuccess(resjson.getJSONObject("data"))
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