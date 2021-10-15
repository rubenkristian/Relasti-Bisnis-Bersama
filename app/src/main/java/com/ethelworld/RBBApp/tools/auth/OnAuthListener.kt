package com.ethelworld.RBBApp.tools.auth

/**
 * Created by RUBEN on 2/24/2019.
 */
interface OnAuthListener {
    suspend fun onLogged()
    suspend fun notLogged()
    suspend fun tacNotSigned()
}
