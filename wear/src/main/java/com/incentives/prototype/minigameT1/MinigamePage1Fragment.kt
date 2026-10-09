package com.incentives.prototype.minigameT1

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.incentives.prototype.R
import kotlin.random.Random

class MinigamePage1Fragment : Fragment() {

    //private lateinit var imgs: Array<ImageView>
    private lateinit var names: Array<TextView>
    private lateinit var levels: Array<TextView>
    //private lateinit var frameLayouts: Array<FrameLayout>
    private lateinit var progressBars: Array<ProgressBar>
    private lateinit var textsOverProgress: Array<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_minigame_t1s3_page1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        names = Array(5) { i ->
            view.findViewById(
                resources.getIdentifier(
                    "Name${i+1}",
                    "id",
                    context?.packageName ?: ""
                )
            )
        }

        levels = Array(5) { i ->
            view.findViewById(
                resources.getIdentifier(
                    "Level${i+1}",
                    "id",
                    context?.packageName ?: ""
                )
            )
        }

        progressBars = Array(5) { i ->
            view.findViewById(
                resources.getIdentifier(
                    "levelProgressBar${i+1}",
                    "id",
                    context?.packageName ?: ""
                )
            )
        }

        textsOverProgress = Array(5) { i ->
            view.findViewById(
                resources.getIdentifier(
                    "text_over_progress${i+1}",
                    "id",
                    context?.packageName ?: ""
                )
            )
        }

        // Use views here
        // e.g., levels[0].text = "Level 1"
        // names[0].text = "Name of name 1", etc.
        updateData(MyTimer.CurrentProgress)
    }

    private fun updateData(CurrentProgress: List<Int>) {

        // Progress Update
        val assignmentProgressLength = minOf(CurrentProgress.size, progressBars.size)
        for (i in 0 until assignmentProgressLength){
            progressBars[i]?.progress = CurrentProgress[i]
        }


        for (i in 0 until assignmentProgressLength){
            textsOverProgress[i]?.text = CurrentProgress[i].toString()
        }
    }
}
