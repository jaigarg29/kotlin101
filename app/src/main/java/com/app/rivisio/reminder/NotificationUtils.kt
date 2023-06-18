package com.app.rivisio.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.rivisio.R
import com.app.rivisio.ui.splash.SplashActivity

object NotificationUtils {

    private val NOTIFICATION_ID = 101

    fun createNotificationChannels(context: Context) {

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = context.getString(R.string.reminders_notification_channel)
            val descriptionText = context.getString(R.string.reminders_notification_channel)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(
                context.getString(R.string.reminders_notification_channel_id),
                name,
                importance
            )
            mChannel.description = descriptionText
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun renderNotification(context: Context) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val contentIntent = Intent(context, SplashActivity::class.java)
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(
            context,
            context.getString(R.string.reminders_notification_channel_id)
        )
            .setContentTitle(context.getString(R.string.title_notification_reminder))
            .setContentText(context.getString(R.string.description_notification_reminder))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.description_notification_reminder))
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}