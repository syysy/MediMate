package com.example.mms.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.mms.R
import com.example.mms.Utils.dateToString
import com.example.mms.Utils.stringHourMinuteToInt
import com.example.mms.broadcast.NotifTakesButtonReceiver
import com.example.mms.broadcast.SendNotifReminderReceiver
import com.example.mms.model.ShowableHourWeight
import com.example.mms.model.Takes
import com.example.mms.ui.loader.LoaderActivity
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Service to send or planify notifications
 * Singleton pattern
 *
 * @property context The context of the application
 * @property CHANNEL_MEDI_REMINDER The channel id of reminder notifications
 * @property KEY_NOTIFICATION_ID The key of the notification id in the shared preferences
 * @property PREFS_NAME The name of the shared preferences
 * @property sharedPref The shared preferences of the application
 *
 */
class NotifService(
    private val context: Context
) {
    private val CHANNEL_MEDI_REMINDER = "TAKE_MEDICINE_REMINDER"

    private val PREFS_NAME = "notification_prefs"
    private val KEY_NOTIFICATION_ID = "notification_id"
    private val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    /**
     *
     */
    fun createNotificationChannel() {
        val name = this.context.getString(R.string.channel_name_reminder)
        val descriptionText = this.context.getString(R.string.channel_description_reminder)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(this.CHANNEL_MEDI_REMINDER, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system.
        val notificationManager: NotificationManager =
            this.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendReminder(medicineName: String, takes: Takes, hasEnoughStock: Boolean) {
        val title = medicineName
        val message = this.context.getString(R.string.notification_message)

        this.createNotificationChannel()

        val notifId = this.getNextNotificationId()

        // Create an intent to open the app when the user clicks on the notification
        val intent = Intent(this.context, LoaderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this.context, 3, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this.context, this.CHANNEL_MEDI_REMINDER)
            .setSmallIcon(R.drawable.medicament)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        // If enough stock, we can take on the notification
        if (hasEnoughStock) {
            val dateString = dateToString(takes.date.dayOfMonth, takes.date.monthValue, takes.date.year)
            val takeMedicineIntent = Intent(this.context, NotifTakesButtonReceiver::class.java).apply {
                putExtra("notifId", notifId)
                putExtra("hourWeightId", takes.hourWeightId)
                putExtra("date", dateString)
            }

            val takeMedicinePendingIntent = PendingIntent.getBroadcast(this.context, notifId, takeMedicineIntent, PendingIntent.FLAG_IMMUTABLE)
            builder.addAction(
                R.drawable.medicament,
                this.context.getString(R.string.pris),
                takeMedicinePendingIntent
            )
        }

        // Get an instance of NotificationManager
        val notificationManager: NotificationManager =
            this.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notify the notification
        notificationManager.notify(notifId, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun planifyTakesNotifications(showableHourWeights: MutableList<ShowableHourWeight>) {
        for (hourWeight in showableHourWeights) {
            this.planifyOneNotification(hourWeight)
        }
    }

    // now est utilisé pour les tests
    @RequiresApi(Build.VERSION_CODES.S)
    fun planifyOneNotification(showableHourWeight: ShowableHourWeight, now: LocalDateTime = LocalDateTime.now()): Boolean {
        val alarmManager = this.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this.context, SendNotifReminderReceiver::class.java).apply {
            putExtra("medicineName", showableHourWeight.medicineName)
            putExtra("hourWeightId", showableHourWeight.hourWeight.id)
            putExtra("hasEnoughStock", showableHourWeight.hasEnoughStock())
        }

        val hourMin = stringHourMinuteToInt(showableHourWeight.hourWeight.hour)

        // si l'heure est déjà passée, on retourne false
        if (now.hour > hourMin.first || (now.hour == hourMin.first && now.minute > hourMin.second)) {
            return false
        }

        val takesDate = now
            .withHour(hourMin.first)
            .withMinute(hourMin.second)
            .withSecond(0)

        val pendingIntent = PendingIntent.getBroadcast(this.context, showableHourWeight.hourWeight.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val zonedDateTime = ZonedDateTime.of(takesDate, ZoneOffset.systemDefault())

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                zonedDateTime.toEpochSecond() * 1000,
                pendingIntent
            )
            return true
        }
        return false
    }

    fun getNextNotificationId(): Int {
        // Get the current notification ID
        val prevNotificationId = sharedPref.getInt(KEY_NOTIFICATION_ID, 3)
        val notificationId = prevNotificationId + 1

        // Increment the notification ID and store it
        with(sharedPref.edit()) {
            putInt(KEY_NOTIFICATION_ID, notificationId)
            apply()
        }

        return notificationId
    }
}
