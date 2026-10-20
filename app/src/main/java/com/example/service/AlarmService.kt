package com.example.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("ALARM_TITLE") ?: "تذكير Ramez Psychology"
        val message = intent.getStringExtra("ALARM_MESSAGE") ?: "حان وقت إنجاز مهمتك وفق الخطة السلوكية!"
        val ringOnSilent = intent.getBooleanExtra("RING_ON_SILENT", true)

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val ringerMode = audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL

        val isPhoneSilent = (ringerMode == AudioManager.RINGER_MODE_SILENT || ringerMode == AudioManager.RINGER_MODE_VIBRATE)

        // Vibration
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500, 200, 1000), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 500, 200, 500, 200, 1000), -1)
            }
        } catch (_: Exception) {}

        // Sound if not silent, or if user allowed ringing on silent
        val shouldPlaySound = !isPhoneSilent || ringOnSilent

        val channelId = "ramez_alarm_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Ramez Psychology Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات ومواعيد المهام والعادات السلوكية"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .apply {
                if (shouldPlaySound) {
                    setSound(soundUri)
                }
            }
            .build()

        notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), notification)

        if (shouldPlaySound) {
            try {
                val ringtone = RingtoneManager.getRingtone(context, soundUri)
                ringtone?.play()
            } catch (_: Exception) {}
        }
    }
}

object AlarmScheduler {
    fun scheduleTimer(
        context: Context,
        hours: Int,
        minutes: Int,
        seconds: Int,
        taskTitle: String,
        ringOnSilent: Boolean = true
    ) {
        val totalMillis = ((hours * 3600L) + (minutes * 60L) + seconds) * 1000L
        if (totalMillis <= 0) return

        val triggerTime = System.currentTimeMillis() + totalMillis
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_TITLE", "انتهاء وقت المهمة: $taskTitle")
            putExtra("ALARM_MESSAGE", "انتهى الوقت المحدد لمهمتك. هل قمت بها؟ [تم / تسويف / إلغاء]")
            putExtra("RING_ON_SILENT", ringOnSilent)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskTitle.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
}
