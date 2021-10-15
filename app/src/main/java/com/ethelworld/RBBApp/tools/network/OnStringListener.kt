package com.ethelworld.RBBApp.tools.network

import com.android.volley.VolleyError


/**
 * Created by RUBEN on 2/24/2019.
 */
interface OnStringListener {
    suspend fun onSuccess(result: String)
    suspend fun onError(error: VolleyError?)
    suspend fun onError(code: Int, errmsg: String?)
    suspend fun onError(errmsg: String?)
}
