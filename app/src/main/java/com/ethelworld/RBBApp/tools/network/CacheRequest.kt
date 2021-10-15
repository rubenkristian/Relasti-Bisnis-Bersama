package com.ethelworld.RBBApp.tools.network

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser

open class CacheRequest(
    method: Int,
    url: String?,
    cacheExp: Long?,
    listener: Response.Listener<NetworkResponse?>?,
    errorListener: Response.ErrorListener?
) :
    Request<NetworkResponse?>(method, url, errorListener) {
    private val mListener: Response.Listener<NetworkResponse?>? = listener
    private val mErrorListener: Response.ErrorListener? = errorListener
    private var cacheExp: Long? = null
    override fun parseNetworkResponse(response: NetworkResponse?): Response<NetworkResponse?>? {
        var cacheEntry: Cache.Entry? = HttpHeaderParser.parseCacheHeaders(response)
        if (cacheEntry == null) {
            cacheEntry = Cache.Entry()
        }
        val cacheHitButRefreshed =
            3 * 60 * 1000.toLong() // in 3 minutes cache will be hit, but also refreshed on background
        val cacheExpired =
            cacheExp ?: 24 * 60 * 60 * 1000 // in 24 hours this cache entry expires completely
        val now = System.currentTimeMillis()
        val softExpire = now + cacheHitButRefreshed
        val ttl = now + cacheExpired
        cacheEntry.data = response!!.data
        cacheEntry.softTtl = softExpire
        cacheEntry.ttl = ttl
        var headerValue: String? = response.headers?.get("Date")
        if (headerValue != null) {
            cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue)
        }
        if (headerValue != null) {
            headerValue = response.headers?.get("Last-Modified")
        }
        if (headerValue != null) {
            cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue)
        }
        cacheEntry.responseHeaders = response.headers
        return Response.success(response, cacheEntry)
    }

    override fun deliverResponse(response: NetworkResponse?) {
        mListener?.onResponse(response)
    }

    override fun parseNetworkError(volleyError: VolleyError?): VolleyError? {
        return super.parseNetworkError(volleyError)
    }

    override fun deliverError(error: VolleyError?) {
        mErrorListener?.onErrorResponse(error)
    }

    init {
        this.cacheExp = cacheExp
    }
}