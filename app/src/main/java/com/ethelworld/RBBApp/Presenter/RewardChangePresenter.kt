package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.RewardChange
import com.ethelworld.RBBApp.View.RewardChangeView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RewardChangePresenter(
    private var view: RewardChangeView.View?,
    private var context: Context?
): RewardChangeView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAG = "REWARDCHANGEREQ"
        const val LISTTAG = "REWARDLISTREQ"
    }

    override suspend fun submitChangeReward(idreward: Int, type: Int) {
        view?.showLoading()
        api.cancelRequest(TAG)

        val data: HashMap<String, String> = HashMap()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        data["id"]          = id
        data["idreward"]    = idreward.toString()
        data["type"]        = type.toString()

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
        "${ReqAPI.apiPoint}rewardrequest",
            data)

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun getRewardChangeList() {
        view?.showLoading()
        api.cancelRequest(LISTTAG)

        val data: HashMap<String, String> = HashMap()

        val requestResult = api.requestAPI(
            LISTTAG,
            Request.Method.GET,
            "${ReqAPI.apiPoint}starrewardlist",
            data,
            TimeUnit.MILLISECONDS.convert(
                20,
                TimeUnit.MINUTES)
        )

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onListSuccess(requestResult.response)
        }
    }

    suspend fun onListSuccess(result: String) {
        val resjson: JSONObject

        try {
            resjson     = JSONObject(result)
            val status  = resjson.getBoolean("status")

            if(status) {
                val data    = resjson.getJSONObject("data")
                val lists   = data.getJSONArray("list")
                val star    = data.getInt("star")

                val rewardList = ArrayList<RewardChange>()

                var index = 0

                while(index < lists.length()) {
                    val list = lists.getJSONObject(index)

                    val id              = list.getInt("id")
                    val content         = list.getString("content")
                    val type            = list.getInt("type")
                    val starrequired    = list.getInt("starrequired")

                    rewardList.add(RewardChange(id, type, content, starrequired))

                    index++
                }

                view?.onGetListSuccess(rewardList, star)
            } else {
                onError(resjson.getString("msg"))
            }
        }catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }

        view?.hideLoading()
    }

    override suspend fun onSuccess(result: String) {
        val resjson: JSONObject

        try {
            resjson     = JSONObject(result)
            val status  = resjson.getBoolean("status")

            if (status){
                view?.onSuccess(resjson.getJSONObject("data"))
            }else{
                onError(resjson.getString("msg"))
            }
        }catch (e: JSONException){
            onError(e.hashCode(), e.message)
        }

        view?.hideLoading()
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
        view    = null
        context = null

        api.cancelRequest(TAG)
    }
}