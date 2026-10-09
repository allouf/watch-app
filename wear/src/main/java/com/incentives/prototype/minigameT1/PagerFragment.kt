package com.incentives.prototype.minigameT1


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.incentives.prototype.CustomViewPager
import com.incentives.prototype.EnumStateType
import com.incentives.prototype.NoteFile
import com.incentives.prototype.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.util.*


class PagerFragment : Fragment() {
    companion object {
        private const val SET_ITEM_DELAY = 300
    }
    //lateinit var minigameT1Activity:MinigameT1
    lateinit var pager: CustomViewPager
    lateinit var tabLayout: TabLayout

    var currentDisplayScreenStates = ArrayList<Boolean>()

    fun updateCurrentDisplayScreenStates(displayScreens: ArrayList<NoteFile>) {
        currentDisplayScreenStates.clear()
        currentDisplayScreenStates.addAll(Collections.nCopies(displayScreens.size, false))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        //minigameT1Activity = activity as MinigameT1
        MyTimer.ChangeCurrentState(EnumStateType.SHWNG_RMNDR_PAGER.ordinal)
        val result = inflater.inflate(com.incentives.prototype.R.layout.fragment_view_pager, container, false)

        //val rootLayout : ConstraintLayout = result.findViewById<ConstraintLayout>(R.id.rootview)

        pager = result.findViewById(com.incentives.prototype.R.id.pager)

        //val indicator = result.findViewById(com.incentives.prototype.R.id.indicator) as DotsIndicator

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                MyTimer.lastRemScreenId = position

                // To update the UI instantly, you can call pager.adapter?.notifyDataSetChanged()

                pager.adapter?.notifyDataSetChanged()
                //pager.postDelayed({ handleSetCurrentItem(position) }, SET_ITEM_DELAY.toLong())
            }


            override fun onPageScrollStateChanged(state: Int) {
            }

            private fun handleSetCurrentItem(position: Int) {

                //val lastPosition = pager.adapter!!.count - 1
                val lastPosition = if (MyTimer.lastRemPagesCount == 1) 0 else pager.adapter!!.count - 1
                if (position == 0) {
                    pager.setCurrentItem(lastPosition , false)
                } else if (position == lastPosition) {
                    pager.setCurrentItem(0, false)
                }
                MyTimer.lastRemScreenId = position
                //indicator.notifyDataSetChanged()
            }
        })
        pager.adapter = buildAdapter()
        //indicator.attachTo(pager)



        tabLayout = result.findViewById(com.incentives.prototype.R.id.tab_layout)
        tabLayout!!.setupWithViewPager(pager, true)

        pager.setCurrentItem(MyTimer.lastRemScreenId, false)

        tabLayout.visibility = View.GONE


        return result


        //pager.setCurrentItem(MyTimer.lastRemScreenId)

        //return result
    }

    private fun buildAdapter(): PagerAdapter {
        return SampleAdapter(requireActivity(), childFragmentManager)
    }
}