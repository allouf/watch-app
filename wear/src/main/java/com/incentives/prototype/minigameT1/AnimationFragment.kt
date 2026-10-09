package com.incentives.prototype.minigameT1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.navigation.fragment.NavHostFragment
import com.incentives.prototype.R

class AnimationFragment : Fragment() {

    private lateinit var ticAnimationView: LottieAnimationView

    private lateinit var minigameT1Activity: MinigameT1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_animation, container, false)

        minigameT1Activity = activity as MinigameT1

        ticAnimationView = view.findViewById(R.id.ticAnimationView)
        if(minigameT1Activity.typeAI == 0){
            ticAnimationView.setAnimation("done.json")
        }
        else if(minigameT1Activity.typeAI == 1){
            ticAnimationView.setAnimation("question.json")
        }
        else if(minigameT1Activity.typeAI == 2){
            ticAnimationView.setAnimation("done.json")
        }
        else if(minigameT1Activity.typeAI == 3){
            ticAnimationView.setAnimation("done.json")
        }
        ticAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animation?.removeAllListeners()
                //removeFragment()
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    if(minigameT1Activity.typeAI == 0){
                        currentNavHost.navController.navigate(R.id.pagerHint)
                    }
                    else if(minigameT1Activity.typeAI == 1){
                        currentNavHost.navController.navigate(R.id.questionInput)
                    }
                    else if(minigameT1Activity.typeAI == 2){
                        currentNavHost.navController.navigate(R.id.questionInput)
                    }
                    else if(minigameT1Activity.typeAI == 3){
                        currentNavHost.navController.navigate(R.id.questionInput)
                    }
                }
            }
        })

        return view
    }
}