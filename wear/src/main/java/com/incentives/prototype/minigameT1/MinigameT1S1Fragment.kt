package com.incentives.prototype.minigameT1

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.wear.widget.WearableLinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.incentives.prototype.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Math.min
import kotlin.random.Random


class MinigameT1S1Fragment : Fragment() , OnCustomScrollSwipeListener, MyTimer.Companion.AvatarProfileUpdateListener{

    private val progressBarViews = mutableListOf<ProgressBar>()
    private val progressPercentageViews = mutableListOf<TextView>()

    lateinit var swipeDetector: OnSwipeTouchListener
    private lateinit var customScrollView: CustomScrollView

    private lateinit var txtViewTimer:TextView
    private lateinit var btnTimerControlStart:ImageButton
    private lateinit var btnTimerControlStop:ImageButton
    private lateinit var btnTimerControlPause:ImageButton
    lateinit var minigameT1Activity:MinigameT1

    private lateinit var simpleProgressBar:ProgressBar
    private lateinit var txtViewLable:TextView
    private lateinit var txtViewLevel:TextView

    private lateinit var mainAnimationView: LottieAnimationView
    private lateinit var glowAnimationView: ImageView
    private lateinit var viewAnimator: ViewAnimator

    private lateinit var model: ItemListSharedVM

    lateinit var labelsAnim: List<String>

    private lateinit var arrowBtn: LottieAnimationView

    private lateinit var txtViewAvatarName:TextView

    private lateinit var layout_progressBars: ConstraintLayout

    private fun buildAdapter(): PagerAdapter? {
        return SpriteAdapter(activity, childFragmentManager, 9)
    }

    override fun onSwipeDetected(view: View, event: MotionEvent) {
        // Handle swipe event
        swipeDetector.onTouch(customScrollView, event)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_minigame_t1_s1, container, false)
        minigameT1Activity = activity as MinigameT1
        MyTimer.addAvatarProfileUpdateListener(this)
        customScrollView = view.findViewById<CustomScrollView>(R.id.scrollViewT1S1)
        customScrollView.setOnCustomScrollSwipeListener(this)
        swipeDetector = object : OnSwipeTouchListener(minigameT1Activity) {
            override fun onSwipeRight() {
                minigameT1Activity.finish()
                minigameT1Activity.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }

            override fun onSwipeLeft() {
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    currentNavHost.navController.navigate(R.id.action_fragment_minigame_t1_s1_to_fragment_minigame_t1_s2)
                }
            }
        }

        labelsAnim = listOf("spyglass.json", "heart.json", "paint-tray.json", "idea.json")
        if (activity is MinigameT1) {
            if ((activity as MinigameT1).startCodeRequested) {
                startCode()
                GlobalScope.sendStartToMobile()
                (activity as MinigameT1).startCodeRequested = false
            }
        }

        model = ViewModelProvider(this.requireActivity()).get(ItemListSharedVM::class.java)

        txtViewAvatarName=view.findViewById(R.id.avatar_name)
        txtViewAvatarName.text = MyTimer.avatarProfile.Name

        val circularImageView = view.findViewById(R.id.avatar) as CircleImageView
        val filename = "avatar.png"

        val file = File(minigameT1Activity.filesDir, filename)

        if (file.exists()) {
            // The avatar image file exists, decode it to a bitmap and set it to the ImageView
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            circularImageView.setImageBitmap(bitmap)
        } else {
            // The avatar image file does not exist, use a default placeholder image
            circularImageView.setImageResource(R.drawable.avatar) // Use a placeholder image from your drawable resources
        }



        /*try {
            val fileInputStream: FileInputStream = minigameT1Activity.openFileInput(filename)
            val bitmap = BitmapFactory.decodeStream(fileInputStream)
            fileInputStream.close()
            circularImageView.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            Log.e("WearablePart", "File not found: $filename, using default avatar", e)
            // Set a default avatar image from drawable resources when the specific file is not found
            circularImageView.setImageResource(R.drawable.avatar) // Ensure you have a default_avatar.png in your drawable resources
        }
        catch (e: IOException) {
            Log.e("WearablePart", "Error loading the image from internal storage", e)
            // Fallback to default avatar in case of other IO exceptions
            circularImageView.setImageResource(R.drawable.avatar)
        }*/

        //circularImageView.setImageResource(R.drawable.avatar)


        val avatarProfile = MyTimer.avatarProfile

        val scale = resources.displayMetrics.density
        val dpWidthInPx = (160 * scale).toInt()
        val dpPaddingInPx = (0 * scale).toInt()
        val dpMarginTopInPx = (10 * scale).toInt()
        val fixedWidthInPx = (140 * scale).toInt()

        layout_progressBars = view.findViewById(R.id.layout_progressbars)
        var previousItemBottomId = layout_progressBars.id // No item above on the first iteration

        for ((i, progressBarUnity) in avatarProfile.ProgressBars.withIndex()) {
            val titleTextViewId = View.generateViewId()
            val percentageTextViewId = View.generateViewId()
            val progressBarId = View.generateViewId()

            val titleTextView = TextView(context).apply {
                id = titleTextViewId
                text = progressBarUnity.ProgressName
                textSize = 14f // Match title text size to progress percentage text size
                setTextColor(Color.WHITE) // Ensure both TextViews have same text color
                maxLines = 2 // Limits the lines of the text to 2 lines
                ellipsize = TextUtils.TruncateAt.END // Adds ellipsis at the end if the text exceeds 2 lines

                val typeface1: Typeface? = try {
                    ResourcesCompat.getFont(context, R.font.roboto_medium)
                } catch (e: Exception) {
                    Log.e("FontError", "Error fetching font", e)
                    null
                }

                typeface = typeface1

                //typeface = ResourcesCompat.getFont(context, R.font.roboto_medium) // Ensure both TextView use the same font
                maxWidth = fixedWidthInPx // Set maximum width
                layoutParams = ConstraintLayout.LayoutParams(fixedWidthInPx, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dpMarginTopInPx
                    marginStart = dpPaddingInPx
                }
            }

            val progressPercentageTextView = TextView(context).apply {
                id = percentageTextViewId
                text = "${(progressBarUnity.ProgressValue.toFloat() / 180f * 100).toInt()}%"
                textSize = 14f // Matched title text size
                setTextColor(Color.WHITE) // Text color matched with title
                val typeface1: Typeface? = try {
                    ResourcesCompat.getFont(context, R.font.roboto_medium)
                } catch (e: Exception) {
                    Log.e("FontError", "Error fetching font", e)
                    null
                }
                typeface = typeface1
                //typeface = ResourcesCompat.getFont(context, R.font.roboto_medium) // TypeFace matched with title
                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dpMarginTopInPx
                    marginEnd = dpPaddingInPx
                }
            }

            val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                val vibrantColors = listOf(
                    Color.rgb(255, 0, 0), // Red
                    Color.rgb(0, 255, 0), // Green
                    Color.rgb(0, 0, 255), // Blue
                    Color.rgb(255, 255, 0)  // Yellow
                )

                val color: Int
                if (i < vibrantColors.size) {
                    color = vibrantColors[i]
                }
                else {
                    // Generate custom colors for i >= 4
                    val offset = 20
                    val base = 30
                    val red = (i * 40 % 235) + offset + base
                    val green = (i * 160 % 235) + offset + base
                    val blue = (i * 100 % 235) + offset + base

                    color = Color.rgb(
                        min(red, 255),
                        min(green, 255),
                        min(blue, 255)
                    )
                }

                progressDrawable = ContextCompat.getDrawable(context, R.drawable.my_progress_drawable)
                setBackgroundColor(Color.LTGRAY) // Set a background color
                progressBackgroundTintList = ColorStateList.valueOf(Color.LTGRAY) // Set a background progress color
                progressTintList = ColorStateList.valueOf(color) // Dynamically set the color here
                
                max = 180
                progress = progressBarUnity.ProgressValue.toInt()
                id = progressBarId
                layoutParams = ConstraintLayout.LayoutParams(dpWidthInPx, (12 * scale).toInt()).apply {
                    // Ensure this margin aligns with the start margin of title text
                    marginStart = dpPaddingInPx
                    if (previousItemBottomId != View.NO_ID) {
                        topToBottom = previousItemBottomId
                        topMargin = dpMarginTopInPx
                    }
                    marginEnd = dpPaddingInPx
                }
            }

            progressBarViews.add(progressBar)
            progressPercentageViews.add(progressPercentageTextView)

            // Add Views to ConstraintLayout before you create any ConstraintSets
            layout_progressBars.addView(titleTextView)
            layout_progressBars.addView(progressPercentageTextView)
            layout_progressBars.addView(progressBar)

            // Initialise new constraint set and clone layout parameters from our layout
            val constraints = ConstraintSet()
            constraints.clone(layout_progressBars)

            // Apply constraints for titleTextView to stay at the top of each individual progress
            constraints.connect(titleTextViewId, ConstraintSet.START, layout_progressBars.id, ConstraintSet.START, dpPaddingInPx)
            constraints.connect(
                titleTextViewId, ConstraintSet.TOP,
                previousItemBottomId, if (previousItemBottomId == layout_progressBars.id) ConstraintSet.TOP else ConstraintSet.BOTTOM,
                dpMarginTopInPx
            )

            // Apply constraints for progressPercentageTextView:
            constraints.connect(percentageTextViewId, ConstraintSet.END, layout_progressBars.id, ConstraintSet.END, dpPaddingInPx)
            constraints.connect(percentageTextViewId, ConstraintSet.BOTTOM, progressBarId, ConstraintSet.BOTTOM)

            // set the titleTextView to be aligned with the start of progress bars
            constraints.connect(titleTextViewId, ConstraintSet.START, progressBarId, ConstraintSet.START)
            constraints.connect(titleTextViewId, ConstraintSet.BOTTOM, progressBarId, ConstraintSet.TOP)

            // set the progressPercentageTextView to be aligned with the end of progress bars
            constraints.connect(percentageTextViewId, ConstraintSet.END, progressBarId, ConstraintSet.END)
            constraints.connect(percentageTextViewId, ConstraintSet.BOTTOM, progressBarId, ConstraintSet.TOP)



            // Apply constraints for each progressBar
            constraints.connect(progressBarId, ConstraintSet.START, layout_progressBars.id, ConstraintSet.START, dpPaddingInPx)
            constraints.connect(progressBarId, ConstraintSet.END, layout_progressBars.id, ConstraintSet.END, dpPaddingInPx)
            constraints.connect(progressBarId, ConstraintSet.TOP, titleTextViewId, ConstraintSet.BOTTOM, dpPaddingInPx)

            constraints.applyTo(layout_progressBars)

            // Remember the last element's id so next views will be placed below it correctly
            previousItemBottomId = progressBarId
        }




















        simpleProgressBar = view.findViewById(R.id.simpleProgressBar) as ProgressBar
        txtViewLable=view.findViewById(R.id.label)
        txtViewLevel=view.findViewById(R.id.level)

        mainAnimationView = view.findViewById(R.id.mainAnimationView)
        mainAnimationView.visibility = View.VISIBLE
        mainAnimationView.repeatCount = LottieDrawable.INFINITE

        glowAnimationView = view.findViewById(R.id.glowAnimationView)


        when(MyTimer.currLable){
            MyTimer.labels[0]->{
                txtViewLable.text = MyTimer.labels[0]
                mainAnimationView.setAnimation(labelsAnim[0])
                glowAnimationView.visibility = View.GONE

            }
            MyTimer.labels[1]->{
                txtViewLable.text = MyTimer.labels[1]
                mainAnimationView.setAnimation(labelsAnim[1])
                glowAnimationView.visibility = View.VISIBLE
                createPulsingGlow()

            }
            MyTimer.labels[2]->{
                txtViewLable.text = MyTimer.labels[2]
                mainAnimationView.setAnimation(labelsAnim[2])
                glowAnimationView.visibility = View.GONE

            }
            MyTimer.labels[3]->{
                txtViewLable.text = MyTimer.labels[3]
                mainAnimationView.setAnimation(labelsAnim[3])
                glowAnimationView.visibility = View.VISIBLE
                createPulsingGlow()
            }
        }


        mainAnimationView.playAnimation()



        arrowBtn = view.findViewById(R.id.arrowBtn)
        arrowBtn.repeatMode = LottieDrawable.RESTART
        arrowBtn.repeatCount = LottieDrawable.INFINITE
        arrowBtn.setSpeed(1.0f)
        arrowBtn.playAnimation()


        viewAnimator = view.findViewById(R.id.viewAnimator)
        //animateArrow()
        viewAnimator.setOnClickListener {
            arrowBtn.visibility = View.INVISIBLE
            //MyTimer.currLable = "LEARNING"
            if (viewAnimator.displayedChild != viewAnimator.childCount - 1) {
                applyAnimation(true, viewAnimator)
            } else {
                applyAnimation(false, viewAnimator)
            }
        }
        arrowBtn.setOnClickListener {
            if (viewAnimator.currentView == viewAnimator.getChildAt(0)) {
                viewAnimator.showNext()
            } else {
                viewAnimator.showPrevious()
            }
        }

        val recyclerView1 = view.findViewById<View>(R.id.recycler_view1) as RecyclerView

        val layoutManager1 = LinearLayoutManager(minigameT1Activity)
        layoutManager1.orientation = WearableLinearLayoutManager.VERTICAL

        recyclerView1.layoutManager = layoutManager1

        model.itemsList1.value = ArrayList<ItemsListMenu>().apply {
            add(ItemsListMenu(R.drawable.plus1, "Thought", R.drawable.idea1))
            add(ItemsListMenu(R.drawable.plus1, "Emotion", R.drawable.platozas))
            add(ItemsListMenu(R.drawable.plus1, "Imagination", R.drawable.idea2))
            add(ItemsListMenu(R.drawable.plus1, "Action", R.drawable.plato1))
            add(ItemsListMenu(R.drawable.plus1, "Belief", R.drawable.whatza1))
        }

        val listAdapter = AnswerAdapter(this.model.itemsList1.value) { v, itemPosition ->
            if(itemPosition==0){
                //val intent = Intent(minigameT1Activity, MinigameT1::class.java)
                //startActivity(intent)
            }
        }
        recyclerView1.adapter = listAdapter



        val recyclerView2 = view.findViewById<View>(R.id.recycler_view2) as RecyclerView

        val layoutManager2 = LinearLayoutManager(minigameT1Activity)
        layoutManager2.orientation = WearableLinearLayoutManager.VERTICAL

        recyclerView2.layoutManager = layoutManager2

        model.itemsList2.value = ArrayList<ItemsListMenu>().apply {
            add(ItemsListMenu(R.drawable.plus1, "Dreams", R.drawable.memories))
            add(ItemsListMenu(R.drawable.plus1, "Goals", R.drawable.target))
            add(ItemsListMenu(R.drawable.plus1, "Rules", R.drawable.rules))
            add(ItemsListMenu(R.drawable.plus1, "Memories", R.drawable.dreaming))
            add(ItemsListMenu(R.drawable.plus1, "Questions", R.drawable.question))
            add(ItemsListMenu(R.drawable.plus1, "Dangers", R.drawable.expression))
            add(ItemsListMenu(R.drawable.plus1, "Desires", R.drawable.instructions))
        }


        val listAdapter2 = AnswerAdapter(model.itemsList2.value!!) { v, itemPosition ->
            if(itemPosition==0){
                //val intent = Intent(minigameT1Activity, MinigameT1::class.java)
                //startActivity(intent)
            }
        }
        recyclerView2.adapter = listAdapter2

        txtViewTimer=view.findViewById(R.id.txtViewTimer)
        btnTimerControlStart=view.findViewById(R.id.btnStart)
        btnTimerControlStop=view.findViewById(R.id.btnStop)
        btnTimerControlPause=view.findViewById(R.id.btnPause)

        val nodesCount = MyTimer.selectedSyncNodesIds.size

        //val timerValue:Int =
        //MyTimer.settings.getInt(MyTimer.context.getString(R.string.primaryTimerDurString), MyTimer.context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1))
        this.setProgressMax(MyTimer.primaryTimerDurString)
        when(MyTimer.currentState){
            EnumStateType.ALL_INACTIVE.ordinal->{
                txtViewTimer.text=MyTimer.ConvertInTimeStr(MyTimer.primaryTimerDurString)
                this.setProgress(this.simpleProgressBar.max)
            }
            else->{
                txtViewTimer.text=MyTimer.ConvertInTimeStr(MyTimer.remainingSecondsPrimary)
                this.setProgress(MyTimer.remainingSecondsPrimary)
            }
        }

        btnTimerControlStart.setOnClickListener(View.OnClickListener {
            if(MyTimer.isTimerPaused || MyTimer.remainingSecondsPrimary.equals(-1) || MyTimer.isTimerStopped) {
                if(!MyTimer.currentState.equals(EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal))
                    GlobalScope.sendStartToMobile()
                startCode()
            }
        })

        btnTimerControlStop.setOnClickListener(View.OnClickListener {
            if(!MyTimer.isTimerStopped) {
                GlobalScope.sendStopToMobile()
                stopCode()
            }
        })

        btnTimerControlPause.setOnClickListener(View.OnClickListener {
            if(!MyTimer.isTimerPaused && !MyTimer.remainingSecondsPrimary.equals(-1) && !MyTimer.isTimerStopped) {
                GlobalScope.sendPauseToMobile()
                pauseCode()
            }
        })

        val btnToT1S2: ImageButton = view.findViewById(R.id.btnToT1S2)
        btnToT1S2.setOnClickListener(View.OnClickListener {
            findNavController().navigate(MinigameT1S1FragmentDirections.actionFragmentMinigameT1S1ToFragmentMinigameT1S2())
            Log.d("MyTag","Hello")
        })

        return view
    }

    override fun onAvatarProfileUpdated(newProfile: AvatarProfile) {
        // Update your UI here with the new profile data
        updateProgressBars(newProfile)
    }

    private fun updateProgressBars(avatarProfile: AvatarProfile) {
        // Run the UI update on the main thread
        activity?.runOnUiThread {
            if (progressBarViews.size == avatarProfile.ProgressBars.size) {
                for ((i, progressBarUnity) in avatarProfile.ProgressBars.withIndex()) {
                    val progressBar = progressBarViews[i]
                    val textPercentage = progressPercentageViews[i]
                    progressBar.progress = progressBarUnity.ProgressValue.toInt()
                    textPercentage.text = "${(progressBarUnity.ProgressValue / 180f * 100).toInt()}%"
                }
            } else {
                // The data has changed, we need to rebuild the progress bars
                buildProgressBars(avatarProfile)
            }
        }
    }

    private fun buildProgressBars(avatarProfile: AvatarProfile) {
        // Clear existing progress bars and rebuild them
        progressBarViews.clear()
        progressPercentageViews.clear()
        layout_progressBars.removeAllViews() // Assuming layout_progressBars is your layout container

        // Rebuild the progress bars here, similar to what you do in onCreateView
    }


    private fun createPulsingGlow() {
        val glowAnimator = ValueAnimator.ofFloat(0f, 1f)
        glowAnimator.duration = 2900
        glowAnimator.repeatMode = ValueAnimator.REVERSE
        glowAnimator.repeatCount = ValueAnimator.INFINITE
        glowAnimator.interpolator = LinearInterpolator()
        glowAnimator.addUpdateListener { animatedValue ->
            val progress = animatedValue.animatedValue as Float
            glowAnimationView.alpha = progress
        }
        glowAnimator.start()
    }

    private fun animateArrow() {
        val arrowAnimator = ObjectAnimator.ofFloat(arrowBtn, "translationX", 0f, -15f, 0f)
        arrowAnimator.duration = 800
        arrowAnimator.repeatMode = ObjectAnimator.REVERSE
        arrowAnimator.repeatCount = ObjectAnimator.INFINITE
        arrowAnimator.start()
    }

    private fun applyAnimation(forward: Boolean, viewAnimator: ViewAnimator) {
        arrowBtn.visibility = View.INVISIBLE
        viewAnimator.findViewById<ConstraintLayout>(
            viewAnimator.getChildAt(viewAnimator.displayedChild).id
        ).animate()
            .translationX(if (forward) -1000f else 1000f)
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                val newState = if (forward) viewAnimator.displayedChild + 1 else viewAnimator.displayedChild - 1
                viewAnimator.displayedChild = newState % viewAnimator.childCount;

                // Get the child by the viewAnimator.displayedChild
                val nextView = viewAnimator.getChildAt(viewAnimator.displayedChild).also {
                    it.translationX = if (forward) 1000f else -1000f
                } as ConstraintLayout

                nextView.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(400)
                    .withEndAction {
                        arrowBtn.visibility = View.VISIBLE
                    }
            }
    }

    public fun setProgressMax(max: Int){
        this.simpleProgressBar.max = max
    }

    public fun setProgress(pro: Int){
        this.simpleProgressBar.progress = pro
    }

    public fun startCode(){
        when(MyTimer.currentState) {
            EnumStateType.ALL_INACTIVE.ordinal -> {
                if(MyTimer.isTimerStopped) {
                    MyTimer.totalRun += 1
                    MyTimer.GtotalRun += 1
                    Log.d("MyTag", "Timer starting from beginning")
                    MyTimer.InitializeAndStartTimer(
                        minigameT1Activity,
                        EnumAlarmTypes.valueOf("MinigameT1"),
                        EnumStateType.PRMR_TMR_RNG_ONLY.ordinal,
                        MyTimer.settings.getInt(
                            minigameT1Activity.getString(R.string.primaryTimerDurString),
                            minigameT1Activity.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)
                        )
                    )
                }
                else {
                    MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)
                }
            }
            EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal -> {
                MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                findNavController().navigate(MinigameT1S1FragmentDirections.actionFragmentMinigameT1S1ToMinigameT1RPre())
            }
            else->{
                MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)
            }
        }
        MyTimer.isTimerPaused = false
        MyTimer.isTimerStopped = false
    }

    public fun stopCode(){
        MyTimer.isTimerPaused = false
        MyTimer.isTimerStopped = true
        Log.d("MyTag","Cancelling timer")
        MyTimer.ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
        MyTimer.resetScoreAndReminder()
        MyTimer.remainingSecondsPrimary = MyTimer.primaryTimerDurString
        MyTimer.totalStopped+=1
        MyTimer.GtotalStopped+=1
        txtViewTimer.text=MyTimer.ConvertInTimeStr(MyTimer.settings.getInt(minigameT1Activity.getString
            (R.string.primaryTimerDurString), minigameT1Activity.resources.getInteger
            (R.integer.primaryTimerDurSecMinigameT1)))
        this.setProgress(this.simpleProgressBar.max)
    }

    public fun pauseCode(){
        MyTimer.isTimerPaused = true
        Log.d("MyTag","Pausing timer")
        MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_PSD.ordinal)
    }

    fun changeTimerText(str:String){
        if(txtViewTimer!=null)
            txtViewTimer.text=str
    }

    public fun CoroutineScope.sendStartToMobile() {
        //val timer = MyTimer.settings.getInt(MyTimer.context.getString(R.string.primaryTimerDurString), MyTimer.context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1))
        val datapath = "/allouf_path"
        launch(Dispatchers.IO) {
            SendMessage(
                datapath, "StartAndPlay&&"+MyTimer.primaryTimerDurString, requireContext()).start()
        }
    }

    public fun CoroutineScope.sendStopToMobile() {
        val datapath = "/allouf_path"
        launch(Dispatchers.IO) {
            SendMessage(
                datapath, "StopAndReset", requireContext()).start()
        }
    }

    public fun CoroutineScope.sendPauseToMobile() {
        val datapath = "/allouf_path"
        launch(Dispatchers.IO) {
            SendMessage(datapath, "PauseAndStop", requireContext()).start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MyTimer.removeAvatarProfileUpdateListener(this)
    }
}

