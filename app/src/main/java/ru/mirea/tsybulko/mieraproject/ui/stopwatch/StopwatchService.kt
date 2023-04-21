package ru.mirea.tsybulko.mieraproject.ui.stopwatch

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.*

class StopwatchService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var stopwatchTimer: Timer
    private lateinit var updateTimer: Timer


    private var isStopWatchRunning = false
    private var timeElapsed = 0


    companion object {
        // Channel ID for notifications
        const val CHANNEL_ID = "Stopwatch_Notifications"

        // Service Actions
        const val START = "START"
        const val PAUSE = "PAUSE"
        const val RESET = "RESET"
        const val GET_STATUS = "GET_STATUS"

        // Intent Extras
        const val STOPWATCH_ACTION = "STOPWATCH_ACTION"
        const val TIME_ELAPSED = "TIME_ELAPSED"
        const val IS_STOPWATCH_RUNNING = "IS_STOPWATCH_RUNNING"

        // Intent Actions
        const val STOPWATCH_TICK = "STOPWATCH_TICK"
        const val STOPWATCH_STATUS = "STOPWATCH_STATUS"
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("Stopwatch", "Stopwatch onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        getNotificationManager()

        val action = intent?.getStringExtra(STOPWATCH_ACTION)!!

        Log.d("Stopwatch", "onStartCommand Action: $action")

        when (action) {
            START -> startStopwatch()
            PAUSE -> pauseStopwatch()
            RESET -> resetStopwatch()
            GET_STATUS -> sendStatus()
        }

        return START_STICKY
    }

    private fun createChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "Stopwatch",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationChannel.setShowBadge(true)
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun sendStatus() {
        sendBroadcast(Intent().apply {
            action = STOPWATCH_STATUS
            putExtra(IS_STOPWATCH_RUNNING, isStopWatchRunning)
            putExtra(TIME_ELAPSED, timeElapsed)
        })
    }

    private fun startStopwatch() {
        isStopWatchRunning = true

        sendStatus()

        stopwatchTimer = Timer()
        stopwatchTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timeElapsed++

                sendBroadcast(Intent().apply {
                    action = STOPWATCH_TICK
                    putExtra(TIME_ELAPSED, timeElapsed)
                })
            }
        }, 0, 1000)
    }

    private fun pauseStopwatch() {
        stopwatchTimer.cancel()
        isStopWatchRunning = false
        sendStatus()
    }

    private fun resetStopwatch() {
        pauseStopwatch()
        timeElapsed = 0
        sendStatus()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun buildNotification(): Notification {
        val title = if (isStopWatchRunning) {
            "Stopwatch is running!"
        } else {
            "Stopwatch is paused!"
        }

        val hours: Int = timeElapsed.div(60).div(60)
        val minutes: Int = timeElapsed.div(60)
        val seconds: Int = timeElapsed.rem(60)

        val intent = Intent(this, StopwatchFragment::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setOngoing(true)
            .setContentText(
                "${"%02d".format(hours)}:${"%02d".format(minutes)}:${
                    "%02d".format(
                        seconds
                    )
                }"
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .build()
    }

    private fun updateNotification() {
        notificationManager.notify(
            1,
            buildNotification()
        )
    }

    private fun moveToForeground() {

        if (isStopWatchRunning) {
            startForeground(1, buildNotification())

            updateTimer = Timer()

            updateTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    updateNotification()
                }
            }, 0, 1000)
        }
    }

    private fun moveToBackground() {
        updateTimer.cancel()
        stopForeground(true)
    }
}