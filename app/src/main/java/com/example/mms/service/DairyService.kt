package com.example.mms.service

import android.content.Context
import com.example.mms.database.inApp.SingletonDatabase

/**
 * Service to manage notes
 *
 * @param context The context of the application
 * @property db The database of the application
 */
class DairyService(context: Context) {
    private var db = SingletonDatabase.getDatabase(context)
}