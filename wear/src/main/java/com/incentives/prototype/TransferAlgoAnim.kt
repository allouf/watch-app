package com.incentives.prototype

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.incentives.prototype.R

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.airbnb.lottie.LottieDrawable


/**
 * A simple [Fragment] subclass.
 * Use the [TransferAlgoAnim.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransferAlgoAnim : Fragment() {

    private lateinit var lottie1: LottieAnimationView
    private lateinit var lottie2: LottieAnimationView
    private lateinit var loadingTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.transfer_algo_animation, container, false)

        lottie1 = view.findViewById(R.id.lottie1)
        lottie2 = view.findViewById(R.id.lottie2)
        loadingTextView = view.findViewById(R.id.loadingTextView)

        playAnimations()
        handlerAnimation()

        return view
    }

    private fun playAnimations() {
        lottie1.setAnimation("loading-circle.json")
        lottie2.setAnimation("gaming-controller.json")
        lottie1.repeatCount = LottieDrawable.INFINITE
        lottie2.repeatCount = LottieDrawable.INFINITE
        lottie1.playAnimation()
        lottie2.playAnimation()

        Handler().postDelayed({
            lottie1.cancelAnimation()
            lottie2.cancelAnimation()
            lottie1.visibility = View.GONE
            lottie2.visibility = View.GONE
            loadingTextView.visibility = View.GONE
        }, 10000)
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