package com.example.mms.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mms.database.mongoObjects.MongoVersion
import com.example.mms.model.medicines.Medicine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class ApiService private constructor(context: Context) {
    private val url = "http://138.68.64.36:8080/"
    private val queue = Volley.newRequestQueue(context)

    private val json = Json { ignoreUnknownKeys = true }

    private fun makeUrl(path: String): String {
        return this.url + path
    }

    fun getMedicinesCodesToUpdate(localVersion: Int, callback: (version: MongoVersion) -> Unit, callbackError: () -> Unit) {
        val stringRequest = StringRequest(
            Request.Method.GET, this.makeUrl("version/$localVersion"),
            { response ->
                try {
                    val version = this.json.decodeFromString<MongoVersion>(response)
                    callback(version)
                } catch (e: Exception) {
                    Log.d("json", "error: $e")
                    callbackError()
                }
            },
            {
                callbackError()
            })

        queue.add(stringRequest)
    }

    fun getMedicine(codeCis: Int, callback: (Medicine) -> Unit, errorCallback: () -> Unit) {
        val stringRequest = StringRequest(
            Request.Method.GET, this.makeUrl("medicine/$codeCis"),
            { response ->
                try {
                    val medicine = this.json.decodeFromString<Medicine>(response)
                    callback(medicine)
                } catch (e: Exception) {
                    Log.d("json", "error: $e")
                }
            },
            {
                Log.d("json", "error: $it")
                errorCallback()
            })

        queue.add(stringRequest)
    }

    fun getAllMedicine(): List<Medicine>? {
        throw NotImplementedError()
    }

    companion object {
        @Volatile
        private var INSTANCE: ApiService? = null

        fun getInstance(context: Context): ApiService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApiService(context).also {
                    INSTANCE = it
                }
            }
    }
}
