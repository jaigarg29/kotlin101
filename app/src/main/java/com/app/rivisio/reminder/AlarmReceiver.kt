package com.app.rivisio.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var remindersManager: RemindersManager
    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     * */
    override fun onReceive(context: Context, intent: Intent) {

        NotificationUtils.createNotificationChannels(context)

        NotificationUtils.renderNotification(context)

        // Remove this line if you don't want to reschedule the reminder
        remindersManager.startReminder(context.applicationContext)
    }
}