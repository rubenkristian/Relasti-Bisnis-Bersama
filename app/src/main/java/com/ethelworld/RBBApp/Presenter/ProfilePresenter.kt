package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.ProfileView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnInfoListener
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ProfilePresenter(
    private var view: ProfileView.View?,
    private var context: Context?): ProfileView.Presenter, OnInfoListener, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAG           = "PROFILEREQ"
        const val TAGEDIT       = "EDITREQ"
        const val TAGBANK       = "BANKREQ"
        const val TAGSAVEBANK   = "BANKSAVEREQ"
    }

    override suspend fun getProfile() {
        view?.showLoading()
        api.cancelRequest(TAG)

        val data: HashMap<String, String> = HashMap()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            TAG,
            Request.Method.GET,
            "${ReqAPI.apiPoint}profile?id=$id",
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

    override suspend fun updateInfo(
        facebook: String,
        instagram: String,
        olshop: String,
        tiktok: String,
        youtube: String
    ) {
        view?.showLoading()
        api.cancelRequest(TAG)

        val data: HashMap<String, String> = HashMap()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        data["id"] = id
        data["fb"] = facebook
        data["ig"] = instagram
        data["os"] = olshop
        data["tt"] = tiktok
        data["yt"] = youtube

        val requestResult = api.requestAPI(
            TAGEDIT,
            Request.Method.POST,
            "${ReqAPI.apiPoint}editinfo",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onEditInfoSuccess(requestResult.response)
        }
    }

    override suspend fun getBank() {
        val data: HashMap<String, String> = HashMap()

        val requestResult = api.requestAPI(
            TAGBANK,
            Request.Method.GET,
            "${ReqAPI.bankPoint}get",
            data,
            TimeUnit.MILLISECONDS.convert(
                1,
                TimeUnit.DAYS))

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onBankSuccess(requestResult.response)
        }
    }

    override suspend fun saveBank(bank: Int?, bankNumber: String, bankName: String) {
        val data: HashMap<String, String> = HashMap()

        val id = Authentication(context).getValueInt(Authentication.ID).toString()

        data["id"]          = id
        data["bank"]        = bank.toString()
        data["bank_number"] = bankNumber
        data["bank_name"]   = bankName

        val requestResult = api.requestAPI(
            TAGSAVEBANK,
            Request.Method.POST,
            "${ReqAPI.apiPoint}savebank",
            data)

        if(requestResult.error != null) {
            onError(requestResult.error)
        } else {
            onBankSaveSuccess(requestResult.response)
        }
    }

    override suspend fun onEditInfoSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson     = JSONObject(result)
            val status  = resjson.getBoolean("status")

            if(status) {
                view?.onUpdateSuccess(resjson.getJSONObject("data"))
            } else {
                onError(resjson.getString("msg"))
            }
        }catch (e: JSONException){
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onBankSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if (resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")

                view?.onBank(data)
            } else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onBankSaveSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if (resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")

                view?.onUpdateBank(data)
            } else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
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