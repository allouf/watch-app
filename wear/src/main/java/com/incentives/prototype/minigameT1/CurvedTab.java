package com.incentives.prototype.minigameT1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class CurvedTab extends View {
    private static final int NO_OF_TABS = 4;
    private static final float RADIUS = 50;
    private Paint markerPaint;
    private double angleStep;

    public CurvedTab(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Configure our drawing materials
        markerPaint = new Paint();
        markerPaint.setColor(Color.BLUE);
    }

    public void setupWithViewPager(ViewPager viewPager, Boolean enableSwipe){
        // listening to swipe
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // handle UI changes based on page scrolling
            }

            @Override
            public void onPageSelected(int position) {
                // Handle new page - reflecting into dots being highlighted
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // handling UI based on scroll state
            }
        });

        // If your Custom View need to handle onClick or similar for deeplinks, or direct jumps, setup Listeners here
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        angleStep = Math.PI / (NO_OF_TABS - 1); // Semi-circle, not whole. hence, no "2 *"
        int width = MeasureSpec.getSize(widthMeasureSpec);

        // Set resultant height to compose a semi-circle
        int height = (int)(width * Math.sin(Math.PI / 2) / Math.sin(angleStep / 2));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas c) {
        float y = getMeasuredHeight();
        float centerX = getMeasuredWidth() / 2;

        for(int i=0; i<NO_OF_TABS; i++) {
            double angle = angleStep * i;
            float x = (float)(RADIUS * Math.cos(angle)) + centerX;
            c.drawCircle(x, y, 15f, markerPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch(e.getAction()) {
            /** Handle the logic if the semi-circular indicators were touched **/
        }

        return true;
    }
}
