package com.incentives.prototype;

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ScrollView
import com.incentives.prototype.minigameT1.MinigameT1

class CustomScrollView : ScrollView {

    private var swipeListener: OnCustomScrollSwipeListener? = null

    private var gestureDetector: GestureDetector? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Setup Gesture Detector
     */
    fun setGestureDetector(getstureDetector: GestureDetector) {
        this.gestureDetector = getstureDetector
    }

    fun setOnCustomScrollSwipeListener(listener: OnCustomScrollSwipeListener) {
        this.swipeListener = listener
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            gestureDetector?.onTouchEvent(it)
            this.parent.requestDisallowInterceptTouchEvent(true)
            swipeListener?.onSwipeDetected(this, it)
        }
        return super.onTouchEvent(ev) || gestureDetector?.onTouchEvent(ev!!) == true
    }
}

