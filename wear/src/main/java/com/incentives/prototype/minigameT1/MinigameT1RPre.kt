package com.incentives.prototype.minigameT1

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.incentives.prototype.EnumStateType
import com.incentives.prototype.R
import com.incentives.prototype.SendMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MinigameT1RPre : Fragment() {

    public fun CoroutineScope.sendPauseRemToMobile() {
        val datapath = "/allouf_path"
        launch(Dispatchers.IO) {
            SendMessage(
                datapath, "PauseRem", requireContext()).start()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_R_PRE.ordinal)

        GlobalScope.sendPauseRemToMobile()
        /*activity?.runOnUiThread(Runnable {
            val datapath = "/allouf_path"
            SendMessage(
                datapath,
                "PauseRem",
                this.requireActivity().applicationContext
            ).start()
        })*/
        // send pause to unity

        val view:View =  inflater.inflate(R.layout.fragment_minigame_t1_r_pre_prompt, container, false)

        val btnYes: Button = view.findViewById(R.id.btnYes)
        btnYes.setTextColor(Color.WHITE)
        btnYes.setOnClickListener(View.OnClickListener {
            val nodesCount = MyTimer.selectedSyncNodesIds.size
            if(MyTimer.selectedSyncNodesIds.contains(MyTimer.nodeId) && nodesCount > 1){
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1ENTRY())
            }
            else{
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1RIdle())
            }
            /*if(nodesCount == 0){
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1R1())
            }
            else {
                if(MyTimer.selectedSyncNodesIds.contains(MyTimer.nodeId)){
                    if(nodesCount == 1){
                        findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1R1())
                    }
                    else{
                        findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1ENTRY())
                    }
                }
                else{
                    findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1R1())
                }
            }*/
            /*if(MyTimer.isStandalone)
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1R1())
            else
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1ENTRY())*/
        })

        val btnYesInv: Button = view.findViewById(R.id.btnYesInv)
        btnYesInv.visibility = view.visibility
        btnYesInv.setBackgroundColor(Color.TRANSPARENT)
        btnYesInv.setTextColor(Color.TRANSPARENT)
        btnYesInv.setOnClickListener(View.OnClickListener {
            val nodesCount = MyTimer.selectedSyncNodesIds.size
            if(MyTimer.selectedSyncNodesIds.contains(MyTimer.nodeId) && nodesCount > 1){
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1ENTRY())
            }
            else{
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1RIdle())
            }
            /*if(nodesCount == 0){
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1R1())
            }
            else {
                if(MyTimer.selectedSyncNodesIds.contains(MyTimer.nodeId)){
                    if(nodesCount == 1){
                        findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1R1())
                    }
                    else{
                        findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1ENTRY())
                    }
                }
                else{
                    findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1R1())
                }
            }*/
            /*if(MyTimer.isStandalone)
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1R1())
            else
                findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1ENTRY())*/
        })


        val btnNo: Button = view.findViewById(R.id.btnNo)
        btnNo.setTextColor(Color.WHITE)
        btnNo.setOnClickListener(View.OnClickListener {
            findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1RPreNo())
        })

        val btnNoInv: Button = view.findViewById(R.id.btnNoInv)
        btnNoInv.visibility = view.visibility
        btnNoInv.setBackgroundColor(Color.TRANSPARENT)
        btnNoInv.setTextColor(Color.TRANSPARENT)
        btnNoInv.setOnClickListener(View.OnClickListener {
            findNavController().navigate(MinigameT1RPreDirections.actionMinigameT1RPreToMinigameT1RPreNo())
        })



        return view
    }
}