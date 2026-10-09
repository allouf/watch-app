package com.incentives.prototype.minigameT1

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.wear.widget.BoxInsetLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.incentives.prototype.*
import okhttp3.*
import java.util.Random

class MinigameT1R1 : Fragment() {

    lateinit var minigameT1Activity:MinigameT1

    //private lateinit var model: ItemListSharedVM
    lateinit var items1: ArrayList<ItemsListMenuLottie?>
    lateinit var items2: ArrayList<ItemsListMenuLottie?>
    private lateinit var lottieView: LottieAnimationView

    var txtView:TextView? = null
    var BotTxtView:TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        minigameT1Activity = activity as MinigameT1
        MyTimer.ChangeCurrentState(EnumStateType.SEC_TMR_RNG_SHWNG_RMNDR.ordinal)

        val isCauseReminder:Boolean= requireArguments().getBoolean("causeReminder",true)

        if(isCauseReminder!= null && isCauseReminder) {
            MyTimer.causeReminder()
            MyTimer.lastRemId = MyTimer.getNextReminderId()
            MyTimer.lastRemScreenId = 0;
        }
        MyTimer.updateCurrentDisplayScreenStates(MyTimer.remindersList[MyTimer.lastRemId].DisplayScreens)
        val layoutID:Int = Utilities.GetReminderLayoutGivenIndex(MyTimer.lastRemId)
        val view:View = inflater.inflate(layoutID, container, false)

        //model = ViewModelProvider(this.requireActivity()).get(ItemListSharedVM::class.java)

        /*val imgsViews = arrayOf(R.id.imageView1,R.id.imageView2,R.id.imageView3,R.id.imageView4,R.id.imageView5
        ,R.id.imageView6,R.id.imageView7,R.id.imageView8,R.id.imageView9,R.id.imageView10,R.id.imageView11)*/

        //val imgs = arrayOf(R.drawable.s1,R.drawable.s2,R.drawable.s3,R.drawable.s4,R.drawable.s5,
            //R.drawable.s6,R.drawable.s7,R.drawable.s8,R.drawable.s9,R.drawable.s10,R.drawable.s10)
        //var imagView:ImageView? = null
        //val randomIndex2 = Random.nextInt(imgs.size)

        when(layoutID){
            R.layout.fragment_minigame_t1_r11->
            {
                //imagView=view.findViewById(imgsViews[10])
                txtView=view.findViewById(R.id.txtViewDesc11)
                BotTxtView=view.findViewById(R.id.txtViewBot11)
            }
            R.layout.fragment_minigame_t1_r10->{
                //imagView=view.findViewById(imgsViews[9])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc10)
                BotTxtView=view.findViewById(R.id.txtViewBot10)
            }
            R.layout.fragment_minigame_t1_r9->{
                //imagView=view.findViewById(imgsViews[8])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc9)
                BotTxtView=view.findViewById(R.id.txtViewBot9)
            }
            R.layout.fragment_minigame_t1_r8->{
                //imagView=view.findViewById(imgsViews[7])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc8)
                BotTxtView=view.findViewById(R.id.txtViewBot8)
            }
            R.layout.fragment_minigame_t1_r7->{
                //imagView=view.findViewById(imgsViews[6])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc7)
                BotTxtView=view.findViewById(R.id.txtViewBot7)
            }
            R.layout.fragment_minigame_t1_r6->{
                //imagView=view.findViewById(imgsViews[5])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc6)
                BotTxtView=view.findViewById(R.id.txtViewBot6)
            }
            R.layout.fragment_minigame_t1_r5->{
                //imagView=view.findViewById(imgsViews[4])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc5)
                BotTxtView=view.findViewById(R.id.txtViewBot5)
            }
            R.layout.fragment_minigame_t1_r4->{
                //imagView=view.findViewById(imgsViews[3])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc4)
                BotTxtView=view.findViewById(R.id.txtViewBot4)
            }
            R.layout.fragment_minigame_t1_r3->{
                //imagView=view.findViewById(imgsViews[2])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc3)
                BotTxtView=view.findViewById(R.id.txtViewBot3)
            }
            R.layout.fragment_minigame_t1_r2->{
                //imagView=view.findViewById(imgsViews[1])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc2)
                BotTxtView=view.findViewById(R.id.txtViewBot2)
            }
            R.layout.fragment_minigame_t1_r1->{
                //imagView=view.findViewById(imgsViews[0])
                //imagView.setImageResource(imgs[MyTimer.lastRemId])
                txtView=view.findViewById(R.id.txtViewDesc1)
                BotTxtView=view.findViewById(R.id.txtViewBot1)
            }

        }
        /*model.itemsList1.observe(viewLifecycleOwner, Observer { items1 ->
    model.itemsList2.observe(viewLifecycleOwner, Observer { items2 ->

        val newList = ArrayList<ItemsListMenu>()
        newList.addAll(items1)
        newList.addAll(items2)

        val random = kotlin.random.Random
        val randId = random.nextInt(newList.size)  // Obtain a random index from the list

        // Obtain the ItemsListMenu object from the randomly chosen position in the list
        val randomItem = newList[randId]

        // Choose the image randomly from imageItem1 or imageItem2
        val chosenImg = randomItem.getImageItem2()

        imagView?.setImageResource(chosenImg)
        txtView!!.text = randomItem.nameItem


    })
})*/
        items1 = ArrayList()
        items2 = ArrayList()
        items1.add(ItemsListMenuLottie(R.drawable.plus1, "Thought", "Tetrahedron.json"))
        items1.add(ItemsListMenuLottie(R.drawable.plus1, "Emotion", "Cube.json"))
        items1.add(ItemsListMenuLottie(R.drawable.plus1, "Imagination", "Octahedron.json"))
        items1.add(ItemsListMenuLottie(R.drawable.plus1, "Action", "Icosahedron.json"))
        items1.add(ItemsListMenuLottie(R.drawable.plus1, "Belief", "Cuboctahedron.json"))

        items2.add(ItemsListMenuLottie(R.drawable.plus1, "Dreams", "dreams.json"))
        items2.add(ItemsListMenuLottie(R.drawable.plus1, "Goals", "goals.json"))
        items2.add(ItemsListMenuLottie(R.drawable.plus1, "Rules", "rules.json"))
        items2.add(ItemsListMenuLottie(R.drawable.plus1, "Memories", "memories.json"))
        items2.add(ItemsListMenuLottie(R.drawable.plus1, "Questions", "questions.json"))
        items2.add(ItemsListMenuLottie(R.drawable.plus1, "Dangers", "dangers.json"))
        items2.add(ItemsListMenuLottie(R.drawable.plus1, "Desires", "desires.json"))

        val combinedItems = ArrayList<ItemsListMenuLottie?>()
        combinedItems.addAll(items1)
        combinedItems.addAll(items2)

        val randomIndex = Random().nextInt(combinedItems.size)

        val randomItem = combinedItems[randomIndex]

        lottieView = view.findViewById(R.id.lottie_view)

        lottieView.setAnimation(randomItem!!.lottieFile)
        lottieView.setRepeatCount(LottieDrawable.INFINITE)
        if(randomItem!!.lottieFile.equals("dreams.json") ||
            randomItem!!.lottieFile.equals("goals.json")){
            lottieView.scaleX = 1.0f // New lottie scale
            lottieView.scaleY = 1.0f // New lottie scale
        }
        if(randomItem!!.lottieFile.equals("questions.json")){
            lottieView.scaleX = 1.7f // New lottie scale
            lottieView.scaleY = 1.7f // New lottie scale
        }

        //lottieView.scaleType =  // New lottie scale
        lottieView.setRepeatMode(LottieDrawable.REVERSE) // Optional, RESTART is the default.
        lottieView.playAnimation()

        txtView!!.text = randomItem.nameItem



        //txtView!!.text = MyTimer.titleAnswer

        //BotTxtView!!.text = MyTimer.storyAnswer
        BotTxtView!!.visibility = View.GONE

        /*val btnToPager: Button = view.findViewById(R.id.btnToPager)
        btnToPager.setOnClickListener(View.OnClickListener {
            MyTimer.isSwipedBack = false
            val currentNavHost = (MyTimer.context as MinigameT1).supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null) {
                MyTimer.getLastRemPages()
                MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)

                currentNavHost.navController.navigate(R.id.action_minigameT1R1_to_pagerFragment)
            }
        })*/
        val layoutContent: LinearLayout = view.findViewById(R.id.linearLayoutT1r)
        layoutContent.setOnClickListener {
            MyTimer.isSwipedBack = false
            val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null) {
                MyTimer.getLastRemPages()
                MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)

                currentNavHost.navController.navigate(R.id.action_minigameT1R1_to_pagerFragment)
            }
        }


        /*val btnReminderOK: Button = view.findViewById(R.id.btnReminderOK)
        btnReminderOK.setOnClickListener(View.OnClickListener {
            MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)//Will set it up in the feedback pre screen
            findNavController().navigate(MinigameT1R1Directions.actionMinigameT1R1ToPagerFragment())
        })*/
        if (this.context?.resources?.configuration?.isScreenRound == true) {
            val inset = (FACTOR * Resources.getSystem().displayMetrics.widthPixels).toInt()
            val layout_content: BoxInsetLayout = view.findViewById(R.id.layout_content) as BoxInsetLayout
            layout_content.setPadding(inset, inset, inset, inset)
        }
        return view
    }

    companion object {
        private const val FACTOR = 0.1f // c = a * sqrt(2)
        //private const val FACTOR = 0.146467f // c = a * sqrt(2)
    }
}