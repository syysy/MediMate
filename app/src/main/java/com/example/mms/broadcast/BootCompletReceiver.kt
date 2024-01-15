package com.example.mms.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mms.service.NotifService
import com.example.mms.service.TasksService

class BootCompletReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }

        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            // Get the hour weights for all users
            val tasksService = TasksService(context)
            val todaysShowableHourWeights = tasksService.getTodaysHourWeightsForAllUsers()

            // Planify the notifications
            val notifService = NotifService(context)
            notifService.planifyTakesNotifications(todaysShowableHourWeights)
        }
    }
}
