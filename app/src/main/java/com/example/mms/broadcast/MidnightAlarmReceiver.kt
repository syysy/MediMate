package com.example.mms.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mms.service.NotifService
import com.example.mms.service.TasksService

/**
 * Receiver triggered at midnight to planify daily notifications.
 */
class MidnightAlarmReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context?, intent: Intent?) {
        // Check if the context is null
        if (context == null) {
            return
        }

        // Get the hour weights for all users
        val tasksService = TasksService(context)
        val todaysShowableHourWeights = tasksService.getTodaysHourWeightsForAllUsers()

        // Planify the notifications
        val notifService = NotifService(context)
        notifService.planifyTakesNotifications(todaysShowableHourWeights)
    }
}
