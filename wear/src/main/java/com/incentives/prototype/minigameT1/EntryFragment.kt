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
 * Use the [EntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EntryFragment : Fragment() {
    lateinit var minigameT1Activity:MinigameT1

    public fun CoroutineScope.sendEntryIntermediateToMobile() {
        val datapath = "/allouf_path"
        launch(Dispatchers.IO) {
            SendMessage(
                datapath, "EntryIntermediate&&"+MyTimer.nodeId, requireContext()).start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        minigameT1Activity = activity as MinigameT1
        MyTimer.ChangeCurrentState(EnumStateType.ENTRY_WAIT.ordinal)
        GlobalScope.sendEntryIntermediateToMobile()
        /*minigameT1Activity.runOnUiThread(Runnable {
            val datapath = "/allouf_path"
            SendMessage(
                datapath,
                "EntryIntermediate&&"+MyTimer.nodeId,
                minigameT1Activity.applicationContext
            ).start()
        })*/

        // Inflate the layout for this fragment
        val view:View =  inflater.inflate(R.layout.fragment_entry, container, false)

        return view
    }
}