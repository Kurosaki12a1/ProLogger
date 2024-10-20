package com.kuro.prologger.util

import android.content.Context
import android.graphics.PointF
import kotlin.math.pow
import kotlin.math.sqrt

object Utils {
    var sizeFloatingView = Pair(0f, 0f)
        private set
    var sizeCancelView = Pair(0f, 0f)
        private set

    fun dpToPx(context: Context, dp: Int): Float {
        val density = context.resources.displayMetrics.density
        return dp * density
    }

    fun setUpSize(context: Context) {
        sizeFloatingView = Pair(dpToPx(context, 56), dpToPx(context, 56))
        sizeCancelView = Pair(dpToPx(context, 60), dpToPx(context, 60))
    }

    fun arePointsCloseEnough(p1: PointF, p2: PointF, threshold: Float = 10f): Boolean {
        val distance = sqrt((p2.x - p1.x).pow(2) + (p2.y - p1.y).pow(2))
        return distance <= threshold
    }
}