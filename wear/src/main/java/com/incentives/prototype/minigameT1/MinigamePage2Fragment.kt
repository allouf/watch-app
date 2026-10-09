package com.incentives.prototype.minigameT1

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.incentives.prototype.R

class MinigamePage2Fragment : Fragment() {

    private lateinit var levelProgressBars: Array<ProgressBar>
    private lateinit var textOverProgresss: Array<TextView>
    private lateinit var levels: Array<TextView>
    //private lateinit var flips: Array<ImageView>
    private lateinit var names: Array<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_minigame_t1s3_page2, container, false)


        levelProgressBars = Array(7) { i ->
            view.findViewById(
                resources.getIdentifier(
                    "levelProgressBar${i+1}",
                    "id",
                    activity?.packageName
                )
            )
        }

        textOverProgresss = Array(7) { i ->
            view.findViewById(
                resources.getIdentifier(
                    "text_over_progress${i+1}",
                    "id",
                    activity?.packageName
                )
            )
        }

        levels = Array(7) { i ->
            view.findViewById(
                resources.getIdentifier(
                    "Level${i+1}",
                    "id",
                    activity?.packageName
                )
            )
        }

        /*flips = Array(7) { i ->
            view.findViewById(
                resources.getIdentifier(
                    "flip${i+1}",
                    "id",
                    activity?.packageName
                )
            )
        }*/

        /*names = Array(7) { i ->
            view.findViewById<TextView>(
                resources.getIdentifier(
                    "Name${i+1}",
                    "id",
                    activity?.packageName
                )
            )
        }*/
        return view
    }
}