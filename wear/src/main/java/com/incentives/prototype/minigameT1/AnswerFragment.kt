package com.incentives.prototype.minigameT1

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
import java.util.ArrayList

import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class AnswerFragment : Fragment() {
    lateinit var items1: ArrayList<ItemsListMenu?>
    lateinit var items2: ArrayList<ItemsListMenu?>

    private lateinit var arrowBtn: ImageButton

    lateinit var minigameT1Activity:MinigameT1

    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        minigameT1Activity = activity as MinigameT1
        MyTimer.ChangeCurrentState(EnumStateType.ANSWER_MENU.ordinal)
        val view:View =  inflater.inflate(R.layout.fragment_minigame_t1_answer, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        observeSpokenQuery()

        items1 = ArrayList()
        val recyclerView1: RecyclerView = view.findViewById(R.id.recycler_view1)
        val layoutManager1 = LinearLayoutManager(minigameT1Activity)
        layoutManager1.orientation = WearableLinearLayoutManager.VERTICAL
        recyclerView1.layoutManager = layoutManager1

        items1.add(ItemsListMenu(R.drawable.plus1, "Thought", R.drawable.idea1))
        items1.add(ItemsListMenu(R.drawable.plus1, "Emotion", R.drawable.platozas))
        items1.add(ItemsListMenu(R.drawable.plus1, "Imagination", R.drawable.idea2))
        items1.add(ItemsListMenu(R.drawable.plus1, "Action", R.drawable.plato1))
        items1.add(ItemsListMenu(R.drawable.plus1, "Belief", R.drawable.whatza1))

        val listAdapter1 = AnswerAdapter(
            items1
        ) { v, itemPosition ->
            when (itemPosition) {

                0 -> {
                    MyTimer.selectedType = "Thought"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
                1 -> {
                    MyTimer.selectedType = "Emotion"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                    //startSpeechRecognition()

                }
                2 -> {
                    MyTimer.selectedType = "Imagination"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)

                }
                3 -> {
                    MyTimer.selectedType = "Action"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)

                }
                4 -> {
                    MyTimer.selectedType = "Belief"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
            }
        }

        recyclerView1.adapter = listAdapter1

        items2 = ArrayList()
        val recyclerView2: RecyclerView = view.findViewById(R.id.recycler_view2)
        val layoutManager2 = LinearLayoutManager(minigameT1Activity)
        layoutManager2.orientation = WearableLinearLayoutManager.VERTICAL
        recyclerView2.layoutManager = layoutManager2

        items2.add(ItemsListMenu(R.drawable.plus1, "Dreams", R.drawable.memories))
        items2.add(ItemsListMenu(R.drawable.plus1, "Goals", R.drawable.target))
        items2.add(ItemsListMenu(R.drawable.plus1, "Rules", R.drawable.rules))
        items2.add(ItemsListMenu(R.drawable.plus1, "Memories", R.drawable.dreaming))
        items2.add(ItemsListMenu(R.drawable.plus1, "Questions", R.drawable.question))
        items2.add(ItemsListMenu(R.drawable.plus1, "Expressions", R.drawable.expression))
        items2.add(ItemsListMenu(R.drawable.plus1, "Instructions", R.drawable.instructions))



        val listAdapter2 = AnswerAdapter(
            items2
        ) { v, itemPosition ->
            when (itemPosition) {
                0 -> {
                    MyTimer.selectedType = "Dreams"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
                1 -> {
                    MyTimer.selectedType = "Goals"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
                2 -> {
                    MyTimer.selectedType = "Rules"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
                3 -> {
                    MyTimer.selectedType = "Memories"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
                4 -> {
                    MyTimer.selectedType = "Questions"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
                5 -> {
                    MyTimer.selectedType = "Expressions"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)

                }
                6 -> {
                    MyTimer.selectedType = "Instructions"
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    findNavController().navigate(R.id.action_showRecordingFragment)
                }
            }
        }

        recyclerView2.adapter = listAdapter2

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
                GlobalScope.sendLabelMobile(query)
            }
        })
    }

    companion object {
        private const val SPEECH_RECOGNITION_REQUEST_CODE = 123
    }

    private fun startSpeechRecognition() {
        val speechRecognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        startActivityForResult(speechRecognitionIntent,
            SPEECH_RECOGNITION_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { speechResults ->
                val spokenQuery = speechResults[0]
                processSpokenQuery(spokenQuery)
            }
        }
    }

    private fun processSpokenQuery(spokenQuery: String) {
        GlobalScope.sendLabelMobile(spokenQuery)
    }

    public fun CoroutineScope.sendLabelMobile(spokenQuery: String) {
        val datapath = "/allouf_path"
        val message = "AnswerNote&&"+MyTimer.selectedType+"&&" +
                MyTimer.getLastRemContent(MyTimer.lastRemScreenId  ) +"&&"+spokenQuery
        launch(Dispatchers.IO) {
            SendMessage(datapath, message, minigameT1Activity).start()
            Log.d("Msg", message)
        }
    }
}



