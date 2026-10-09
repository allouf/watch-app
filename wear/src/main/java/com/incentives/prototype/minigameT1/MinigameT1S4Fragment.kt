package com.incentives.prototype.minigameT1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.incentives.prototype.R

/**
 * A simple [Fragment] subclass.
 * Use the [MinigameT1S4Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MinigameT1S4Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_minigame_t1_s4, container, false)
        val btnToT1S5: ImageButton = view.findViewById(R.id.btnToT1S5)
        btnToT1S5.setOnClickListener(View.OnClickListener {
                findNavController().navigate(MinigameT1S4FragmentDirections.actionFragmentMinigameT1S4ToMinigameT1S5Fragment())
        })
        return view
    }
}