package com.ethelworld.RBBApp.View

interface SplashScreenView {
    interface View {
        suspend fun onLogged(updated: Boolean, versionName: String)
        suspend fun onNoLogged()
        suspend fun onTACNotSigned()
        suspend fun onVersionUpdate(versionName: String)
    }

    interface Presenter {
        suspend fun authentication()
        suspend fun getAccountInfo()
    }
}