package com.ady.studybuddy5

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.app.Service
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var isRunning: Boolean = false
    private var currentCycle: Int = 1
    private var isBreakTime: Boolean = false
    private val totalCycles = 2 // Total cycles you want to track

    companion object {
        const val TIMER_UPDATE_ACTION = "com.ady.studybuddy5.TIMER_UPDATE"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"
        const val ACTION_PAUSE = "com.ady.studybuddy5.ACTION_PAUSE"
        const val ACTION_STOP = "com.ady.studybuddy5.ACTION_STOP"
        const val ACTION_CONTINUE = "com.ady.studybuddy5.ACTION_CONTINUE"
        const val ACTION_NEXT_CYCLE = "com.ady.studybuddy5.ACTION_NEXT_CYCLE"
        const val CHANNEL_ID = "PomodoroTimerChannel"
        const val TIMER_PROCESS_COMPLETE_ACTION = "com.ady.studybuddy5.TIMER_PROCESS_COMPLETE"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_PAUSE -> pauseTimer()
            ACTION_STOP -> stopTimer()
            ACTION_CONTINUE -> {
                if (!isRunning) startTimer()
            }
            ACTION_NEXT_CYCLE -> handleNextCycle()
            else -> {
                // Initialize the timer
                timeLeftInMillis = intent?.getLongExtra(EXTRA_TIME_LEFT, 0L) ?: 0L
                if (!isRunning) {
                    startForeground(1, buildNotification(timeLeftInMillis))
                    startTimer()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun handleNextCycle() {
        if (timeLeftInMillis == 0L) {
            // If in break time, move to next work cycle
            if (isBreakTime) {
                currentCycle++
                isBreakTime = false
                if (currentCycle > totalCycles) {
                    // All cycles completed, show notification
                    showCompletionNotification()
                    stopSelf()
                } else {
                    // Start next work phase
                    timeLeftInMillis = 25 * 60 * 1000 // Work time
                    startTimer()
                }
            } else {
                // If in work time, move to break time
                isBreakTime = true
                timeLeftInMillis = 5 * 60 * 1000 // Break time
                startTimer()
            }
        }
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        updateNotification(timeLeftInMillis)
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        isRunning = false
        stopSelf()
    }

    private fun startTimer() {
        isRunning = true
        updateNotification(timeLeftInMillis)
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateNotification(timeLeftInMillis)
                sendBroadcast(Intent(TIMER_UPDATE_ACTION).putExtra(EXTRA_TIME_LEFT, timeLeftInMillis))
            }

            override fun onFinish() {
                isRunning = false
                sendBroadcast(Intent(TIMER_UPDATE_ACTION).putExtra(EXTRA_TIME_LEFT, 0L))
                handleNextCycle() // Move to next cycle (either work or break phase)
            }
        }.start()
    }

    private fun updateNotification(timeLeft: Long) {
        val notification = buildNotification(timeLeft)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun buildNotification(timeLeft: Long): Notification {
        val timeText = String.format("%02d:%02d", timeLeft / 60000, (timeLeft / 1000) % 60)

        // Display cycle information, e.g., "Cycle 1 of 2"
        val cycleInfo = "Cycle $currentCycle of $totalCycles"

        val pauseOrResumeIntent = Intent(this, TimerService::class.java).apply {
            action = if (isRunning) ACTION_PAUSE else ACTION_CONTINUE
        }
        val cancelIntent = Intent(this, TimerService::class.java).apply {
            action = ACTION_STOP
        }

        val activityIntent = Intent(this, PomodoroActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val activityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseOrResumePendingIntent = PendingIntent.getService(
            this,
            0,
            pauseOrResumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val cancelPendingIntent = PendingIntent.getService(
            this,
            1,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseOrResumeAction = if (isRunning) "Pause" else "Resume"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(if (isBreakTime) "Break Time" else "Work Time")
            .setContentText("Time Left: $timeText\n$cycleInfo")  // Cycle info added here
            .setSmallIcon(R.drawable.timer)
            .setContentIntent(activityPendingIntent)
            .addAction(0, pauseOrResumeAction, pauseOrResumePendingIntent)
            .addAction(0, "Cancel", cancelPendingIntent)
            .setOngoing(isRunning)
            .build()
    }

    private fun showCompletionNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro Process Complete")
            .setContentText("Congratulations! You've completed your Pomodoro cycles.")
            .setSmallIcon(R.drawable.timer)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
