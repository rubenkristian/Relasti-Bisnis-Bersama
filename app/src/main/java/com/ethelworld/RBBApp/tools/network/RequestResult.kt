package com.ethelworld.RBBApp.tools.network

import com.android.volley.VolleyError

data class RequestResult(val error: VolleyError?, val response: String)