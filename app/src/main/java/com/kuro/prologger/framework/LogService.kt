package com.kuro.prologger.framework

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.compose.runtime.MutableState
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
import com.kuro.prologger.presentation.floating_view.CancerView
import com.kuro.prologger.presentation.floating_view.FloatingView
import com.kuro.prologger.util.Utils
import com.kuro.prologger.util.Utils.arePointsCloseEnough
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class LogService : LifecycleService(), SavedStateRegistryOwner {

    companion object {
        private const val TAG = "LogService"
        private const val NOTIFICATION_CHANNEL_ID = "pro_logger_channel"
        private const val NOTIFICATION_NAME = "Pro Logger Service Channel"
        private const val NOTIFICATION_ID = 2307
    }

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var cancelView: ComposeView

    private var shouldShowDetail: MutableState<Boolean> = mutableStateOf(false)
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var touchStartTime = 0L
    private var isHolding = false
    private var job: Job? = null

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
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@LogService)
            setViewTreeSavedStateRegistryOwner(this@LogService)
            setContent {
                val value by shouldShowDetail
                FloatingView(
                    shouldShowDetail = value,
                    onExitClick = {
                        shouldShowDetail.value = !shouldShowDetail.value
                    }
                )
            }
        }

        cancelView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@LogService)
            setViewTreeSavedStateRegistryOwner(this@LogService)
            setContent { CancerView() }
        }

        val floatingViewParams = createLayoutParams(gravity = Gravity.CENTER)
        val cancelViewParams = createLayoutParams(gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)

        floatingView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Store initial position of the view and touch
                    initialX = floatingViewParams.x
                    initialY = floatingViewParams.y
                    initialTouchX = motionEvent.rawX
                    initialTouchY = motionEvent.rawY
                    touchStartTime = System.currentTimeMillis()
                    startHoldingTime()
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    // Calculate and update view's new position
                    val deltaX = motionEvent.rawX - initialTouchX
                    val deltaY = motionEvent.rawY - initialTouchY
                    floatingViewParams.x = initialX + deltaX.toInt()
                    floatingViewParams.y = initialY + deltaY.toInt()
                    windowManager.updateViewLayout(floatingView, floatingViewParams)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val elapsedTime = System.currentTimeMillis() - touchStartTime
                    val deltaX = abs(motionEvent.rawX - initialTouchX)
                    val deltaY = abs(motionEvent.rawY - initialTouchY)

                    if (elapsedTime < 200 && deltaX < 10 && deltaY < 10) {
                        Log.d(TAG, "OnClick ComposeView")
                        shouldShowDetail.value = !shouldShowDetail.value
                    }
                    checkIfCancel()
                    stopHoldingTime()
                    true
                }

                else -> false
            }
        }

        // Add ComposeView to WindowManager
        windowManager.addView(floatingView, floatingViewParams)
        windowManager.addView(cancelView, cancelViewParams)
        cancelView.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        windowManager.removeView(floatingView)
        windowManager.removeView(cancelView)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun createLayoutParams(gravity: Int? = null): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).also {
            if (gravity != null) {
                it.gravity = gravity
            }
        }
    }

    private fun startHoldingTime() {
        if (shouldShowDetail.value) return
        isHolding = true
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(2000L)
            if (isHolding) {
                showCancelButton()
            }
        }
    }

    private fun stopHoldingTime() {
        isHolding = false
        cancelView.visibility = View.GONE
    }

    private fun showCancelButton() {
        cancelView.visibility = View.VISIBLE
        ValueAnimator.ofInt(0, 300).apply {
            duration = 500
            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                val params = cancelView.layoutParams as WindowManager.LayoutParams
                params.y = value
                windowManager.updateViewLayout(cancelView, params)
            }
            start()
        }
    }

    private fun checkIfCancel() {
        val floatingViewParams = floatingView.layoutParams as WindowManager.LayoutParams
        val cancelViewParams = cancelView.layoutParams as WindowManager.LayoutParams

        val centerFloatingView = PointF(
            floatingViewParams.x + Utils.sizeFloatingView.first / 2f,
            floatingViewParams.y + Utils.sizeFloatingView.second / 2f
        )

        val centerCancelView = PointF(
            cancelViewParams.x + Utils.sizeCancelView.first / 2f,
            cancelViewParams.y + Utils.sizeCancelView.second / 2f
        )

        val isCloseEnough = arePointsCloseEnough(centerFloatingView, centerCancelView)
        Log.d(TAG, "x: ${floatingViewParams.x} y:${floatingViewParams.y}")
        Log.d(TAG, "x: ${centerCancelView.x} y:${centerCancelView.y}")
        if (isCloseEnough) {
            Toast.makeText(this, "Inside?", Toast.LENGTH_SHORT).show()
        }


        /* if (Rect.intersects(floatingViewRect, cancelRect)) {
             Toast.makeText(this, "Float: $floatingViewRect and Cancel: $cancelRect", Toast.LENGTH_LONG).show()
           //  stopSelf()
         }*/
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
            .setSmallIcon(R.drawable.ic_floating_button)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(getColor(R.color.app_color))
            .build()
    }
}