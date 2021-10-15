package com.ethelworld.RBBApp.tools.network

interface OnForgotPassword {
    suspend fun onResultEmail(result: String)
    suspend fun onResultResend(result: String)
    suspend fun onResultConfirm(result: String)
    suspend fun onResultPassword(result: String)
}