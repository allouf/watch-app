package com.incentives.prototype.minigameT1
import android.content.Context
import android.content.res.Resources
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.incentives.prototype.*
import android.widget.FrameLayout
import android.widget.ImageButton
import com.airbnb.lottie.LottieDrawable
import com.incentives.prototype.minigameT1.EditorFragment.Companion.newInstance


class EditorLastPage : Fragment() {

    private lateinit var viewPager: CustomViewPager
    private var position = 0

    private lateinit var minigameT1Activity: MinigameT1
    private lateinit var slideAnimationView: LottieAnimationView
    private lateinit var toHint: ImageButton

    companion object {
        private const val KEY_POSITION = "position"

        fun newInstance(position: Int): EditorLastPage {
            val frag = EditorLastPage()
            val args = Bundle()
            args.putInt(KEY_POSITION, position)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val result = inflater.inflate(R.layout.editorlast, container, false)
        minigameT1Activity = activity as MinigameT1
        slideAnimationView = result.findViewById(R.id.slideAnimationView)
        val editor = result.findViewById<TextView>(R.id.editor)
        position = arguments?.getInt(KEY_POSITION, 0) ?: 0

        toHint = result.findViewById<ImageButton>(R.id.hintButton)
        setupToHintButton()
        toHint.setOnClickListener { view ->
            Navigation.findNavController(view)
                .navigate(R.id.action_pagerFragment_to_hint)
            /*MyTimer.generateResponse("generate a hint for this: " +
                    MyTimer.getLastRemContent(MyTimer.lastRemScreenId-1)
                //+rand
                ,
                MyTimer.pagesSysMsg,minigameT1Activity,"page")*/

            /*MyTimer.generateResponse(
                "Regenerate a hint for: " +
                        MyTimer.getLastRemContent(MyTimer.lastRemScreenId),
                MyTimer.pagesSysMsg, minigameT1Activity, "page"
            )
            hint.visibility = View.VISIBLE
            ticAnimationView.visibility = View.VISIBLE
            minigameT1Activity.isHisnt = false
            someFunction()*/
        }
        /*if (minigameT1Activity.isHisnt) {
            hint.visibility = View.VISIBLE
            hint.text = ""
            hint.movementMethod = ScrollingMovementMethod()
            ticAnimationView.visibility = View.VISIBLE
            regenerate.visibility = View.VISIBLE
            minigameT1Activity.isHisnt = false
            someFunction()
        }
        else {
            hint.visibility = View.INVISIBLE
            regenerate.visibility = View.INVISIBLE
            ticAnimationView.visibility = View.GONE
        }*/
        editor.text = MyTimer.getLastRemContent(position)
        editor.movementMethod = ScrollingMovementMethod()

        val next = result.findViewById<TextView>(R.id.next)
        viewPager = activity?.findViewById(R.id.pager) ?: CustomViewPager(requireContext())

        if (position == MyTimer.lastRemPagesCount - 1) {
            next.text = "Finish"
            editor.text = "asd asd asd asadsd asdasfas asdasaf afafasf afafaf afasfaf afafaf afafaf afasfaf afasfa afaffa afasfaf" +
                    "afasfasf afsasf afafasf"
            next.setOnClickListener { view ->
                MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                Navigation.findNavController(view)
                    .navigate(R.id.action_pagerFragment_to_minigameT1FPre)
            }
        }
        else {
            next.visibility = View.INVISIBLE
        }

        if (position == 0  && !minigameT1Activity.isSwipeAnimRendered && MyTimer.lastRemPagesCount!=1) {
            minigameT1Activity.isSwipeAnimRendered = true
            playSlideLottieAnimation()
        }

        val doubleTapGestureDetector = GestureDetector(
            requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    onDoubleTap()
                    return true
                }
            }
        )
        var scrollViewEditor = result.findViewById<CustomScrollView>(R.id.scrollViewEditor)
        scrollViewEditor.setGestureDetector(doubleTapGestureDetector)


        //val rootContainer = result.findViewById<ConstraintLayout>(R.id.frameLayout_content)
        editor.setOnTouchListener { v, event ->
            doubleTapGestureDetector.onTouchEvent(event)
        }

        val layout_content = result.findViewById<ConstraintLayout>(R.id.frameLayout_content)
        if (context?.resources?.configuration?.isScreenRound == true) {
            val inset: Int = (0.1f * Resources.getSystem().displayMetrics.widthPixels).toInt()
            if(toHint.visibility==View.VISIBLE)
                editor.setPadding(inset, 0, inset, inset)
            else
                editor.setPadding(inset, inset, inset, inset)
        }
        layout_content.setOnClickListener(View.OnClickListener {
            slideAnimationView.cancelAnimation()
            slideAnimationView.visibility = View.GONE
        })
        return result
    }

    private fun playSlideLottieAnimation() {
        slideAnimationView.visibility = View.VISIBLE
        slideAnimationView.repeatCount = LottieDrawable.INFINITE
        slideAnimationView.playAnimation()

        // Set 5 seconds duration for the Lottie animation:
        val millis = 5000L // Milliseconds for 5 seconds

        slideAnimationView.postDelayed({
            slideAnimationView.cancelAnimation()
            slideAnimationView.visibility = View.GONE
        }, millis)
    }

    private fun setupToHintButton() {
        if (MyTimer.currentDisplayScreenStates[position].isActive) {
            toHint.visibility = View.VISIBLE
        } else {
            toHint.visibility = View.GONE
        }
    }

    // DoubleTap Callback
    private fun onDoubleTap() {
        if (position == MyTimer.lastRemPagesCount ) {
            MyTimer.lastRemScreenId = MyTimer.lastRemPagesCount
        } else {
            MyTimer.lastRemScreenId = position
        }
        //MyTimer.lastRemScreenId = position
        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
        MyTimer.ChangeCurrentState(EnumStateType.MENU_PAGER.ordinal)
        viewPager?.let {
            Navigation.findNavController(it).navigate(R.id.action_pagerFragment_to_menu)
        }
    }
}