package com.incentives.prototype.minigameT1

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.incentives.prototype.EnumStateType
import com.incentives.prototype.R

class MinigameT1Score : Fragment() {
    lateinit var minigameT1Activity:MinigameT1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        minigameT1Activity = activity as MinigameT1
        MyTimer.ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
        val view = inflater.inflate(R.layout.fragment_minigame_t1_score, container, false)

        val confettiAnim: LottieAnimationView = view.findViewById(R.id.confettiAnim)
        val careerSuccessAnim: LottieAnimationView = view.findViewById(R.id.careerSuccessAnim)
        confettiAnim.visibility = View.VISIBLE
        confettiAnim.playAnimation()

        confettiAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                // Start career-success.json after confetti animation ends
                careerSuccessAnim.visibility = View.VISIBLE
                careerSuccessAnim.playAnimation()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationStart(animation: Animator) {}
        })


        val txtViewScore:TextView=view.findViewById(R.id.txtViewScore)
        txtViewScore.text=MyTimer.getTotalScore().toString()
        val btnToT1S1: ImageButton = view.findViewById(R.id.btnToT1S1)
        btnToT1S1.setOnClickListener({
            MyTimer.resetScoreAndReminder()
            findNavController().navigate(MinigameT1ScoreDirections.actionMinigameT1ScoreToFragmentMinigameT1S1())
            MyTimer.isTimerStopped = true
            MyTimer.totalCompleted += 1
            MyTimer.GtotalCompleted += 1
            val timer = MyTimer.settings.getInt(minigameT1Activity.getString(R.string.primaryTimerDurString), minigameT1Activity.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1))
            MyTimer.totalTime += timer
            MyTimer.GtotalTime += timer
        })
        return view
    }
}