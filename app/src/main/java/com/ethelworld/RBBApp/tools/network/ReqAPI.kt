package com.ethelworld.RBBApp.tools.network

import android.content.Context
import androidx.annotation.Nullable
import com.android.volley.*
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.isActive
import java.nio.charset.Charset
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Throws

import com.ethelworld.RBBApp.tools.auth.Authentication as authenticationLocal

class ReqAPI constructor(private val context: Context?) {
//    private val url: String? = "http://192.168.42.70/rbb/api/"
    private val tagauth: String = "RBBAUTH"
    private var token: String? = authenticationLocal(context).getValueString(authenticationLocal.TOKEN)

    companion object{
        const val url           = "https://rbb.ethel-world.com/"
        const val apiPoint      = "https://rbb.ethel-world.com/api/"
        const val cityPoint     = "https://rbb.ethel-world.com/city/"
        const val bankPoint     = "https://rbb.ethel-world.com/bank/"
        const val provincePoint = "https://rbb.ethel-world.com/province/"
    }

    suspend fun load(urlPoint: String): RequestResult {
        return suspendCoroutine { cont->
            val success = Listener<String> { response ->
                if(cont.context.isActive) cont.resume(RequestResult(null, response))
            }

            val error = ErrorListener { error: VolleyError? ->
                if(cont.context.isActive) cont.resume(RequestResult(error, ""))
            }
            val request = StringRequest("${url}/${urlPoint}", success, error)
            VolleySingleton.getInstance(context).addToRequestQueue(request)
        }
    }

    suspend fun requestAPI(tag: String, method: Int, point: String, data: Map<String, String>): RequestResult {
        return suspendCoroutine { cont ->
            val success = Listener<String> { response ->
                if(cont.context.isActive) cont.resume(RequestResult(null, response))
            }

            val error = ErrorListener { error: VolleyError? ->
                if(cont.context.isActive) cont.resume(RequestResult(error, ""))
            }

            val request: StringRequest =
                    object : StringRequest(method, point, success, error) {
                        @Throws(AuthFailureError::class)
                        override fun getHeaders(): Map<String, String> {
                            val params: MutableMap<String, String> =
                                    HashMap()
                            params["authorization"] = "Bearer $token"
                            params["Content-Type"] = "application/x-www-form-urlencoded"
                            return params
                        }

                        @Nullable
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            return data
                        }
                    }
            request.tag = tag
            VolleySingleton.getInstance(context).addToRequestQueue(request)
        }
    }

    suspend fun requestAPI(
        tag: String,
        method: Int,
        point: String?,
        data: Map<String, String>?,
        cacheExp: Long?
    ): RequestResult {
        return suspendCoroutine { cont ->
            val success = Listener<NetworkResponse?> { response ->
                if (response?.statusCode == 200) {
                    val body = String(
                            response.data,
                            Charset.forName(HttpHeaderParser.parseCharset(response.headers))
                    )
                    if(cont.context.isActive) cont.resume(RequestResult(null, body))
                }
            }

            val error = ErrorListener { error: VolleyError? ->
                if(cont.context.isActive) cont.resume(RequestResult(error, ""))
            }
            val request: CacheRequest = object :
                    CacheRequest(method, point, cacheExp, success, error) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> =
                            HashMap()
                    params["authorization"] = "Bearer $token"
                    params["Content-Type"] = "application/x-www-form-urlencoded"
                    return params
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    return data
                }
            }
            request.tag = tag
            VolleySingleton.getInstance(context).addToRequestQueue(request)
        }
    }

    suspend fun authentication(username: String, password: String): RequestResult {
        return suspendCoroutine { cont ->
            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                if(cont.context.isActive) cont.resume(RequestResult(VolleyError("username and password must filled"), ""))
            }
            val success = Listener<String> { response ->
                if(cont.context.isActive) cont.resume(RequestResult(null, response))
            }

            val error = ErrorListener { error: VolleyError? ->
                if(cont.context.isActive) cont.resume(RequestResult(error, ""))
            }

            val request: StringRequest = object : StringRequest(Method.POST, "${url}auth/authentication", success, error) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["username"] = username
                    params["password"] = password
                    return params
                }
            }

            request.tag = tagauth
            VolleySingleton.getInstance(context).addToRequestQueue(request)
        }
    }

    fun cancelAuth() {
        VolleySingleton.getInstance(context).cancelRequest(tagauth)
    }

    fun cancelRequest(tag: String) {
        VolleySingleton.getInstance(context).cancelRequest(tag)
    }
}