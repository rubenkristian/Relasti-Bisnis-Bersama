package com.ethelworld.RBBApp.tools.network

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

class VolleySingleton constructor(context: Context?) {
    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null
        fun getInstance(context: Context?) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
                object : ImageLoader.ImageCache {
                    private val cache = LruCache<String, Bitmap>(20)
                    override fun getBitmap(url: String?): Bitmap {
                        return cache.get(url)
                    }

                    override fun putBitmap(url: String?, bitmap: Bitmap?) {
                        cache.put(url, bitmap)
                    }
                })
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context?.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        req.retryPolicy = DefaultRetryPolicy(60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(req)
    }

    fun cancelRequest(tag: String) {
        requestQueue.cancelAll(tag)
    }
}