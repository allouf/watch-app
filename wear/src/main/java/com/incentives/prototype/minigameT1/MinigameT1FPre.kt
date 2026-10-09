package com.incentives.prototype.minigameT1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.incentives.prototype.EnumFeebackType
import com.incentives.prototype.EnumStateType
import com.incentives.prototype.R


class MinigameT1FPre : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_F_PRE.ordinal)
        val view:View =inflater.inflate(R.layout.fragment_minigame_t1_f_pre, container, false)
        val btnYes: Button = view.findViewById(R.id.btnYes)
        btnYes.setOnClickListener(View.OnClickListener {
            MyTimer.updateScore(true,false)
            findNavController().navigate(MinigameT1FPreDirections.actionMinigameT1FPreToMinigameT1F1(EnumFeebackType.FEEDBACK_GOOD.ordinal))
        })
        val btnNo: Button = view.findViewById(R.id.btnNo)
        btnNo.setOnClickListener(View.OnClickListener {
            MyTimer.updateScore(false, true)
            findNavController().navigate(MinigameT1FPreDirections.actionMinigameT1FPreToMinigameT1F1(EnumFeebackType.FEEDBACK_BAD.ordinal))
        })
        return view
    }
}