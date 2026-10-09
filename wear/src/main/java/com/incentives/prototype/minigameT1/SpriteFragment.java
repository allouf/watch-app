package com.incentives.prototype.minigameT1;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.incentives.prototype.EnumStateType;
import com.incentives.prototype.GameView;
import com.incentives.prototype.R;
import com.incentives.prototype.Utilities;

public class SpriteFragment extends Fragment {
    public GameView gameView = null;
    private static final String KEY_POSITION = "position";
    private static final String KEY_PAGES = "pages";
    static SpriteFragment newInstance2(int position, int numOfPages) {
        SpriteFragment frag = new SpriteFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        args.putInt(KEY_PAGES, numOfPages);
        frag.setArguments(args);
        return (frag);
    }
    /*static SpriteFragment newInstance(int position) {
        SpriteFragment frag = new SpriteFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        //args.putInt(KEY_PAGES, numOfPages);
        frag.setArguments(args);
        return (frag);
    }*/
    static String getTitle(Context ctxt, int position) {
        return (String.format("Editor #%d", position + 1));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.sprite, container, false);
        int position = getArguments().getInt(KEY_POSITION, -1);
        int pages = getArguments().getInt(KEY_PAGES, -1);
        int id = position % Utilities.Companion.getListOfSpritesNames().length;
        gameView = new GameView(getActivity(),Utilities.Companion.getListOfSpritesNames()[id]);
        gameView.setIsInList(true);
        SurfaceView s = gameView;
        s.setLayoutParams(new ViewGroup.LayoutParams(350,350));
        s.setZOrderOnTop(true);
        s.willNotDraw();
        ViewGroup mLayout   = result.findViewById(R.id.layout_content);
        mLayout.addView(s);

        //TextView spriteName = (TextView) result.findViewById(R.id.spriteName);
        //spriteName.setText(Utilities.Companion.getListOfSpritesNames()[id]);

        if (this.getContext().getResources().getConfiguration().isScreenRound() == true) {
            int inset = (int) (0.1f  * Resources.getSystem().getDisplayMetrics().widthPixels);
            LinearLayout layout_content = result.findViewById(R.id.layout_content);
            layout_content.setPadding(inset, inset, inset, inset);
        }

        return (result);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Diz ao método retomar gameView para executar
        gameView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Indica para o método que pausa gameView executar
        gameView.pause();
    }
}