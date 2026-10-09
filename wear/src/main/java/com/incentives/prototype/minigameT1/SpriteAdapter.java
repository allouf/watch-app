package com.incentives.prototype.minigameT1;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


//public class SampleAdapter extends FragmentPagerAdapter {
public class SpriteAdapter extends FragmentStatePagerAdapter {
    private int numberOfPages = 3;
    Context ctxt = null;
    public SpriteAdapter(Context ctxt, FragmentManager mgr, int pages) {
        super(mgr);
        this.ctxt = ctxt;
        this.numberOfPages = pages;
    }
    @Override
    public int getCount() {
        return (numberOfPages + 2);
    }

    @Override
    public Fragment getItem(final int position) {
        if (position == 0) {
            //return (EditorFragment.newInstance(numberOfPages - 1,numberOfPages));
            return (SpriteFragment.newInstance2(numberOfPages - 1, numberOfPages));
        } else if (position == numberOfPages + 1) {
            return (SpriteFragment.newInstance2(0, numberOfPages));
        } else {
            return (SpriteFragment.newInstance2(position - 1, numberOfPages));
        }
    }
    @Override
    public String getPageTitle(int position) {
        return (SpriteFragment.getTitle(ctxt, position));
    }
}