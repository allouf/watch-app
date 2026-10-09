package com.incentives.prototype;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;

import com.incentives.prototype.minigameT1.MyTimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PerformanceStatActivity extends Activity {
    private ArrayList<ItemsList> items  = new ArrayList();
    //List<String> viewsNames = Arrays.asList("Experience             lvl 3", "Learning             lvl 88", "Expressing             lvl 10","Creating             lvl 56","Exploring             lvl 33");
    List<String> viewsNames = Arrays.asList("Experience", "Learning", "Expressing","Creating","Exploring");
    List<String> viewsLevels = Arrays.asList("lvl 3", "lvl 88", "lvl 10","lvl 56","lvl 33");

    List<Integer> progBarColors = Arrays.asList(R.drawable.rect_progress_blue, R.drawable.rect_progress_green, R.drawable.rect_progress_red,R.drawable.rect_progress_yellow,R.drawable.rect_progress_lightblue);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.performancestat);

        ViewGroup mLayout   = findViewById(R.id.layout_content);


        Typeface roboto = Typeface.create("sans-serif", Typeface.NORMAL);
        int listSize = viewsNames.size();
        for(int i=0; i < listSize; i++){
            TextView textViewName = new TextView(this);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setTypeface(roboto); // Set the typeface to Roboto
            textViewName.setText(viewsNames.get(i));
            textViewName.setTextSize(15);
            LinearLayout.LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // set weight to 1
            nameLayoutParams.gravity = Gravity.START; // align to the start/left
            textViewName.setLayoutParams(nameLayoutParams);

            TextView textViewLevel = new TextView(this);
            textViewLevel.setTextColor(Color.WHITE);
            textViewLevel.setTypeface(roboto); // Set the typeface to Roboto
            textViewLevel.setText(viewsLevels.get(i));
            textViewLevel.setTextSize(13);
            LinearLayout.LayoutParams levelLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0); // set weight to 0
            levelLayoutParams.gravity = Gravity.END; // align to the end/right
            textViewLevel.setLayoutParams(levelLayoutParams);

            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), progBarColors.get(i), null);
            progressBar.setProgressDrawable(drawable);
            LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // use MATCH_PARENT for the width
                    LinearLayout.LayoutParams.WRAP_CONTENT // use WRAP_CONTENT for the height
            );
            if(i != listSize-1)
                progressBarParams.setMargins(0, 0, 0, 30); // add a 20px margin at the top
            progressBar.setLayoutParams(progressBarParams);
            progressBar.setProgress((i+1)*10);

            LinearLayout horizontalLayout = new LinearLayout(this);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

            horizontalLayout.addView(textViewName);

            LinearLayout.LayoutParams levelLayoutparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            levelLayoutparams.gravity = Gravity.END;

            horizontalLayout.addView(textViewLevel, levelLayoutparams);

            LinearLayout verticalLayout = new LinearLayout(this);
            verticalLayout.setOrientation(LinearLayout.VERTICAL);
            verticalLayout.addView(horizontalLayout);
            verticalLayout.addView(progressBar);
            mLayout.addView(verticalLayout);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(WearableLinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        items.add(new ItemsList(R.drawable.equalizer, "Total Algorithms", String.valueOf(MyTimer.Companion.getTotalAlgos())));
        items.add(new ItemsList(R.drawable.settings1, "Total Run", String.valueOf(MyTimer.Companion.getGtotalRun())));
        items.add(new ItemsList(R.drawable.settings2, "Total Completed", String.valueOf(MyTimer.Companion.getGtotalCompleted())));
        items.add(new ItemsList(R.drawable.settings2, "Total Stopped", String.valueOf(MyTimer.Companion.getGtotalStopped())));
        items.add(new ItemsList(R.drawable.settings2, "Total Ideas Received", "564"));
        items.add(new ItemsList(R.drawable.settings2, "Total Ideas Captured", "864"));
        PerformanceListAdapter listAdapter = new PerformanceListAdapter(items, new PerformanceListAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(View v, int itemPosition) {

            }
        });

        recyclerView.setAdapter(listAdapter);

        TextView totalTimeValue  = findViewById(R.id.TimeView);
        totalTimeValue.setText(MyTimer.Companion.ConvertInTimeStr(MyTimer.Companion.getGtotalTime()));

        if (this.getResources().getConfiguration().isScreenRound() == true) {
            int inset = (int) (0.1f  * Resources.getSystem().getDisplayMetrics().widthPixels);
            LinearLayout layout_content = findViewById(R.id.layout_content);
            layout_content.setPadding(inset, inset, inset, inset);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}