package com.ethelworld.RBBApp.tools.network

interface OnRegisterListener {
    suspend fun onProvinceSuccess(result: String)
    suspend fun onCitySuccess(result: String)
    suspend fun onBankSuccess(result: String)
}