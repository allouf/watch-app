package com.incentives.prototype

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class CustomViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    private var startX: Float = 0f
    private var startY: Float = 0f

    private val idleHandler = Handler(Looper.getMainLooper())
    private val showTab = Runnable {
        val parent = this.parent as ViewGroup
        val tab = parent.findViewById<TabLayout>(R.id.tab_layout)
        tab?.also { myTab ->
            myTab.visibility = View.VISIBLE
            val anim = ObjectAnimator.ofFloat(myTab, "translationY", -myTab.height.toFloat(), 0f)
            anim.duration = 300    // specify the duration of the animate here
            anim.start()
        }
    }

    private val hideTab = Runnable {
        val parent = this.parent as ViewGroup
        val tab = parent.findViewById<TabLayout>(R.id.tab_layout)
        tab?.also { myTab ->
            val anim = ObjectAnimator.ofFloat(myTab, "translationY", 0f, -myTab.height.toFloat())
            anim.duration = 300   // specify the duration of the animate here
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    myTab.visibility = View.INVISIBLE  // change from GONE to INVISIBLE
                }
            })
            anim.start()
        }
    }

    private val mDoubleTapDetector: GestureDetector = GestureDetector(context, ExpansionGestureListener())

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x
                val endY = ev.y
                val distanceX = Math.abs(endX - startX)
                val distanceY = Math.abs(endY - startY)
                // detect the horizontal swipe
                if (distanceX > distanceY) {
                    idleHandler.removeCallbacks(hideTab)
                    idleHandler.post(showTab)
                }
            }
            MotionEvent.ACTION_UP -> {
                idleHandler.postDelayed(hideTab, 500)
            }
        }
        mDoubleTapDetector.onTouchEvent(ev)
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
            }
        }
        mDoubleTapDetector.onTouchEvent(ev)

        return super.onInterceptTouchEvent(ev)
    }

    private inner class ExpansionGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Detect double tap inside custom view pager
            // Perform the action you want to implement when a double tap is detected
            performDoubleTapAction()
            return true
            //return super.onDoubleTap(e)
        }
    }

    private fun performDoubleTapAction() {
        // You can call your onDoubleTap method from your activity or fragment containing this CustomViewPager.
        // Or you can use an interface to listen from the activity or fragment as an observer pattern method.

        // E.g. (Sample calling from fragment):
        context?.let {
            if (it is YourFragmentInterface) {
                it.onDoubleTap()
            }
        }
    }
}

interface YourFragmentInterface {
    fun onDoubleTap()
}