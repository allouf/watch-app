package com.incentives.prototype.minigameT1

import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.incentives.prototype.CustomScrollView
import com.incentives.prototype.EnumStateType
import com.incentives.prototype.OnCustomScrollSwipeListener
import com.incentives.prototype.OnSwipeTouchListener
import com.incentives.prototype.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [MinigameT1S3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MinigameT1S3Fragment : Fragment(), OnCustomScrollSwipeListener {

    lateinit var swipeDetector: OnSwipeTouchListener
    private lateinit var customScrollView: CustomScrollView

    lateinit var minigameT1Activity:MinigameT1
    //private var pagePosition: Int = 1
    private lateinit var viewPager:ViewPager2
    private lateinit var tabLayout:TabLayout
    //private lateinit var animatedArrow:ImageButton
    private lateinit var animatedArrow: LottieAnimationView

    private lateinit var TitleTitleView:TextView
    private lateinit var TitleContentView: TextView
    private lateinit var originalText: String
    private var isTextExpanded: Boolean = false
    private val maxLinesCollapsed = 4

    private fun setupTextView() {
        TitleContentView.text = originalText
        handleCollapsedText()
    }

    private fun handleCollapsedText() {
        TitleContentView.post {
            if (TitleContentView.layout.lineCount > maxLinesCollapsed && !isTextExpanded) {
                val spaceForMore = "... More".length  // including the ellipsis and an extra space
                val lineEndIndex = TitleContentView.layout.getLineEnd(maxLinesCollapsed - 1) - spaceForMore
                val textToShow = cutOffTextForMore(lineEndIndex)
                applySpannableStringToView(textToShow, "More")
            } else if (isTextExpanded) {
                applySpannableStringToView("$originalText Less", "Less")
            }
            TitleContentView.maxLines = if (isTextExpanded) Integer.MAX_VALUE else maxLinesCollapsed
            // After setting text, possibly need to adjust the scroll to make 'More' visible
            scrollTextViewToBottom()
        }
    }

    private fun applySpannableStringToView(text: String, clickText: String) {
        val spannableString = SpannableString(text).apply {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    isTextExpanded = !isTextExpanded  // Toggle the expanded state
                    handleCollapsedText()             // Handle the text update
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false   // Do not underline the "More" / "Less"
                    ds.color = Color.BLUE        // Set your clickable text color here
                }
            }
            // Find the indices for the clickText like "More" or "Less"
            val startIndexOfClickText = text.indexOf(clickText)
            if (startIndexOfClickText != -1) {
                setSpan(
                    clickableSpan,
                    startIndexOfClickText,
                    startIndexOfClickText + clickText.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        TitleContentView.movementMethod = LinkMovementMethod.getInstance()  // Make TextView clickable
        TitleContentView.text = spannableString
    }

    private fun cutOffTextForMore(endPosition: Int): String {
        // Adjust the cut-off not to be in the middle of a word
        val lastSpaceIndex = originalText.lastIndexOf(' ', endPosition) + 1
        if (lastSpaceIndex < endPosition) {
            return originalText.substring(0, lastSpaceIndex) + "More"
        }
        return originalText.substring(0, endPosition) + "More"
    }

    fun ScrollView.scrollToChild(view: View) {
        // Obtain height difference and scroll to that position to make 'More' visible
        val bottomOfTextView = Rect().apply {
            view.getDrawingRect(this)
            offset(view.left, view.top)
        }
        val parentRect = Rect().apply {
            this@scrollToChild.getDrawingRect(this)
        }
        if (bottomOfTextView.bottom > parentRect.bottom) {
            this@scrollToChild.smoothScrollTo(0, bottomOfTextView.bottom - parentRect.height())
        }
    }

    private fun scrollTextViewToBottom() {
        TitleContentView.post {
            (TitleContentView.parent as? ScrollView)?.scrollToChild(TitleContentView)
        }
    }

    private fun animateArrow() {
        val arrowAnimator = ObjectAnimator.ofFloat(animatedArrow, "translationX", 0f, -15f, 0f)
        arrowAnimator.duration = 800
        arrowAnimator.repeatMode = ObjectAnimator.REVERSE
        arrowAnimator.repeatCount = ObjectAnimator.INFINITE
        arrowAnimator.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun gotoNextPositionInPager(): Int = (viewPager.currentItem + 1) % viewPager.adapter!!.itemCount

    override fun onSwipeDetected(view: View, event: MotionEvent) {
        // Handle swipe event
        swipeDetector.onTouch(customScrollView, event)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        minigameT1Activity = activity as MinigameT1
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_minigame_t1_s3, container, false)

        val avatarImageView: de.hdodenhof.circleimageview.CircleImageView = view.findViewById(R.id.avatar)

        customScrollView = view.findViewById<CustomScrollView>(R.id.scrollViewT1S3)
        customScrollView.setOnCustomScrollSwipeListener(this)
        swipeDetector = object : OnSwipeTouchListener(minigameT1Activity) {
            override fun onSwipeRight() {
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    //MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    currentNavHost.navController.navigate(R.id.action_fragment_minigame_t1_s3_to_fragment_minigame_t1_s2)
                }
            }

            override fun onSwipeLeft() {
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    //MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    currentNavHost.navController.navigate(R.id.action_fragment_minigame_t1_s3_to_fragment_minigame_t1_s4)
                }
            }
        }

        viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment = when(position){
                0 -> MinigamePage1Fragment()
                else -> MinigamePage2Fragment()
            }
        }
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> } //Hide tab indicators
            .attach()


        animatedArrow = view.findViewById(R.id.animatedArrow)

        //animatedArrow.repeatMode = LottieDrawable.REVERSE
        animatedArrow.repeatCount = LottieDrawable.INFINITE
        animatedArrow.setSpeed(1.0f)
        animatedArrow.playAnimation()

        //animateArrow()
        animatedArrow.setOnClickListener {
            viewPager.setCurrentItem(gotoNextPositionInPager())
        }



        TitleTitleView=view.findViewById(com.incentives.prototype.R.id.TitleTitleView)
        TitleTitleView.text = MyTimer.algoTitle


        TitleContentView=view.findViewById(R.id.TitleContentView)
        TitleContentView.text = MyTimer.algoDescription

        // Set up your original text
        originalText = MyTimer.algoDescription
        // Perform initial check if we need to make text collapsible
        TitleContentView.text = originalText
        setupTextView()


        /*TitleContentView.setText(TitleContentView.text, TextView.BufferType.SPANNABLE)
        TitleContentView.setMovementMethod(LinkMovementMethod.getInstance());
        makeSpannable(TitleContentView, TitleContentView.text.toString(), moreText.toString())*/



        //val buttonExpand: Button = view.findViewById(com.incentives.prototype.R.id.button_expand)
        //val buttonCollapse: Button = view.findViewById(com.incentives.prototype.R.id.button_collapse)

        /*buttonExpand.setOnClickListener { v: View? ->
            buttonCollapse.visibility = View.VISIBLE
            buttonExpand.visibility = View.GONE
            TitleContentView.setMaxLines(Int.MAX_VALUE) // to expand all the lines
        }

        buttonCollapse.setOnClickListener { v: View? ->
            buttonCollapse.visibility = View.GONE
            buttonExpand.visibility = View.VISIBLE
            TitleContentView.setMaxLines(4) // or any number of lines that represents the collapsed state
        }*/

        /*if (MyTimer.AIImageTex.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(MyTimer.AIImageTex, 0, MyTimer.AIImageTex.size)
            avatarImageView.setImageBitmap(bitmap)
        } else {
            // Set a drawable as the default image.
            avatarImageView.setImageResource(R.drawable.andrius)
        }*/

        val filename = "image.png"


        val file = File(minigameT1Activity.filesDir, filename)

        if (file.exists()) {
            // The avatar image file exists, decode it to a bitmap and set it to the ImageView
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            avatarImageView.setImageBitmap(bitmap)
        } else {
            // The avatar image file does not exist, use a default placeholder image
            avatarImageView.setImageResource(R.drawable.avatar) // Use a placeholder image from your drawable resources
        }

        val totalRunValue: TextView = view.findViewById(R.id.totalRunValue)
        totalRunValue!!.text = MyTimer.totalRun.toString()

        val totalCompletedValue: TextView = view.findViewById(R.id.totalCompletedValue)
        totalCompletedValue!!.text = MyTimer.totalCompleted.toString()

        val totalStoppedValue: TextView = view.findViewById(R.id.totalStoppedValue)
        totalStoppedValue!!.text = MyTimer.totalStopped.toString()

        val completionRatingValue: TextView = view.findViewById(R.id.CompletionRatingValue)
        var percentage: Double = 0.0
        if(MyTimer.totalRun!=0)
            percentage = (MyTimer.totalCompleted.toDouble() / MyTimer.totalRun.toDouble()) * 100
        val percentageString = "%.1f%%".format(percentage)
        completionRatingValue!!.text = (percentageString).toString()

        val completionRatingProgressBar = view.findViewById(R.id.CompletionRatingProgressBar) as ProgressBar
        completionRatingProgressBar.setProgress(percentage.toInt())



        val EngagmentRatingValue: TextView = view.findViewById(R.id.EngagmentRatingValue)
        var EngagmentRatingPercentage: Double = 0.0

        val EngagmentRatingPercentageString = "%.1f%%".format(EngagmentRatingPercentage)
        EngagmentRatingValue!!.text = (EngagmentRatingPercentageString).toString()

        val EngagmentRatingProgressBar = view.findViewById(R.id.EngagmentRatingProgressBar) as ProgressBar
        EngagmentRatingProgressBar.setProgress(EngagmentRatingPercentage.toInt())



        val totalRemindersReceivedValue: TextView = view.findViewById(R.id.totalRemindersReceivedValue)
        totalRemindersReceivedValue!!.text = MyTimer.totalRemindersRecieved.toString()

        val totalTimeValue: TextView = view.findViewById(R.id.TimeView)
        totalTimeValue!!.text = MyTimer.ConvertInTimeStr(MyTimer.totalTime)
        val btnToT1S4: ImageButton = view.findViewById(R.id.btnToT1S4)
        btnToT1S4.setOnClickListener(View.OnClickListener {
            findNavController().navigate(MinigameT1S3FragmentDirections.actionFragmentMinigameT1S3ToFragmentMinigameT1S4())
        })
        return view
    }

}