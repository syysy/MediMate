package com.example.mms.service

import android.content.Context
import com.example.mms.constant.API_URL_DOCTOR

class DoctorApiService private constructor(context: Context): Api(context, API_URL_DOCTOR) {

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
