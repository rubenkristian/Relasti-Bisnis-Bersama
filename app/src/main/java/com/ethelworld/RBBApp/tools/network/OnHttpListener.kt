package com.ethelworld.RBBApp.tools.network

interface OnHttpListener {
    suspend fun OnPendingSuccess(result: String)
}