package com.ethelworld.RBBApp.Presenter

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.TotalBonus
import com.ethelworld.RBBApp.View.TotalBonusView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TotalBonusPresenter(
    private var view: TotalBonusView.View?,
    private val context: Context): TotalBonusView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)
    private val limit: Int = 20

    companion object {
        const val TAG = "TOTBONUSREQ"
        const val REWARDTAG = "REWARDREQ"
        const val LASTADSTAG = "LASTADSTAGREQ"
    }

    override suspend fun getTotalBonus() {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}infototalbonus",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun getReward() {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            REWARDTAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}reward",
            data)

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onRewardSuccess(requestResult.response)
        }
    }

    override suspend fun checkLastTimeAdsWatch() {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            LASTADSTAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}checklastadsshow",
            data,
            TimeUnit.MILLISECONDS.convert(
                10,
                TimeUnit.SECONDS)
        )

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onCheckLastAdsWatch(requestResult.response)
        }
    }

    suspend fun onCheckLastAdsWatch(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            val status = resjson.getBoolean("status")
            val msg    = if(resjson.isNull("msg")) {
                ""
            } else {
                resjson.getString("msg")
            }

            view?.onCheckLastAdsWatched(status, msg)
        }catch (e: JSONException) {
            onError(e.message)
        }
    }

    suspend fun onRewardSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val rewardtotal     = resjson.getLong("total")
                val rewardreceived  = resjson.getLong("receive")

                Authentication(context).save(Authentication.STARITEM, rewardtotal)

                view?.onRewardSuccess(rewardtotal, rewardreceived)
            } else {
                onError(resjson.getString("msg"))
            }
        }catch (e: JSONException) {
            onError(e.message)
        }
    }

    override suspend fun onSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val total       = resjson.getLong("totalbonus")
                val star        = resjson.getLong("star")

                val totalBonus  = TotalBonus(total, star)

                if(star != Authentication(context).getStarCount()) {
                    Authentication(context).save(Authentication.STARITEM, star)
                }

                view?.onSuccess(totalBonus)
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
        api.cancelRequest(REWARDTAG)
        api.cancelRequest(LASTADSTAG)
    }
}