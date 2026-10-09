package com.incentives.prototype.minigameT1

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import com.incentives.prototype.*
import com.incentives.prototype.minigameT1.MyTimer.Companion.GtotalCompleted
import com.incentives.prototype.minigameT1.MyTimer.Companion.GtotalRun
import com.incentives.prototype.minigameT1.MyTimer.Companion.GtotalStopped
import com.incentives.prototype.minigameT1.MyTimer.Companion.getLastRemContent
import com.incentives.prototype.minigameT1.MyTimer.Companion.totalAlgos
import java.util.ArrayList

class MenuFragment : Fragment() {
    lateinit var items: ArrayList<ItemsListMenu?>

    private lateinit var arrowBtn: ImageButton

    lateinit var minigameT1Activity:MinigameT1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        minigameT1Activity = activity as MinigameT1
        MyTimer.ChangeCurrentState(EnumStateType.MENU_PAGER.ordinal)


        val view:View =  inflater.inflate(R.layout.fragment_minigame_t1_menu, container, false)

        items = ArrayList()
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(minigameT1Activity)
        layoutManager.orientation = WearableLinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        items.add(ItemsListMenu(R.drawable.aichip, "AI", R.drawable.robot))
        items.add(ItemsListMenu(R.drawable.creativeidea, "Answer", R.drawable.mindmapping))
        items.add(ItemsListMenu(R.drawable.languagetranslate2, "Translate", R.drawable.languagetranslate))

        val listAdapter = MenuPagerAdapter(
            items
        ) { v, itemPosition ->
            if(itemPosition==2){
                val animationDialog = AnimationDialog()
                animationDialog.show(parentFragmentManager,"AnimationDialog")
                //val intent = Intent(minigameT1Activity, IntermediateActivity::class.java)
               // startActivity(intent)
            }
            else if(itemPosition==1){
                val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    currentNavHost.navController.navigate(R.id.answerMenu)
                }

                /*val animationDialog = AnimationDialog()
                animationDialog.show(parentFragmentManager, "AnimationDialog")*/

            }
            else if(itemPosition==0){

            val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null) {
            currentNavHost.navController.navigate(R.id.generateMenu)
            }
                /*MyTimer.updateNoteFileBooleanState(MyTimer.lastRemScreenId-1, true)
                val currentNavHost = (MyTimer.context as MinigameT1).supportFragmentManager.primaryNavigationFragment as NavHostFragment
                if(currentNavHost != null) {
                    currentNavHost.navController.navigate(R.id.ticAnimation)
                }*/
            }
            else if(itemPosition==3){
            }
        }

        recyclerView.adapter = listAdapter

        arrowBtn = view.findViewById(R.id.backBtn)

        arrowBtn.setOnClickListener {
            val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null) {
                currentNavHost.navController.navigate(R.id.pagerFragment)
            }
        }

        return view
    }
}