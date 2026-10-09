package com.incentives.prototype;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.Arrays;
import java.util.List;

public class DailyStatActivity extends Activity {
    GameView gameView = null;
    List<String> viewsNames = Arrays.asList("Fun", "Creativity", "Curiosity","Fear");
    List<Integer> progBarColors = Arrays.asList(R.drawable.roundedprogressbar_yellow, R.drawable.roundedprogressbar_green, R.drawable.roundedprogressbar_red,R.drawable.roundedprogressbar_blue);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailystat);

        gameView = new GameView(this,"Boy");
        gameView.setIsInList(false);
        SurfaceView s = gameView;
        s.setLayoutParams(new ViewGroup.LayoutParams(450,450));
        s.setZOrderOnTop(true);
        s.willNotDraw();
        ViewGroup mLayout   = findViewById(R.id.layout_content);
        mLayout.addView(s);

        Typeface roboto = Typeface.create("sans-serif", Typeface.NORMAL);
        for(int i=0; i < viewsNames.size(); i++){
            TextView textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(roboto); // Set the typeface to Roboto
            textView.setText(viewsNames.get(i));
            textView.setTextSize(22);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.setMargins(0, 0, 0, 20); // Add margin to the bottom
            textView.setLayoutParams(textParams);
            textView.setTextColor(Color.WHITE);

            mLayout.addView(textView);

            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), progBarColors.get(i), null);
            progressBar.setProgressDrawable(drawable);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(269,26);
            params.setMargins(0, 0, 0, 20); // Add margin to the top
            progressBar.setLayoutParams(params);
            progressBar.setProgress((i+1)*10);
            mLayout.addView(progressBar);
        }

        if (this.getResources().getConfiguration().isScreenRound() == true) {
            int inset = (int) (0.1f  * Resources.getSystem().getDisplayMetrics().widthPixels);
            LinearLayout layout_content = findViewById(R.id.layout_content);
            layout_content.setPadding(inset, inset, inset, inset);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
    }
}