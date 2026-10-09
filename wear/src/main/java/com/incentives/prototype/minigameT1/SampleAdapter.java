package com.incentives.prototype.minigameT1;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


//public class SampleAdapter extends FragmentPagerAdapter {
public class SampleAdapter extends FragmentStatePagerAdapter {
    private int numberOfPages = 3;
    Context ctxt = null;
    public SampleAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt = ctxt;
        this.numberOfPages = MyTimer.Companion.getLastRemPagesCount();
        //this.numberOfPages = pages;
    }
    @Override
    public int getCount() {

        /*if(numberOfPages > 1) {
            return (numberOfPages + 2);
        } else {
            return numberOfPages;
        }*/
        return (numberOfPages );
    }

    @Override
    public Fragment getItem(final int position) {
        /*if (position == getCount() - 1) { // if it's the last position
            return EditorLastPage.Companion.newInstance(position); // return your custom LastPageFragment
        } else {
            return EditorFragment.Companion.newInstance(position); // return your regular Fragment
        }*/

        return EditorFragment.Companion.newInstance(position);
        /*if(numberOfPages > 1) {
            if (position == 0) {
                return (EditorFragment.Companion.newInstance(numberOfPages - 1));
            } else if (position == numberOfPages + 1) {
                return (EditorFragment.Companion.newInstance(0));
            } else {
                return (EditorFragment.Companion.newInstance(position - 1));
            }
        } else {
            return (EditorFragment.Companion.newInstance(0));
        }*/
    }

    /*@Override
    public String getPageTitle(int position) {
        return (EditorFragment. .Companion. getTitle(ctxt, position));
    }*/
}