package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.BonusGeneration
import com.ethelworld.RBBApp.View.BonusGenerationView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import com.ethelworld.RBBApp.tools.network.RequestResult
import org.json.JSONException
import org.json.JSONObject

class BonusGenerationPresenter(
    private var view: BonusGenerationView.View?,
    private val context: Context?): BonusGenerationView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object{
        const val TAG       = "BONUSGENERATIONREQ"
        const val TAGTOTAL  = "TOTALBONUSREQ"
    }

    override suspend fun getListBonusgeneration() {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult: RequestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}bonusgeneration",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun getTotalBonus() {
        val data: HashMap<String, String> = HashMap()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult: RequestResult = api.requestAPI(
            TAGTOTAL,
            Request.Method.GET,
            "${ReqAPI.apiPoint}totalcashback?id=$id",
            data)

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onTotalBonus(requestResult.response)
        }
    }

    suspend fun onTotalBonus(result: String) {
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
                val bonusGenerations    = resjson.getJSONArray("bonusgenerations")
                val bonusGenerationList = ArrayList<BonusGeneration?>()

                for(i in 0 until bonusGenerations.length()) {
                    val bonusGeneration = bonusGenerations.getJSONObject(i)

                    bonusGenerationList
                        .add(
                            BonusGeneration(
                                bonusGeneration.getInt("index"),
                                bonusGeneration.getString("total")
                            )
                        )
                }

                view?.onSuccess(bonusGenerationList)
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
    }
}