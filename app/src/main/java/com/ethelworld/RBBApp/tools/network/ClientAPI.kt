package com.ethelworld.RBBApp.tools.network

import android.content.Context
import com.ethelworld.RBBApp.tools.auth.Authentication
import okhttp3.*
import ru.gildor.coroutines.okhttp.await


class ClientAPI constructor(private val context: Context?) {
    private val url: String = "https://animaxi.my.id/rbbx/api/"

    private val client = OkHttpClient()

    suspend fun postAPI(tag: String, point: String, formPost: RequestBody): Response {
        val token = Authentication(context).getValueString(Authentication.TOKEN)
        val request = Request
                .Builder()
                .tag(tag)
                .header("authorization", "Bearer $token")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(url + point)
                .post(formPost)
                .build()
        return client.newCall(request).await()
    }

    suspend fun getAPI(tag: String, point: String): Response {
        val token = Authentication(context).getValueString(Authentication.TOKEN)
        val request = Request
                .Builder()
                .tag(tag)
                .header("authorization", "Bearer $token")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(url + point)
                .get()
                .build()
        return client.newCall(request).await()
    }

    suspend fun userAuth(userkey: RequestBody): Response {
        val request = Request
                .Builder()
                .tag(AUTHTAG)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("https://animaxi.my.id/rbbx/auth/authentication")
                .post(userkey)
                .build()
        return client.newCall(request).await()
    }

    fun cancelCall(tag: String) {
        client.dispatcher.queuedCalls().forEach { call->
            if(call.request().tag()?.equals(tag)!!) {
                call.cancel()
            }
        }
        client.dispatcher.runningCalls().forEach { call->
            if(call.request().tag()?.equals(tag)!!) {
                call.cancel()
            }
        }
    }

    companion object {
        const val AUTHTAG = "auth_user"
    }
}