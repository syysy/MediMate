package com.example.mms.service

import android.content.Context
import com.android.volley.toolbox.Volley
import kotlinx.serialization.json.Json

abstract class Api constructor(
    context: Context,
    protected val url: String
) {
    protected val queue = Volley.newRequestQueue(context)

    protected val json = Json { ignoreUnknownKeys = true }

    protected fun makeUrl(path: String): String {
        return this.url + path
    }

}
