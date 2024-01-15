package com.example.mms.broadcast

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mms.Utils.stringddmmyyyyToDate
import com.example.mms.database.inApp.SingletonDatabase
import java.time.LocalDateTime


class NotifTakesButtonReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            return
        }

        val notifId = intent.getIntExtra("notifId", -1)
        val hourWeightId = intent.getIntExtra("hourWeightId", -1)
        val dateString = intent.getStringExtra("date")

        if (notifId == -1) {
            return
        }

        if (hourWeightId == -1 || dateString == null) {
            // close the notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notifId)
            return
        }

        val date = stringddmmyyyyToDate(dateString)
        val now = LocalDateTime.now()

        try {
            val db = SingletonDatabase.getDatabase(context)
            val takesDao = db.takesDao()
            val thread = Thread {
                takesDao.updateIsDone(true, hourWeightId, date, now)
            }
            thread.start()
            thread.join()
        } catch (e: Exception) {
            Log.d("notifTakeBroadcast", e.toString())
        }

        // close the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notifId)
    }
}
