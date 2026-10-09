package com.incentives.prototype

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.incentives.prototype.minigameT1.MyTimer
import kotlinx.coroutines.*
import java.lang.Runnable

class AnimationDialog : DialogFragment() {

    var onDialogDismissed: (() -> Unit)? = null

    private lateinit var lottie1: LottieAnimationView
    private lateinit var lottie2: LottieAnimationView
    private lateinit var loadingTextView: TextView
    private lateinit var lottie3: LottieAnimationView

    var dismissalRequested = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(savedInstanceState!= null) { dismissalRequested = savedInstanceState.getBoolean("alreadyShown",false) }
        val customAnimationView = inflater.inflate(R.layout.transfer_algo_animation, container, false)

        lottie1 = customAnimationView.findViewById(R.id.lottie1)
        lottie2 = customAnimationView.findViewById(R.id.lottie2)
        loadingTextView = customAnimationView.findViewById(R.id.loadingTextView)
        lottie3 = customAnimationView.findViewById(R.id.lottie3)

        // Initialize animations
        lottie1.setAnimation("loading-circle.json")
        lottie2.setAnimation("gaming-controller.json")
        lottie3.setAnimation("successfully-done.json")

        dialog?.window?.setBackgroundDrawableResource(android.R.color.black)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        startAnimationsSequence()

        return customAnimationView
    }

    private fun startAnimationsSequence() {
        // Start the first two animations immediately
        lottie1.visibility = View.VISIBLE
        lottie2.visibility = View.VISIBLE
        lottie3.visibility = View.GONE // Hide the third animation initially

        lottie1.playAnimation()
        lottie2.playAnimation()
        loadingTextView.text = "Loading"

        // Schedule the change to the third animation after 7 seconds
        lottie1.postDelayed({
            // Hide the first two animations and show the third
            lottie1.visibility = View.GONE
            lottie2.visibility = View.GONE
            lottie3.visibility = View.VISIBLE

            lottie3.playAnimation()
            loadingTextView.text = "Completed"

            // Schedule the dialog dismissal and callback invocation after the third animation completes
            // Assuming the third animation lasts for 3 seconds
            lottie3.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // Not needed for this example
                }

                override fun onAnimationEnd(animation: Animator) {
                    dismissAllowingStateLoss()
                    onDialogDismissed?.invoke()
                }

                override fun onAnimationCancel(animation: Animator) {
                    // Not needed for this example
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // Not needed for this example
                }
            })
        }, 7000) // 7 seconds delay for the first two animations
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("alreadyShown", dismissalRequested)
    }

    private fun playAnimations() {
        // Reset visibility and animations for loading
        lottie1.visibility = View.VISIBLE
        lottie2.visibility = View.VISIBLE
        lottie3.visibility = View.GONE // Initially hide lottie3 until it's time to show it

        lottie1.playAnimation()
        lottie2.playAnimation()

        // Here, decide when to start lottie3, for example, after a delay or immediately
        // For demonstration, let's start it immediately for now
        playLottie3()
    }

    private fun playLottie3() {
        // Show success animation
        lottie1.visibility = View.GONE
        lottie2.visibility = View.GONE
        lottie3.visibility = View.VISIBLE
        lottie3.playAnimation()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        this.dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun handlerAnimation() {
        val loadingPhase = arrayOf("Loading", "Loading.", "Loading..", "Loading...")

        val loadingHandler = Handler()
        var phaseIndex = 0

        val loadingRunnable = object : Runnable {
            override fun run() {
                phaseIndex = (phaseIndex + 1) % loadingPhase.size
                loadingTextView.text = loadingPhase[phaseIndex]
                loadingHandler.postDelayed(this, 500)
            }
        }

        loadingHandler.post(loadingRunnable)

    }
}
