package com.incentives.prototype.minigameT1

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
class FullScreenCircularProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var _progress = 0f // Private progress variable as float
    var progress: Float
        get() = _progress
        set(value) {
            _progress = value.coerceIn(0f, maxProgress)
            invalidate() // Redraw the view when progress changes
        }

    var maxProgress = 180f // Default maximum progress

    private val paint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 20f // Thickness of the progress circle
        isAntiAlias = true
    }
    private val rect = RectF()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // Calculate the sweep angle based on current progress and maxProgress
        val sweepAngle = 360f * (_progress / maxProgress)
        // Define the circle to fit the screen
        val padding = paint.strokeWidth / 2
        rect.set(padding, padding, width - padding, height - padding)
        // Draw the arc representing the progress
        canvas?.drawArc(rect, -90f, sweepAngle, false, paint)
    }

    fun animateProgress(toProgress: Float, duration: Long = 500) {
        ObjectAnimator.ofFloat(this, "progress", progress, toProgress).apply {
            this.duration = duration
            start()
        }
    }
}