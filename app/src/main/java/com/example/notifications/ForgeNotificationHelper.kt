package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.TaskItem

object ForgeNotificationHelper {

    const val CHANNEL_BURNING_TASKS = "channel_burning_tasks"
    const val CHANNEL_HABIT_GUARD = "channel_habit_guard"

    private const val NOTIFICATION_ID_BURNING = 2001
    private const val NOTIFICATION_ID_HABIT_GUARD = 2002

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            // Channel 1: Burning Tasks
            val burningChannel = NotificationChannel(
                CHANNEL_BURNING_TASKS,
                "Task Burning Alert 🔥",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts when high-priority tasks and deadlines are burning down"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#FF6A00")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 300)
                setSound(defaultSoundUri, audioAttributes)
            }

            // Channel 2: Habit Streak Guard
            val habitGuardChannel = NotificationChannel(
                CHANNEL_HABIT_GUARD,
                "Habit Streak Guard 🛡️",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical evening alerts before midnight to protect your active streaks"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#FF9500")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 150, 100, 150)
                setSound(defaultSoundUri, audioAttributes)
            }

            notificationManager.createNotificationChannel(burningChannel)
            notificationManager.createNotificationChannel(habitGuardChannel)
        }
    }

    fun sendBurningTaskAlert(
        context: Context,
        taskTitle: String,
        priority: String = "Urgent",
        timeRemainingText: String = "Due today!"
    ) {
        initNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to_tab", 1) // Open Tasks tab
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BURNING_TASKS)
            .setSmallIcon(R.drawable.ic_stat_forge)
            .setContentTitle("🔥 TASK BURNING: $priority Priority")
            .setContentText("“$taskTitle” is about to expire! ($timeRemainingText)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("“$taskTitle” requires immediate execution. Protect your client commitments and keep your momentum alive.")
                    .setBigContentTitle("🔥 Task Burning: $taskTitle")
                    .setSummaryText("Forge Task Alert")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(android.graphics.Color.parseColor("#FF3B30"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_agenda,
                "Open Task",
                pendingIntent
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BURNING, notification)
        } catch (_: SecurityException) {
            // Permission not granted on Android 13+
        }
    }

    fun sendHabitGuardAlert(
        context: Context,
        uncompletedCount: Int,
        streakDays: Int = 12
    ) {
        initNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to_tab", 0) // Open Today tab
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            102,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HABIT_GUARD)
            .setSmallIcon(R.drawable.ic_stat_forge)
            .setContentTitle("🛡️ STREAK GUARD: $uncompletedCount habits remaining")
            .setContentText("Don't let your $streakDays-day streak die tonight! Tap to log.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("You have $uncompletedCount routine habits unchecked for today. The rule of champions: Never miss twice. Take 5 minutes now.")
                    .setBigContentTitle("🛡️ Streak Under Threat ($streakDays Days Active)")
                    .setSummaryText("Forge Habit Guard")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(android.graphics.Color.parseColor("#FF6A00"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_compass,
                "Log Habits Now",
                pendingIntent
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_HABIT_GUARD, notification)
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
