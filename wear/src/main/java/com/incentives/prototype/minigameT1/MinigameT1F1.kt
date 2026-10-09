package com.incentives.prototype.minigameT1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.incentives.prototype.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MinigameT1F1 : Fragment() {
    //val args: MinigameT1F1Args by navArgs()
    public fun CoroutineScope.sendPlayRemToMobile() {
        val datapath = "/allouf_path"
        launch(Dispatchers.IO) {
            SendMessage(
                datapath, "PlayRem", requireContext()).start()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MyTimer.ChangeCurrentState(EnumStateType.SEC_TMR_RNG_SHWNG_FDBCK.ordinal)
        //MyTimer.StartSecondaryTimer(resources.getInteger(R.integer.delaySecTimeToShowFeedback))
        val arg = requireArguments().getInt("FeedbackType",0)
        val feedBackFrag:Int = Utilities.GetListOfFeedbackRelatedLayouts(arg)
        val view:View = inflater.inflate(feedBackFrag, container, false)
        var btnNext: Button? = null

        /*when(feedBackFrag) {
            R.layout.fragment_minigame_t1_f1 -> {
                btnYes = view.findViewById(R.id.btnNext)
            }
            R.layout.fragment_minigame_t1_f2 -> {
                btnYes = view.findViewById(R.id.btnNext)
            }
            R.layout.fragment_minigame_t1_f3 -> {
                btnYes = view.findViewById(R.id.btnNext)
            }
            R.layout.fragment_minigame_t1_f4 -> {
                btnYes = view.findViewById(R.id.btnNext)
            }
        }*/

        btnNext = view.findViewById(R.id.btnNext)
        btnNext.setOnClickListener(View.OnClickListener {
            if(MyTimer.reminderTimes.isAllReminderDone() && MyTimer.remainingSecondsPrimary <=0)
                (context as MinigameT1).supportFragmentManager.fragments.last().findNavController().navigate(R.id.minigameT1Score)
            else{
                val nodesCount = MyTimer.selectedSyncNodesIds.size
                if(MyTimer.selectedSyncNodesIds.contains(MyTimer.nodeId) && nodesCount > 1){
                    (context as MinigameT1).supportFragmentManager.fragments.last().findNavController().navigate(R.id.exitFragment)
                }
                else{
                    MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)//since S5 does not resume the timer automaticaly it is needed.
                    GlobalScope.sendPlayRemToMobile()
                    /*activity?.runOnUiThread(Runnable {
                        val datapath = "/allouf_path"
                        SendMessage(datapath, "PlayRem", this.requireActivity().applicationContext).start() })*/
                    (context as MinigameT1).supportFragmentManager.fragments.last().findNavController().navigate(R.id.minigameT1S5Fragment)
                }
                /*if (MyTimer.isStandalone){
                    MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)//since S5 does not resume the timer automaticaly it is needed.
                    activity?.runOnUiThread(Runnable {
                        val datapath = "/allouf_path"
                        SendMessage(datapath, "PlayRem", this.requireActivity().applicationContext).start() })
                    (context as MinigameT1).supportFragmentManager.fragments.last().findNavController().navigate(R.id.minigameT1S5Fragment)
                }
                else
                    (context as MinigameT1).supportFragmentManager.fragments.last().findNavController().navigate(R.id.exitFragment)*/
            }
        })

        /*val view:View = inflater.inflate(R.layout.view_pager_main, container, false)
        if (this.requireActivity().supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            this.requireActivity().supportFragmentManager.beginTransaction()
                .add(
                    android.R.id.content,
                    PagerFragment()
                ).commit()
        }*/
        return view
    }
}