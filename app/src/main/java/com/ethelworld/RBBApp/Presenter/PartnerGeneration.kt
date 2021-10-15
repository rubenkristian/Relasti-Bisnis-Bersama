package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.Item.Generation
import com.ethelworld.RBBApp.View.PartnerGenerationView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject

class PartnerGeneration(
    private var view: PartnerGenerationView.View?,
    private val context: Context) : PartnerGenerationView.Presenter, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAGPARTNER = "PARTNERGENREQ"
    }

    override suspend fun getGenerationInfo() {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["id"] = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            TAGPARTNER,
            Request.Method.POST,
            "${ReqAPI.apiPoint}generation",
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
                val generations     = resjson.getJSONArray("generation")
                val generationList  = ArrayList<Generation?>()

                for(i in 0 until generations.length()) {
                    val generation = generations.getJSONObject(i)

                    generationList.add(
                        Generation(
                            generation.getInt("index"),
                            generation.getInt("users")))
                }

                view?.onSuccess(generationList)
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
        api.cancelRequest(TAGPARTNER)
    }

}