package com.example.mms.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.mms.constant.API_URL_DOCTOR
import com.example.mms.model.Doctor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DoctorApiService private constructor(context: Context): Api(context, API_URL_DOCTOR) {

    private fun getField(map: Map<String, String>, field: String): String? {
        return when (map[field]) {
            null -> null
            "null" -> null
            else -> map[field]
        }
    }

    private fun getDoctor(search: String, callback: (doctors: List<Doctor>) -> Unit, callbackError: () -> Unit) {
        // build the request
        val stringRequest = StringRequest(
            Request.Method.GET, this.makeUrl("rpps?page=1&_per_page=30&$search"),
            { response ->
                try {
                    val gson = Gson()
                    val apiResponse = gson.fromJson<Map<String, Any>>(response, object : TypeToken<Map<String, Any>>() {}.type)

                    // try to parse the response into a Doctor
                    val jsonString = apiResponse["hydra:member"]
                        .toString()
                        .replace("=", "=\"")
                        .replace(", ", "\", ")
                        .replace("}]", "\"}]")
                    val doctorsMap = gson.fromJson<List<Map<String, String>>>(jsonString, object : TypeToken<List<Map<String, String>>>() {}.type)

                    val doctors = mutableListOf<Doctor>()
                    for (doctorMap in doctorsMap) {
                        val doctor = Doctor(
                            doctorMap["rpps"] ?: "rpps",
                            doctorMap["firstName"] ?: "firstName",
                            doctorMap["lastName"] ?: "lastName",
                            this.getField(doctorMap, "fullName"),
                            this.getField(doctorMap, "phoneNumber"),
                            this.getField(doctorMap, "email"),
                            this.getField(doctorMap, "address"),
                            this.getField(doctorMap, "city"),
                            this.getField(doctorMap, "zipcode")
                        )

                        doctors.add(doctor)
                    }

                    Log.d(":3", doctors.toString())
                    callback(doctors)
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
    fun getDoctorByRPPS(rpps: String, callback: (doctors: List<Doctor>) -> Unit, callbackError: () -> Unit) {
        val searchUrl = "idRpps=$rpps"

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
    fun getDoctorByName(firstName: String, lastName: String, callback: (doctors: List<Doctor>) -> Unit, callbackError: () -> Unit) {
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
