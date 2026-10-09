package com.incentives.prototype.minigameT1;

import android.view.GestureDetector;
import android.view.MotionEvent;

class DoubleTapGestureDetector extends GestureDetector.SimpleOnGestureListener {

    // Declare an interface to implement
    public interface DoubleTapListener {
        void onDoubleTap();
    }

    private DoubleTapListener mListener;

    public DoubleTapGestureDetector(DoubleTapListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mListener.onDoubleTap();
        return true;
    }
}
