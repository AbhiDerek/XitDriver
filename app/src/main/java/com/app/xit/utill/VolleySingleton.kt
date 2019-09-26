package com.app.xit.utill

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

object VolleySingletion {

    private lateinit var context: Context

    val requestQueque: RequestQueue by lazy {
        Volley.newRequestQueue(context
            ?: throw NullPointerException(" Initialize VolleySingletion in application class"))
    }

    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueque, LruBtimapCache())
    }

    fun initConfi(context: Context) {
        this.context = context.applicationContext
    }

}