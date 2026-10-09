package com.incentives.prototype.minigameT1

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.incentives.prototype.CustomScrollView
import com.incentives.prototype.R // Replace this with your package name

class HintFragment : Fragment() {

    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var hint: TextView
    private lateinit var title: TextView
    private lateinit var currentNavHost: NavHostFragment
    private lateinit var minigameT1Activity: MinigameT1
    private lateinit var hintImg: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_minigame_t1_hint, container, false)

        minigameT1Activity = activity as MinigameT1
        currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment

        hint = view.findViewById(R.id.hint_data)
        hintImg = view.findViewById(R.id.title_image)
        title = view.findViewById(R.id.title)
        lottieAnimationView = view.findViewById(R.id.animationView_loading)
        lottieAnimationView.setSpeed(1.0f)
        lottieAnimationView.visibility = View.GONE
        hint.visibility = View.GONE

        if(minigameT1Activity.typeAI == 0){
            title.visibility = View.VISIBLE
            hintImg.visibility = View.GONE
            title.setText("Hint:")
            if(MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint =="") {
                MyTimer.generateResponse(
                    "generate a hint for this: " +
                            MyTimer.getLastRemContent(MyTimer.lastRemScreenId ) + ", without Hint: at the beggining of answer."
                    //+rand
                    ,
                    MyTimer.pagesSysMsg, minigameT1Activity, "page"
                )
                someFunction()
            }
            else{
                hint.visibility = View.VISIBLE
                hint.text = MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint
            }
        }
        else if(minigameT1Activity.typeAI == 1){
            title.visibility = View.GONE
            hintImg.visibility = View.VISIBLE
            //title.setText("Questions:")
            if(MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint =="") {
                MyTimer.generateResponse(
                    "generate " + minigameT1Activity.questionsCount +" questions about this: " +
                            MyTimer.getLastRemContent(MyTimer.lastRemScreenId )
                            //+", as an ordered list."
                    //+ ", without question: at the beggining of answer."
                    //+rand
                    ,
                    MyTimer.questionsSysMsg, minigameT1Activity, "page"
                )
                someFunction()
            }
            else{
                hint.visibility = View.VISIBLE
                hint.text = MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint
            }
        }
        else if(minigameT1Activity.typeAI == 2){
            title.visibility = View.VISIBLE
            hintImg.visibility = View.GONE
            title.setText("Instructions:")
            if(MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint =="") {
                MyTimer.generateResponse(
                    "generate " + minigameT1Activity.questionsCount +" instructions about this: " +
                            MyTimer.getLastRemContent(MyTimer.lastRemScreenId)
                            //+", as an ordered list."
                    //+ ", without question: at the beggining of answer."
                    //+rand
                    ,
                    MyTimer.questionsSysMsg, minigameT1Activity, "page"
                )
                someFunction()
            }
            else{
                hint.visibility = View.VISIBLE
                hint.text = MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint
            }
        }
        else if(minigameT1Activity.typeAI == 3){
            title.visibility = View.VISIBLE
            hintImg.visibility = View.GONE
            title.setText("Tips:")
            if(MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint =="") {
                MyTimer.generateResponse(
                    "generate " + minigameT1Activity.questionsCount +" tips about this: " +
                            MyTimer.getLastRemContent(MyTimer.lastRemScreenId )
                            //+", as an ordered list."
                    //+ ", without question: at the beggining of answer."
                    //+rand
                    ,
                    MyTimer.questionsSysMsg, minigameT1Activity, "page"
                )
                someFunction()
            }
            else{
                hint.visibility = View.VISIBLE
                hint.text = MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint
            }
        }
        else if(minigameT1Activity.typeAI == 4){
            title.visibility = View.VISIBLE
            hintImg.visibility = View.GONE
            title.setText("S.A.M.")
            if(MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint =="") {
                MyTimer.generateResponse(
                    "generate a hint for this: " +
                            MyTimer.getLastRemContent(MyTimer.lastRemScreenId ) + ", " + minigameT1Activity.recordedString
                    //+rand
                    ,
                    MyTimer.pagesSysMsg, minigameT1Activity, "page"
                )
                someFunction()
            }
            else{
                hint.visibility = View.VISIBLE
                hint.text = MyTimer.currentDisplayScreenStates[MyTimer.lastRemScreenId].oldHint
            }
        }

        view.findViewById<ImageButton>(R.id.toPager).setOnClickListener {
            onButtonPagerClick()
        }
        view.findViewById<ImageButton>(R.id.regenerate).setOnClickListener {
            onButtonRegenClick()
        }

        return view
    }

    private fun someFunction() {
        hint.visibility = View.GONE
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.repeatCount = LottieDrawable.INFINITE
        lottieAnimationView.playAnimation()

        val checkFunctionRunnable = object : Runnable {
            override fun run() {
                lottieAnimationView.playAnimation()
                if (MyTimer.hintReady) {
                    lottieAnimationView.cancelAnimation()
                    lottieAnimationView.visibility = View.GONE
                    MyTimer.hintReady = false
                    hint.text = MyTimer.pageAnswer
                    MyTimer.updateNoteFileStringState(MyTimer.lastRemScreenId,MyTimer.pageAnswer)
                    hint.visibility = View.VISIBLE
                } else {
                    hint.visibility = View.GONE
                    Handler(Looper.getMainLooper()).postDelayed(this, 3000)
                }
            }
        }
        Handler(Looper.getMainLooper()).post(checkFunctionRunnable)
    }

    private fun onButtonPagerClick() {

        if(currentNavHost != null) {
            currentNavHost.navController.navigate(R.id.pagerFragment)
        }
    }

    private fun onButtonRegenClick() {
        someFunction()
        hint.text = ""
        if(minigameT1Activity.typeAI == 0){
            MyTimer.generateResponse("generate a hint for this: " +
                    MyTimer.getLastRemContent(MyTimer.lastRemScreenId)+ ", without Hint: at the beggining of answer."
                //+rand
                ,
                MyTimer.pagesSysMsg,minigameT1Activity,"page")
        }
        else if(minigameT1Activity.typeAI == 1){
            MyTimer.generateResponse(
                "generate " + minigameT1Activity.questionsCount +" questions about this: " +
                        MyTimer.getLastRemContent(MyTimer.lastRemScreenId)
                        //+", as an ordered list."
                //+ ", without question: at the beggining of answer."
                //+rand
                ,
                MyTimer.questionsSysMsg, minigameT1Activity, "page"
            )
        }
        else if(minigameT1Activity.typeAI == 2){
            MyTimer.generateResponse(
                "generate " + minigameT1Activity.questionsCount +" instructions about this: " +
                        MyTimer.getLastRemContent(MyTimer.lastRemScreenId )
                        //+", as an ordered list."
                //+ ", without question: at the beggining of answer."
                //+rand
                ,
                MyTimer.questionsSysMsg, minigameT1Activity, "page"
            )
        }
        else if(minigameT1Activity.typeAI == 3){
            MyTimer.generateResponse(
                "generate " + minigameT1Activity.questionsCount +" tips about this: " +
                        MyTimer.getLastRemContent(MyTimer.lastRemScreenId )
                        //+", as an ordered list."
                //+ ", without question: at the beggining of answer."
                //+rand
                ,
                MyTimer.questionsSysMsg, minigameT1Activity, "page"
            )
        }
        else if(minigameT1Activity.typeAI == 4){
            MyTimer.generateResponse(
                "generate a hint for this: " +
                        MyTimer.getLastRemContent(MyTimer.lastRemScreenId ) + ", " + minigameT1Activity.recordedString
                //+rand
                ,
                MyTimer.pagesSysMsg, minigameT1Activity, "page"
            )
        }
        //minigameT1Activity.isHisnt = true
    }
}