package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.HomeView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class HomePresenter(
    private var view: HomeView.View?,
    private var context: Context?): HomeView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAGREQSLIDER = "requestslider"
        const val TAGREQBONUSREWARD = "requestbonusreward"
        const val TAGREQCLAIMREWARD = "requestclaimreward"
    }

    override suspend fun getImageAssetSlider() {
        view?.showLoading()
        val data: HashMap<String, String> = HashMap()
        val requestResult = api.requestAPI(
            TAGREQSLIDER,
            Request.Method.GET,
            "${ReqAPI.apiPoint}slider",
            data,
            TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS)
        )

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun getBonusReward() {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            TAGREQBONUSREWARD,
            Request.Method.POST,
            "${ReqAPI.apiPoint}checkpromoreward",
            data
        )

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onBonusRewardInfo(requestResult.response)
        }
    }

    override suspend fun claimBonusReward(reward: Int) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"]      = Authentication(context).getValueInt(Authentication.ID).toString()
        data["reward"]  = reward.toString()

        val requestResult = api.requestAPI(
            TAGREQCLAIMREWARD,
            Request.Method.POST,
            "${ReqAPI.apiPoint}claimreward",
            data
        )

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onClaimRewardInfo(requestResult.response)
        }
    }

    suspend fun onBonusRewardInfo(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")
                view?.onBonusSuccess(data.getInt("reward"), data.getString("msg"))
            }
        }catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    suspend fun onClaimRewardInfo(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val msg = resjson.getString("msg")
                view?.onClaimBonusSuccess(msg)
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val data = resjson.getJSONArray("images")

                view?.onSuccess(data)
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
        view?.showError(errmsg.hashCode(), errmsg)
    }

    override suspend fun onError(errmsg: String?) {
        view?.hideLoading()
        view?.showError(errmsg.hashCode(), errmsg)
    }

    fun onDestroy() {
        view    = null
        context = null

        api.cancelRequest(TAGREQSLIDER)
        api.cancelRequest(TAGREQCLAIMREWARD)
        api.cancelRequest(TAGREQBONUSREWARD)
    }
}