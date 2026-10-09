package com.incentives.prototype.minigameT1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import com.incentives.prototype.*
import com.incentives.prototype.minigameT1.MyTimer.Companion.GtotalCompleted
import com.incentives.prototype.minigameT1.MyTimer.Companion.GtotalRun
import com.incentives.prototype.minigameT1.MyTimer.Companion.GtotalStopped
import com.incentives.prototype.minigameT1.MyTimer.Companion.getLastRemContent
import com.incentives.prototype.minigameT1.MyTimer.Companion.totalAlgos
import kotlinx.coroutines.GlobalScope
import java.util.ArrayList

class GenerateFragment : Fragment() {
    lateinit var items: ArrayList<ItemsListImagTxt?>

    lateinit var minigameT1Activity:MinigameT1
    private lateinit var arrowBtn: ImageButton

    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        minigameT1Activity = activity as MinigameT1
        MyTimer.ChangeCurrentState(EnumStateType.GENERATE.ordinal)


        val view:View =  inflater.inflate(R.layout.fragment_minigame_t1_generate, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        observeSpokenQuery()

        items = ArrayList()
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(minigameT1Activity)
        layoutManager.orientation = WearableLinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        items.add(ItemsListImagTxt(R.drawable.idea, "Hint"))
        items.add(ItemsListImagTxt(R.drawable.question, "Questions"))
        items.add(ItemsListImagTxt(R.drawable.instructions, "Instructions"))
        items.add(ItemsListImagTxt(R.drawable.tips, "Tips"))
        items.add(ItemsListImagTxt(R.drawable.comments, "System Message"))

        val listAdapter = GenerateAdapter(
            items
        ) { v, itemPosition ->
            if(itemPosition==2){
                MyTimer.updateNoteFileStringState(MyTimer.lastRemScreenId,"")
                minigameT1Activity.typeAI = 2
                MyTimer.updateNoteFileBooleanState(MyTimer.lastRemScreenId, true)
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    currentNavHost.navController.navigate(R.id.ticAnimation)
                }
            }
            else if(itemPosition==1){
                MyTimer.updateNoteFileStringState(MyTimer.lastRemScreenId,"")
                minigameT1Activity.typeAI = 1

                MyTimer.updateNoteFileBooleanState(MyTimer.lastRemScreenId, true)
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    currentNavHost.navController.navigate(R.id.ticAnimation)
                }
                /*val animationDialog = AnimationDialog()
                animationDialog.show(parentFragmentManager,"AnimationDialog")*/
            }
            else if(itemPosition==0){
                MyTimer.updateNoteFileStringState(MyTimer.lastRemScreenId,"")
                minigameT1Activity.typeAI = 0
                MyTimer.updateNoteFileBooleanState(MyTimer.lastRemScreenId, true)
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    currentNavHost.navController.navigate(R.id.ticAnimation)
                }
            }
            else if(itemPosition==3){
                MyTimer.updateNoteFileStringState(MyTimer.lastRemScreenId,"")
                minigameT1Activity.typeAI = 3
                MyTimer.updateNoteFileBooleanState(MyTimer.lastRemScreenId, true)
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    currentNavHost.navController.navigate(R.id.ticAnimation)
                }
            }
            else if(itemPosition==4){
                MyTimer.updateNoteFileStringState(MyTimer.lastRemScreenId,"")
                minigameT1Activity.typeAI = 4
                MyTimer.updateNoteFileBooleanState(MyTimer.lastRemScreenId, true)
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
            }
        }

        recyclerView.adapter = listAdapter

        arrowBtn = view.findViewById(R.id.backBtn)

        arrowBtn.setOnClickListener {
            val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null) {
                currentNavHost.navController.navigate(R.id.pagerMenu)
            }
        }

        return view
    }

    private fun observeSpokenQuery() {
        sharedViewModel.spokenQueryUpdate.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { query ->
                // Perform actions here when the "Done" button is clicked in RecordingFragment
                minigameT1Activity.recordedString = query
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    currentNavHost.navController.navigate(R.id.pagerHint)
                }
            }
        })
    }
}