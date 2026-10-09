package com.incentives.prototype.minigameT1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.incentives.prototype.R
import com.incentives.prototype.EnumStateType

class MinigameT1RPreNo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MyTimer.ChangeCurrentState(EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal)
        //MyTimer.StartSecondaryTimer(resources.getInteger(R.integer.delaySecTimeToAnswerQuest))
        val view:View = inflater.inflate(R.layout.fragment_minigame_t1_r_pre_no, container, false)
        val btnToT1S1: ImageButton = view.findViewById(R.id.btnToT1S1)
        btnToT1S1.setOnClickListener {
            if (MyTimer.currentState == EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal) {
                findNavController().navigate(MinigameT1RPreNoDirections.actionMinigameT1RPreNoToFragmentMinigameT1S1())
                MyTimer.isTimerPaused = true
            }
        }
        return view
    }
}