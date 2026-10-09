package com.incentives.prototype.minigameT1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.incentives.prototype.EnumAlarmTypes
import com.incentives.prototype.EnumStateType
import com.incentives.prototype.R
import render.animations.*;
import render.animations.Attention

import render.animations.Render




/**
 * A simple [Fragment] subclass.
 * Use the [MinigameT1S5Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MinigameT1S5Fragment : Fragment() {
    //private lateinit var animationText: LottieAnimationView
    private lateinit var txtViewRulesExplan: TextView
    private lateinit var txtViewStart: TextView
    lateinit var minigameT1Activity:MinigameT1
    private lateinit var txtViewTimer: TextView
    lateinit var render1:Render
    lateinit var render2:Render
    private val handler = Handler(Looper.getMainLooper())

    private val animateRunnable = object : Runnable {
        override fun run() {
            render1.setAnimation(Fade.Out(txtViewStart))
            render1.setDuration(1000)
            render1.start()
            render2.setAnimation(Attention.Flash(txtViewStart))
            render2.setDuration(1000)
            render2.start()
            handler.postDelayed(this, 2000)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_minigame_t1_s5, container, false)

        minigameT1Activity = activity as MinigameT1
        render1 = Render(minigameT1Activity)
        render2 = Render(minigameT1Activity)
        txtViewRulesExplan =view.findViewById(R.id.txtViewRulesExplan)
        txtViewStart =view.findViewById(R.id.txtViewStart)
        txtViewTimer = view.findViewById(R.id.txtViewTimerS5)

        updateUIComponents()
        txtViewStart.setOnClickListener {
            handler.removeCallbacks(animateRunnable)
            findNavController().navigate(MinigameT1S5FragmentDirections.actionMinigameT1S5FragmentToFragmentMinigameT1S1())
            (activity as MinigameT1).startCodeRequested()
        }
        return view
    }

    fun updateUIComponents() {
        if (MyTimer.currentState != EnumStateType.ALL_INACTIVE.ordinal) {
            txtViewStart.visibility = View.GONE
            txtViewRulesExplan.visibility = View.VISIBLE
            //txtViewTimer.text =
                //MyTimer.ConvertInTimeStr(MyTimer.remainingSecondsPrimary)
        } else {
            txtViewStart.visibility = View.VISIBLE
            //render.setAnimation(Attention.Wobble(txtViewStart))
            handler.post(animateRunnable)


            //animationText.repeatCount = LottieDrawable.INFINITE
            //animationText.playAnimation()
            txtViewRulesExplan.visibility = View.GONE
            txtViewTimer.visibility = View.GONE
        }
    }

    fun changeTimerText(str:String){
        if(txtViewTimer!=null)
            txtViewTimer.text=str
    }

    fun changeState(str:String){
        txtViewRulesExplan.visibility = View.GONE
        txtViewTimer.text = "\n\n\nAll reminders occured\nWell Done!.\n Remainig time to stop Timer is \n"+ str
        txtViewTimer.visibility = View.VISIBLE
        txtViewStart.visibility = View.GONE
    }

/*    private fun pauseTimerAndShowCongratsWithDelay(){
        MyTimer.pauseBothTimers()
        val mainLooperHandler = Handler(Looper.getMainLooper())
        mainLooperHandler.postDelayed({//only go when the current fragment is the self score screen.
            if (activity != null && findNavController().currentDestination?.label==resources.getString(R.string.MinigameT1S5FragmentLabel)) {
                findNavController().navigate(MinigameT1S5FragmentDirections.actionMinigameT1S5FragmentToFragmentMinigameT1S1())
            }
        }, resources.getInteger(R.integer.delayMilSecToFeedbackScreen).toLong())
    }*/
}