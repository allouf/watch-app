package com.incentives.prototype.minigameT1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.incentives.prototype.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ExitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExitFragment : Fragment() {

    lateinit var minigameT1Activity:MinigameT1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    public fun CoroutineScope.sendExitIntermediateToMobile() {
        val datapath = "/allouf_path"
        launch(Dispatchers.IO) {
            SendMessage(
                datapath, "ExitIntermediate&&"+MyTimer.nodeId, requireContext()).start()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        minigameT1Activity = activity as MinigameT1
        MyTimer.ChangeCurrentState(EnumStateType.EXIT_WAIT.ordinal)
        GlobalScope.sendExitIntermediateToMobile()
        /*minigameT1Activity.runOnUiThread(Runnable {
            val datapath = "/allouf_path"
            SendMessage(
                datapath,
                "ExitIntermediate&&"+MyTimer.nodeId,
                minigameT1Activity.applicationContext
            ).start()
        })*/

        // Inflate the layout for this fragment
        val view:View =  inflater.inflate(R.layout.fragment_exit, container, false)

        return view
    }
}