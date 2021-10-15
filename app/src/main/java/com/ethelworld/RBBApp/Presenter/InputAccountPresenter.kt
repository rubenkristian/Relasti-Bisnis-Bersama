package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.View.InputAccountView
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnRegisterListener
import com.ethelworld.RBBApp.tools.network.OnStringListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class InputAccountPresenter(
    private var view: InputAccountView.View?,
    val context: Context): InputAccountView.Presenter, OnRegisterListener, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)

    companion object {
        const val TAGACCOUNT    = "ACCOUNTREQ"
        const val TAGPROVINCE   = "PROVINCEREQ"
        const val TAGCITY       = "CITYREQ"
        const val TAGBANK       = "BANKREQ"
    }

    override suspend fun postAccount(
        name: String,
        phone: String,
        email: String,
        gender: Int?,
        occupation: String,
        company: String,
        province: Int?,
        city: Int?,
        bank: Int?,
        bankNumber: String,
        bankName: String,
        facebook: String,
        instagram: String,
        tiktok: String,
        olshop: String,
        youtube: String,
        password: String,
        confirmPassword: String
    ) {
        view?.showLoading()

        val data: HashMap<String, String> = HashMap()

        data["idcreated"] = Authentication(context).getValueInt(Authentication.ID).toString()
        data["name"]        = name
        data["wa"]          = phone
        data["email"]       = email
        data["gender"]      = gender.toString()
        data["occupation"]  = occupation
        data["company"]     = company
        data["province"]    = province.toString()
        data["city"]        = city.toString()
        data["bank"]        = bank.toString()
        data["bank_number"] = bankNumber
        data["bank_name"]   = bankName
        data["fb"]          = facebook
        data["ig"]          = instagram
        data["tiktok"]      = tiktok
        data["olshop"]      = olshop
        data["yt"]          = youtube
        data["password"]    = password
        data["repassword"]  = confirmPassword

        val requestResult = api.requestAPI(
            TAGACCOUNT,
            Request.Method.POST,
            "${ReqAPI.apiPoint}createpartner",
            data)

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun getProvince() {
        val data: HashMap<String, String> = HashMap()

        val requestResult = api.requestAPI(
            TAGPROVINCE,
            Request.Method.GET,
            "${ReqAPI.provincePoint}get",
            data,
            TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onProvinceSuccess(requestResult.response)
        }
    }

    override suspend fun getCity(provinceId: Int?) {
        val data: HashMap<String, String> = HashMap()

        val requestResult = api.requestAPI(
            TAGCITY,
            Request.Method.GET,
            "${ReqAPI.cityPoint}get?idprovince=${provinceId.toString()}",
            data,
            TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))

        if(requestResult.error!= null){
            onError(requestResult.error)
        } else {
            onCitySuccess(requestResult.response)
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

    override suspend fun onSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if(resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")

                view?.onSuccess(data)
            }else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onProvinceSuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if (resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")

                view?.onProvince(data)
            } else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onCitySuccess(result: String) {
        view?.hideLoading()

        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if (resjson.getBoolean("status")) {
                val data = resjson.getJSONObject("data")

                view?.onCity(data)
            } else {
                onError(resjson.getString("msg"))
            }
        } catch (e: JSONException) {
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

        api.cancelRequest(TAGACCOUNT)
        api.cancelRequest(TAGPROVINCE)
        api.cancelRequest(TAGCITY)
        api.cancelRequest(TAGBANK)
    }
}