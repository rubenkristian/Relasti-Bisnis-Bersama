package com.ethelworld.RBBApp.tools.network

interface OnInfoWithdrawListener {
    suspend fun onInfoWithdrawSuccess(result: String)
}