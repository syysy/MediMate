package com.example.mms.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.mms.constant.API_URL_DOCTOR
import com.example.mms.model.Doctor
import kotlinx.serialization.decodeFromString

class DoctorApiService private constructor(context: Context): Api(context, API_URL_DOCTOR) {

    private fun getDoctor(search: String, callback: (doctor: Doctor) -> Unit, callbackError: () -> Unit) {
        // build the request
        val stringRequest = StringRequest(
            Request.Method.GET, this.makeUrl("rpps?page=1&_per_page=30"),
            { response ->
                try {
                    // try to parse the response into a Doctor
                    val doctor = this.json.decodeFromString<Doctor>(response)
                    callback(doctor)
                } catch (e: Exception) {
                    // if an error occurred, log and call the error callback
                    Log.d("parse_json", "error: $e")
                    callbackError()
                }
            },
            {
                // if the request failed, call the error callback
                callbackError()
            })

        queue.add(stringRequest)
    }

    /**
     * get a doctor by his RPPS
     *
     * @param rpps the RPPS of the doctor
     * @param callback the callback to call when the request is successful
     * @param callbackError the callback to call when the request failed
     */
    fun getDoctorByRPPS(rpps: String, callback: (doctor: Doctor) -> Unit, callbackError: () -> Unit) {
        val searchUrl = "rpps=$rpps"

        this.getDoctor(searchUrl, callback, callbackError)
    }

    /**
     * get a doctor by his name
     *
     * @param firstName the first name of the doctor
     * @param lastName the last name of the doctor
     * @param callback the callback to call when the request is successful
     * @param callbackError the callback to call when the request failed
     */
    fun getDoctorByName(firstName: String, lastName: String, callback: (doctor: Doctor) -> Unit, callbackError: () -> Unit) {
        val searchUrl = "firstName=$firstName&lastName=$lastName"

        this.getDoctor(searchUrl, callback, callbackError)
    }

    /**
     * Singleton pattern
     */
    companion object {
        @Volatile
        private var INSTANCE: DoctorApiService? = null

        /**
         * get the instance of the service
         */
        fun getInstance(context: Context): DoctorApiService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DoctorApiService(context).also {
                    INSTANCE = it
                }
            }
    }
}
