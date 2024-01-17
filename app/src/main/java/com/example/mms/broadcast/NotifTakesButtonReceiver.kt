package com.example.mms.broadcast

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mms.Utils.stringddmmyyyyToDate
import com.example.mms.database.inApp.SingletonDatabase
import java.time.LocalDateTime

/**
 * Receiver triggered when the user clicks on the button of a notification.
 */
class NotifTakesButtonReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // check if the intent and the context are not null
        if (intent == null || context == null) {
            return
        }

        // get the notification id, the hourWeight id and the date
        val notifId = intent.getIntExtra("notifId", -1)
        val hourWeightId = intent.getIntExtra("hourWeightId", -1)
        val dateString = intent.getStringExtra("date")

        // check if the notification id is valid
        if (notifId == -1) {
            return
        }

        // check if the hourWeight id and the date are valid
        if (hourWeightId == -1 || dateString == null) {
            // close the notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notifId)
            return
        }

        // transform the date string into a date
        val date = stringddmmyyyyToDate(dateString)
        val now = LocalDateTime.now()

        try {
            // set the isDone field of the takes to true
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
