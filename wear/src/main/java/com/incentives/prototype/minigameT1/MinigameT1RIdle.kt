package com.incentives.prototype.minigameT1

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.incentives.prototype.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.math.min
import kotlin.random.Random


public class MinigameT1RIdle : Fragment() {

    val maxProgressValue = 180f
    private var currentScore: Int= 0
    private var threadRunning = false

    private lateinit var  youwonMsg: TextView
    private lateinit var  scoreInf: TextView
    private lateinit var  currentScoreInf: TextView
    private lateinit var mLayout1: ViewGroup

    private lateinit var imageBtn: ImageButton
    private lateinit var hintAnimation: LottieAnimationView
    private lateinit var simpleProgressBar:ProgressBar

    lateinit var animationDraw: AnimationDrawable
    lateinit var minigameT1Activity:MinigameT1

    private lateinit var rewardLayout: ConstraintLayout
    private lateinit var rewardAnimationView: LottieAnimationView
    private lateinit var  rewardTextView: TextView
    private lateinit var rewardAvatarLayout: LinearLayout
    //private lateinit var smallRewardAvatar: LottieAnimationView
    private lateinit var txtViewAvatarName:TextView
    private lateinit var circleImageView: CircleImageView
    //private lateinit var  rewardValueView: TextView
    private lateinit var  selectedBarPercent: TextView
    private lateinit var  selectedBar: TextView
    //private lateinit var  progressLabel: TextView
    //private lateinit var  progressLevel: TextView
    //private lateinit var  progressProgressBar: ProgressBar
    private lateinit var  circularProgress: FullScreenCircularProgressView

    var randomProgress: Int = -1
    var randomReaward : Int = 0

    val filename = "avatar.png"

    //private lateinit var fingerButton: ImageButton
    private var currentChestGroup: Int = 0
    var onlyOnce:Boolean = false
    //var isClicked:Boolean = false

    var slide: Animation? = null
    var gameView: GameView? = null

    private var navigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentScore = 0
        threadRunning = false
        if (!MyTimer.avatarProfile.ProgressBars.isEmpty()) {
            val size = MyTimer.avatarProfile.ProgressBars.size
            if (size > 1) {
                randomProgress = Random.nextInt(0, size - 1)
            } else {
                // Handle the case where there's only one element in the list
                randomProgress = 0 // or some other default value
            }
        }
    }

    private fun bouncingImage(img: ImageButton) {
        val animation = AnimationUtils.loadAnimation(minigameT1Activity, R.anim.bounce)
        img.startAnimation(animation)
    }

    fun Click(view: View?) {
        hintAnimation.cancelAnimation()
        hintAnimation.visibility = View.GONE
        if(!onlyOnce){
            gameView?.onTouchEvent(MotionEvent.obtain(1,1,MotionEvent.ACTION_DOWN,1f,1f,1))
            val img = ImageView(minigameT1Activity)
            img.layoutParams = LinearLayout.LayoutParams(100, 100) // value is in pixels

            val imgResId = R.drawable.coin
            //var resId = imgResId
            img.setImageResource(imgResId)
            val mLayout: ViewGroup  = minigameT1Activity.findViewById(R.id.layout_content)
            val lh:Int = mLayout.height
            val lw:Int = mLayout.width
            img.setY(0F)
            var rand: Int = Random.nextInt(lw)
            img.setX(rand.toFloat())
            img.elevation = 10F
            mLayout.addView(img)
            slide = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.2f
            )

            (slide as TranslateAnimation).setDuration(400)
            (slide as TranslateAnimation).setFillAfter(true)
            (slide as TranslateAnimation).setFillEnabled(true)
            img.startAnimation(slide)

            val currentScoreT: TextView = minigameT1Activity.findViewById<TextView>(R.id.scoreInf)
            bouncingImage(minigameT1Activity.findViewById<ImageButton>(R.id.waifuInf))

            if (currentScore >= 100 && !onlyOnce) {
                imageBtn.setImageResource(Utilities.chests[currentChestGroup][3])
                for (i in 1..26) {
                    val img2:ImageView = ImageView(minigameT1Activity)
                    img2.layoutParams = LinearLayout.LayoutParams(100, 100) // value is in pixels

                    img2.setImageResource(imgResId)
                    var rand2: Int = Random.nextInt(lh)
                    var rand3: Int = Random.nextInt(lw)
                    img2.setY(rand2.toFloat())
                    img2.setX(rand3.toFloat())
                    img2.elevation = 10F
                    mLayout.addView(img2)
                    slide = TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 5.2f
                    )

                    (slide as TranslateAnimation).setDuration(400)
                    (slide as TranslateAnimation).setFillAfter(true)
                    (slide as TranslateAnimation).setFillEnabled(true)
                    img2.startAnimation(slide)
                }

                animationDraw = ContextCompat.getDrawable(view!!.context, R.drawable.boom_animation) as AnimationDrawable
                imageBtn.setImageDrawable(animationDraw)
                // Calculate the total duration of the animation
                var totalDuration = 0
                for (i in 0 until animationDraw.numberOfFrames) {
                    totalDuration += animationDraw.getDuration(i)
                }

                animationDraw.start()

                // Phase 1: Hide initial content and show "You Won!" message
                img.postDelayed({
                    minigameT1Activity?.run {
                        // Hide initial content
                        hintAnimation.visibility = View.INVISIBLE
                        scoreInf.visibility = View.INVISIBLE
                        simpleProgressBar.visibility = View.INVISIBLE
                        currentScoreInf.visibility = View.INVISIBLE
                        mLayout1.visibility = View.INVISIBLE
                        imageBtn.setImageDrawable(null)
                        imageBtn.setBackgroundResource(0)

                        // Show "You Won!" message
                        youwonMsg.visibility = View.VISIBLE
                    }

                    // Phase 2: Hide "You Won!" message and show reward layout
                    // This postDelayed is nested inside the first one to ensure it starts after the "You Won!" message has been shown for 3 seconds
                    youwonMsg.postDelayed({
                        // Hide the "You Won!" message
                        youwonMsg.visibility = View.GONE

                        // Show the reward layout
                        rewardLayout.visibility = View.VISIBLE

                        randomReaward = Random.nextInt(1,30)
                        val text1 = "+"+ randomReaward.toString()+"\n"
                        var text2 = "Creativity Points"
                        if(randomProgress>=0)
                            text2 = MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressName + " Points"
                        val spannableString = SpannableString(text1 + text2)
                        // Set color for the first part
                        val colorSpan1 = ForegroundColorSpan(Color.GREEN) // Change Color.BLUE to any color you want for the first part
                        spannableString.setSpan(colorSpan1, 0, text1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        // Set color for the second part
                        val colorSpan2 = ForegroundColorSpan(Color.WHITE) // Change Color.RED to any color you want for the second part
                        spannableString.setSpan(colorSpan2, text1.length, text1.length + text2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        // Apply the SpannableString to the TextView
                        rewardTextView.text = spannableString


                        // Phase 3: Hide reward layout and show rewardAvatarLayout
                        // This postDelayed is nested inside the second one to ensure it starts after the reward layout has been shown for 3 seconds
                        rewardLayout.postDelayed({
                            // Hide the reward layout
                            rewardLayout.visibility = View.GONE

                            // Show the new content (rewardAvatarLayout)
                            rewardAvatarLayout.visibility = View.VISIBLE

                            txtViewAvatarName.text = MyTimer.avatarProfile.Name
                            val file = File(minigameT1Activity.filesDir, filename)
                            if (file.exists()) {
                                // The avatar image file exists, decode it to a bitmap and set it to the ImageView
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                circleImageView.setImageBitmap(bitmap)
                            } else {
                                // The avatar image file does not exist, use a default placeholder image
                                circleImageView.setImageResource(R.drawable.avatar) // Use a placeholder image from your drawable resources
                            }

                            /*try {
                                val fileInputStream: FileInputStream = minigameT1Activity.openFileInput(filename)
                                val bitmap = BitmapFactory.decodeStream(fileInputStream)
                                fileInputStream.close()
                                circleImageView.setImageBitmap(bitmap)
                            } catch (e: FileNotFoundException) {
                                circleImageView.setImageResource(R.drawable.avatar)
                            } catch (e: IOException) {
                                circleImageView.setImageResource(R.drawable.avatar)
                            }*/
                            //smallRewardAvatar.repeatMode = LottieDrawable.RESTART
                            //smallRewardAvatar.playAnimation()
                            //rewardValueView.text = "+"+ randomReaward.toString()
                            //progressLevel.text = MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue.toString()
                            //progressLabel.text = MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressName
                            //progressProgressBar.progress = MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue.toInt()
                            if(!MyTimer.avatarProfile.ProgressBars.isEmpty())
                                circularProgress.progress=MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue
                            else
                                circularProgress.progress = 0f

                            selectedBar.text = MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressName
                            selectedBarPercent.text = "${(MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue.toFloat() / maxProgressValue * 100).toInt()}%"

                            rewardAvatarLayout.postDelayed({
                                //smallRewardAvatar.visibility = View.GONE
                                //rewardValueView.visibility = View.GONE
                                MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue+=randomReaward

                                MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue = min(MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue, maxProgressValue)

                                selectedBar.text = MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressName
                                selectedBarPercent.text = "${(MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue.toFloat() / maxProgressValue * 100).toInt()}%"


                                //progressLevel.text = MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue.toString()
                                //progressProgressBar.setProgress(MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue, true)
                                circularProgress.animateProgress(MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue)

                                //circularProgress.setProgress(MyTimer.avatarProfile.ProgressBars[randomProgress].ProgressValue.toFloat())
                                rewardAvatarLayout.postDelayed({
                                    rewardAvatarLayout.visibility = View.GONE
                                    youwonMsg.text = "Discovered"
                                    youwonMsg.visibility = View.VISIBLE
                                    youwonMsg.postDelayed({
                                        youwonMsg.visibility = View.GONE
                                        navigateToNextScreen()
                                    }, 1500L)

                                }, 3000L)
                            }, 1500L)

                        }, 1500L) // Delay for transitioning to rewardAvatarLayout

                    }, 500L) // Delay for transitioning to rewardLayout

                }, totalDuration.toLong() + 100L) // Initial delay for showing "You Won!" message



                val storySysMsg ="You are amazing story and game master. You have Generated many game items, stories and titles. "
                val titleQuestion = "generate a title on one line no longer than 4 words for a fantasy game item potion. "
                val storyQuestion = "write by max 20 words a short story about magic potion for gaining knowledge and wisdom. "

                val randomString = Utilities.getRandomString()

                MyTimer.generateResponse(titleQuestion + randomString,storySysMsg,minigameT1Activity,"title")

                MyTimer.generateResponse(storyQuestion +randomString,storySysMsg,minigameT1Activity,"story")

                onlyOnce = true


            }
            else{
                if (currentScore >= 75) {
                    currentScore += (Math.random() * (21 - 1)).toInt()
                    if (currentScore >= 100)
                        currentScore=100
                    imageBtn.setImageResource(Utilities.chests[currentChestGroup][2])
                }
                else{
                    if (currentScore >= 50) {
                        currentScore += (Math.random() * (21 - 1)).toInt()
                        if (currentScore >= 100)
                            currentScore=100
                        imageBtn.setImageResource(Utilities.chests[currentChestGroup][1])
                    }
                    else{
                        currentScore+= 20
                    }
                }
            }
            currentScoreT.text = currentScore.toString()
            this.setProgress(currentScore)

            if (!threadRunning)
                infernalDecrease(view)
        }

    }

    fun infernalDecrease(v: View?) {
        threadRunning = true
        Thread {
            while (currentScore < 94) {
                try {
                    Thread.sleep((Math.abs(2000 - currentScore) + 100).toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                //currentScore-=3
                minigameT1Activity.runOnUiThread(Runnable {
                    val currentScoreT: TextView? = v?.findViewById<TextView>(R.id.scoreInf)
                    currentScoreT?.text = currentScore.toString()
                    this.setProgress(currentScore)

                    if (currentScore < 50) {
                        imageBtn.setImageResource(Utilities.chests[currentChestGroup][0])
                    }else{
                        if (currentScore < 75) {
                            imageBtn.setImageResource(Utilities.chests[currentChestGroup][1])
                        }else{
                            if (currentScore < 100) {
                                imageBtn.setImageResource(Utilities.chests[currentChestGroup][2])
                            }
                        }
                    }
                })
            }
        }.start()
    }

    public fun setProgress(pro: Int){
        this.simpleProgressBar.progress = pro
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onlyOnce = false
        minigameT1Activity = activity as MinigameT1
        minigameT1Activity.isSwipeAnimRendered = false
        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_R_IDLE.ordinal)

        val view:View =  inflater.inflate(R.layout.activity_infernal_click, container, false)

        rewardAnimationView = view.findViewById(R.id.rewardAnimationView)
        rewardLayout = view.findViewById(R.id.rewardLayout)
        rewardAvatarLayout = view.findViewById(R.id.rewardAvatar)
        youwonMsg = view.findViewById(R.id.youwonMsg)
        scoreInf = view.findViewById(R.id.scoreInf)
        currentScoreInf= view.findViewById(R.id.currentScoreInf)
        rewardTextView = view.findViewById(R.id.rewardTextView)
        txtViewAvatarName=view.findViewById(R.id.avatar_name)
        circleImageView = view.findViewById(R.id.avatar) as CircleImageView
        hintAnimation= view.findViewById(R.id.hintAnimation)
        imageBtn = view.findViewById(R.id.waifuInf)
        simpleProgressBar= view.findViewById(R.id.simpleProgressBar) as ProgressBar
        //smallRewardAvatar=view.findViewById(R.id.smallReward)
        //rewardValueView = view.findViewById(R.id.rewardValueView)
        selectedBar = view.findViewById(R.id.selectedBar)
        selectedBarPercent = view.findViewById(R.id.selectedBarPercent)
        //progressLabel = view.findViewById(R.id.label)
        //progressLevel= view.findViewById(R.id.level)
        //progressProgressBar= view.findViewById(R.id.levelProgressBar)
        circularProgress = view.findViewById(R.id.circularProgressBar)

        playHintLottieAnimation()

        //progressProgressBar.max = 100


        imageBtn.setOnClickListener(View.OnClickListener { Click(view) })
        var rand: Int = Random.nextInt(3)
        if(MyTimer.isSwipedBack ) {
            rand = MyTimer.settings.getInt(
                minigameT1Activity.getString(R.string.lastShowenImage),
                0
            )
        }
        else{
            with(MyTimer.settings.edit()) {
                putInt(
                    minigameT1Activity.getString(R.string.lastShowenImage),
                    rand
                )
                apply()
            }
        }
        when(rand){
            0->{
                currentChestGroup = 0
                imageBtn.setImageResource(Utilities.chests[0][0])
            }
            1->{
                currentChestGroup = 1
                imageBtn.setImageResource(Utilities.chests[1][0])
            }
            2->{
                currentChestGroup = 2
                imageBtn.setImageResource(Utilities.chests[2][0])
            }
            else-> {
                currentChestGroup = 0
                imageBtn.setImageResource(Utilities.chests[0][0])
            }
            //default value. Usually if the code is right, this line should not be reached.
        }


        this.simpleProgressBar.max = 100
        this.setProgress(0)
        /*val image: ImageView = view.findViewById(R.id.imgView)*/
        gameView = GameView(context,"coin")
        gameView!!.setIsInList(true)
        var s: SurfaceView = gameView as GameView
        s.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        //s.setZOrderOnTop(true)
        s.z = 5F
        val f = s.willNotDraw()
        mLayout1  = view.findViewById(R.id.layout_sprite)
        mLayout1.addView(s)
        return view
    }

    private fun navigateToNextScreen() {
        GlobalScope.launch {
            //delay(3000) // Adjust based on your needs
            withContext(Dispatchers.Main) {
                if (!navigated) {
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    val currentNavHost =
                        minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                    if (MyTimer.isSwipedBack) {
                        val bundle = Bundle()
                        bundle.putBoolean("causeReminder", false)
                        currentNavHost.navController.navigate(R.id.minigameT1R1, bundle)
                    } else {
                        currentNavHost.navController.navigate(R.id.minigameT1R1)
                    }
                    MyTimer.isSwipedBack = false
                    navigated = true
                }
            }
        }
    }

    private fun playHintLottieAnimation() {
        // check if it has been clicked or onlyOnce is true then don't play the animation
        //if (isClicked ) return

        hintAnimation.visibility = View.VISIBLE
        hintAnimation.repeatCount = LottieDrawable.INFINITE
        hintAnimation.playAnimation()
    }

    override fun onResume() {
        super.onResume()

        // Diz ao método retomar gameView para executar
        gameView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        gameView!!.pause()
        if(!onlyOnce){
            // Stop AnimationDrawable if its running allotted to ImageButton imageBtn
            (imageBtn.drawable as? AnimationDrawable)?.stop()
        }
    }
}