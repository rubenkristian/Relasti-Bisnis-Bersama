package com.ethelworld.RBBApp.tools.network

interface OnInfoListener {
    suspend fun onEditInfoSuccess(result: String)
    suspend fun onBankSuccess(result: String)
    suspend fun onBankSaveSuccess(result: String)
}