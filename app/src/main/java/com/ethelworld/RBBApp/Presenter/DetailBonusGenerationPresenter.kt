package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.BonusGenerationItem
import com.ethelworld.RBBApp.View.DetailBonusGenerationView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DetailBonusGenerationPresenter(
    private var view: DetailBonusGenerationView.View?,
    private val context: Context): DetailBonusGenerationView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)
    private val localCurr = Locale("in", "ID")
    private var rpFormat: NumberFormat

    companion object{
        const val TAG = "DETAILBONUSGENERATIONREQ"
    }

    init {
        rpFormat = NumberFormat.getCurrencyInstance(localCurr)
        rpFormat.maximumFractionDigits = 0
    }

    override suspend fun getListDetialBonusGeneration(gen: Int, page: Int, limit: Int) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()
        data["genid"] = gen.toString()

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}detailbonusgeneration?index=${page}&limit=${limit}",
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
                val detailBonusItems    = resjson.getJSONArray("detailbonus")
                val detailBonusList     = ArrayList<BonusGenerationItem?>()

                for (i in 0 until detailBonusItems.length()) {
                    val detailBonus = detailBonusItems.getJSONObject(i)

                    detailBonusList.add(
                        BonusGenerationItem((i+1),
                            detailBonus.getString("bonus_date"),
                            detailBonus.getString("idmember"),
                            rpFormat.format(
                                detailBonus.getLong("cash_bonus"))))
                }

                view?.onSuccess(detailBonusList)
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
        view?.showError(errmsg.hashCode(), errmsg)
    }

    override suspend fun onError(errmsg: String?) {
        view?.hideLoading()
        view?.showError(errmsg.hashCode(), errmsg)
    }

    fun onDestroy() {
        view = null

        api.cancelRequest(TAG)
    }
}