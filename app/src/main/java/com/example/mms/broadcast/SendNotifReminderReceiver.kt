package com.example.mms.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mms.model.Takes
import com.example.mms.service.NotifService
import com.example.mms.service.TasksService
import java.time.LocalDateTime

class SendNotifReminderReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }

        val medicineName = intent.getStringExtra("medicineName")
        val hourWeightId = intent.getIntExtra("hourWeightId", -1)
        val hasEnoughStock = intent.getBooleanExtra("hasEnoughStock", false)

        if (medicineName == null || hourWeightId == -1) {
            return
        }

        val now = LocalDateTime.now()
        val tasksService = TasksService(context)

        var takes: Takes? = null
        val thread = Thread {
            takes = tasksService.getOrCreateTakes(hourWeightId, now)
        }
        thread.start()
        thread.join()

        if (takes!!.isDone) {
            return
        }

        val notifService = NotifService(context)
        notifService.sendReminder(medicineName, takes!!, hasEnoughStock)
    }
}
