package com.kuro.prologger.framework

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.kuro.prologger.MainActivity
import com.kuro.prologger.R
import com.kuro.prologger.presentation.floating_view.FloatingView

class LogService : LifecycleService(), SavedStateRegistryOwner {

    companion object {
        private const val TAG = "LogService"
        private const val NOTIFICATION_CHANNEL_ID = "pro_logger_channel"
        private const val NOTIFICATION_NAME = "Pro Logger Service Channel"
        private const val NOTIFICATION_ID = 2307
    }

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView

    private var shouldShowLog: MutableState<Boolean> = mutableStateOf(false)
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        startForeground()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@LogService)
            setViewTreeSavedStateRegistryOwner(this@LogService)
            setContent {
                val value by shouldShowLog
                FloatingView(
                    shouldShowLog = value
                )
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        composeView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Store initial position of the view and touch
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = motionEvent.rawX
                    initialTouchY = motionEvent.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    // Calculate and update view's new position
                    val deltaX = motionEvent.rawX - initialTouchX
                    val deltaY = motionEvent.rawY - initialTouchY
                    params.x = initialX + deltaX.toInt()
                    params.y = initialY + deltaY.toInt()
                    windowManager.updateViewLayout(composeView, params)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    shouldShowLog.value = !shouldShowLog.value
                    true
                }

                else -> false
            }
        }

        // Add ComposeView to WindowManager
        windowManager.addView(composeView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        windowManager.removeView(composeView)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun startForeground() {
        Log.d(TAG, "Start Foreground")
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        Log.d(TAG, "Create Notification")
        // Create a pending intent that opens the main activity when clicked
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_name))
            .setContentText(getString(R.string.notification_content))
            .setSmallIcon(R.drawable.ic_notification_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(getColor(R.color.app_color))
            .build()
    }
}