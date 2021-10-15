package com.ethelworld.RBBApp.tools.network

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

class InputStreamVolleyRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<ByteArray>,
    errListener:Response.ErrorListener,
    private val params: HashMap<String, String>): Request<ByteArray>(method, url, errListener) {
    private lateinit var responseHeaders: HashMap<String, String>
    init {
        setShouldCache(false)
    }

    override fun getParams(): MutableMap<String, String> {
        return params
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<ByteArray> {
        responseHeaders = response?.headers as HashMap<String, String>
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: ByteArray?) {
        listener.onResponse(response)
    }
}