package com.incentives.prototype

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

abstract class OnSwipeTouchListener(ctx: Context) : View.OnTouchListener {

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        // Check if the event is not null before passing it to the GestureDetector
        event?.let {
            return gestureDetector.onTouchEvent(it)
        }
        // Return false if the event is null, indicating that the event was not handled
        return false
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            val swipeThreshold = 100
            val swipeVelocityThreshold = 100

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > swipeThreshold && Math.abs(velocityX) > swipeVelocityThreshold) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    result = true
                }
            } else {
                // Implement up/down swipe logic if needed
            }
            return result
        }

        // These methods call the outer class's corresponding methods
        fun onSwipeRight() {
            this@OnSwipeTouchListener.onSwipeRight()
        }

        fun onSwipeLeft() {
            this@OnSwipeTouchListener.onSwipeLeft()
        }

        override fun onLongPress(e: MotionEvent) {
            this@OnSwipeTouchListener.onLongPress()
        }
    }

    // Mark these methods as open to allow overriding
    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onLongPress() {}
}

interface OnCustomScrollSwipeListener {
    fun onSwipeDetected(view: View, event: MotionEvent)
}
