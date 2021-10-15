package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.Partner
import com.ethelworld.RBBApp.View.PartnerAreaView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class PartnerAreaPresenter(
    private var view: PartnerAreaView.View?,
    private val context: Context) : PartnerAreaView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAG = "PARTNERAREAREQ"
    }

    override suspend fun getPartnerArea(gen: Int, search: String, page: Int, limit: Int) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"]      = Authentication(context).getValueInt(Authentication.ID).toString()
        data["genid"]   = gen.toString()
        data["search"]  = search

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.POST,
            "${ReqAPI.apiPoint}generationlistaccount?index=${page}&limit=${limit}",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun onSuccess(result: String) {
        //Log.i("PARTNERREQ", result)
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val partners        = resjson.getJSONArray("partners")
                val partnersList    = ArrayList<Partner?>()

                for (i in 0 until partners.length()) {
                    val partner = partners.getJSONObject(i)

                    partnersList.add(
                        Partner(
                            partner.getInt("id"),
                            partner.getString("id_member"),
                            partner.getString("fullname"),
                            partner.getString("wa"),
                            partner.getString("datecreated"),
                            partner.getInt("verified") == 1))
                }

                view?.onSuccess(partnersList)
            }else {
                onError(resjson.getString("msg"))
            }
        }catch (e: JSONException) {
            onError(e.message)
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