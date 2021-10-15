package com.ethelworld.RBBApp.Presenter

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.ethelworld.RBBApp.BuildConfig
import com.ethelworld.RBBApp.View.SplashScreenView
import com.ethelworld.RBBApp.tools.auth.OnAuthListener
import com.ethelworld.RBBApp.tools.network.ReqAPI
import com.ethelworld.RBBApp.tools.auth.Authentication
import com.ethelworld.RBBApp.tools.network.OnStringListener
import org.json.JSONException
import org.json.JSONObject

import com.ethelworld.RBBApp.tools.auth.Authentication as authenticationLocal

class SplashScreenPresenter(
    private var view: SplashScreenView.View?,
    context: Context?) : SplashScreenView.Presenter, OnAuthListener, OnStringListener {
    private var api: ReqAPI = ReqAPI(context)
    private var authentication: authenticationLocal? = null

    companion object {
        const val TAGACCOUNT = "ACCOUNTREQ"
    }

    init {
        authentication = Authentication(context)
    }

    override suspend fun authentication() {
        authentication?.checkLog(this)
//        Log.i("GETACCOUNT", "Authentication")
    }

    override suspend fun getAccountInfo() {
        val data: HashMap<String, String> = HashMap()

        data["id"] = authentication?.getValueInt(Authentication.ID).toString()

        val requestResult = api.requestAPI(
            TAGACCOUNT,
            Request.Method.POST,
            "${ReqAPI.apiPoint}getpersonalinfo",
            data)

        if(requestResult.error != null){
            onError(requestResult.error)
        } else {
            onSuccess(requestResult.response)
        }
    }

    override suspend fun onLogged() {
        getAccountInfo()
    }

    override suspend fun notLogged() {
        view?.onNoLogged()
    }

    override suspend fun tacNotSigned() {
        view?.onTACNotSigned()
    }

    fun onDestroy() {
        view = null

        api.cancelRequest(TAGACCOUNT)
    }

    override suspend fun onSuccess(result: String) {
        val resjson: JSONObject

        try {
            resjson = JSONObject(result)

            if (resjson.getBoolean("status")) {
                val account = resjson.getJSONObject("account")

                authentication?.save(authenticationLocal.NAME, account.getString("fn"))
                authentication?.save(authenticationLocal.ADDRESS, account.getString("add"))
                authentication?.save(authenticationLocal.OCCUPATION, account.getString("occ"))
                authentication?.save(authenticationLocal.LASTADSWATCH, resjson.getString("lasttimeads"))
                authentication?.save(authenticationLocal.CURRENTTIME, resjson.getString("currenttime"))
                authentication?.save(authenticationLocal.STARITEM, resjson.getLong("star"))

                val versionCode     = resjson.getInt("version_code")
                val versionName     = resjson.getString("version_name")
                val versionStatus   = resjson.getInt("version_status")
                val showmenulists   = resjson.getJSONArray("showmenu")

                var index = 0

                while(index < showmenulists.length()) {
                    val showmenulist    = showmenulists.getJSONObject(index)
                    val menuname        = showmenulist.getString("name")
                    val menustat        = showmenulist.getBoolean("status")

                    authentication?.save(menuname, menustat)

                    index++
                }

                if(versionStatus == 1 && BuildConfig.VERSION_CODE < versionCode) {
                    view?.onVersionUpdate(versionName)
                } else {
                    view?.onLogged(BuildConfig.VERSION_CODE < versionCode, versionName)
                }
            } else {
                onError("")
            }
        } catch (e: JSONException) {
            onError(e.hashCode(), e.message)
        }
    }

    override suspend fun onError(error: VolleyError?) {
        view?.onLogged(false, "")
    }

    override suspend fun onError(code: Int, errmsg: String?) {
        view?.onLogged(false, "")
    }

    override suspend fun onError(errmsg: String?) {
        view?.onLogged(false, "")
    }
}