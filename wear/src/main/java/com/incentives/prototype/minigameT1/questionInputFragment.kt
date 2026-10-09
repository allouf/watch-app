package com.incentives.prototype.minigameT1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.incentives.prototype.R

class questionInputFragment : Fragment() {
    private lateinit var sliderValueTextView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var textView: TextView

    lateinit var minigameT1Activity:MinigameT1
    private  var value: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        minigameT1Activity = activity as MinigameT1
        return inflater.inflate(R.layout.fragment_question_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sliderValueTextView = view.findViewById(R.id.sliderValueTextView)
        textView = view.findViewById(R.id.textView)
        seekBar = view.findViewById(R.id.seekBar)

        if(minigameT1Activity.typeAI == 1){
            textView.text = "How many questions?"
        }
        else if(minigameT1Activity.typeAI == 2){
            textView.text = "How many instructions?"
        }
        else if(minigameT1Activity.typeAI == 3){
            textView.text = "How many tips?"
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sliderValueTextView.text = progress.toString()
                value = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val doneButton = view.findViewById<Button>(R.id.doneButton)
        doneButton.setOnClickListener {
            minigameT1Activity.questionsCount = value
            val currentNavHost =
                minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if (currentNavHost != null) {
                currentNavHost.navController.navigate(R.id.pagerHint)

            }
        }
    }
}