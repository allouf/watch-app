package com.incentives.prototype.minigameT1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.incentives.prototype.R

/**
 * A simple [Fragment] subclass.
 * Use the [MinigameT1S2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MinigameT1S2Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myActivity:MinigameT1= activity as MinigameT1
        val view:View = inflater.inflate(R.layout.fragment_minigame_t1_s2, container, false)
        val completedScoreText:TextView=view.findViewById(R.id.txtViewRemComplScore)
        completedScoreText.text=(MyTimer.getReminderStat(true).toString())
        val remainingScoreText:TextView=view.findViewById(R.id.txtViewRemRemainScore)
        remainingScoreText.text=(MyTimer.getReminderStat(false).toString())
        val btnToT1S3: ImageButton = view.findViewById(R.id.btnToT1S3)
        btnToT1S3.setOnClickListener(View.OnClickListener {
            findNavController().navigate(MinigameT1S2FragmentDirections.actionFragmentMinigameT1S2ToMinigameT1S3Fragment2())

        })
        return view
    }

}