package com.incentives.prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.incentives.prototype.minigameT1.MinigameT1
import com.incentives.prototype.minigameT1.MyTimer

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_static_1.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_static_1 : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_static_1, container, false)

        //val toggleButton: ToggleButton =view.findViewById(R.id.toggleButton)

        val btnScreen1:Button = view.findViewById(R.id.buttonStatic1)
        btnScreen1.setOnClickListener(View.OnClickListener {
            //**TODO check if persistant memory indicates that a timer is already running. Show info/warning based on that.
            /*if(toggleButton.text.equals("Network")){
                MyTimer.isStandalone = false

            }*/
            /*if(MyTimer.selectedSyncNodesIds.size == 0)
                toggleButton.setText("standalone")
            else if (MyTimer.selectedSyncNodesIds.size == 1)
                toggleButton.setText("only me")
            else
                toggleButton.setText("multi nodes")*/
            val i = Intent(context, MinigameT1::class.java)
            startActivity(i)
            Log.d("MyTag","T1 Activity started")
        })


        return view
    }
}