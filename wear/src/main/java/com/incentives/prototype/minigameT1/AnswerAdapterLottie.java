package com.incentives.prototype.minigameT1;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.BoxInsetLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.incentives.prototype.ItemsListMenu;
import com.incentives.prototype.R;

import java.util.ArrayList;
import java.util.List;

public class AnswerAdapterLottie extends RecyclerView.Adapter<AnswerAdapterLottie.ViewHolder> {

    final private AdapterCallback callback;
    private List<ItemsListMenuLottie> mItems = new ArrayList<>();

    public AnswerAdapterLottie(List<ItemsListMenuLottie> _items, AdapterCallback _callback){
        this.mItems = _items;
        this.callback = _callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        ImageView imageView1;
        LottieAnimationView lottieView;
        BoxInsetLayout contenedor;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            contenedor = itemView.findViewById(R.id.contenedor);
            imageView1 = itemView.findViewById(R.id.imageView1);
            textView1 = itemView.findViewById(R.id.textView1);
            lottieView = itemView.findViewById(R.id.lottie_view);
        }
    } // class ViewHolder

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.answer_item_lottie, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    } // ListAdapter.ViewHolder

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i){
        ItemsListMenuLottie itemsList = mItems.get(i);
        viewHolder.imageView1.setImageResource(itemsList.getImageItem1());
        viewHolder.textView1.setText(itemsList.getNameItem());
        viewHolder.lottieView.setAnimation(itemsList.getLottieFile());
        viewHolder.lottieView.setRepeatCount(LottieDrawable.INFINITE);
        viewHolder.lottieView.setRepeatMode(LottieDrawable.RESTART); // Optional, RESTART is the default.
        viewHolder.lottieView.playAnimation();

        viewHolder.contenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.onItemClicked(v, i);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return mItems.size();
    }

    // Interfaz para la opción de tap de cada item
    public interface AdapterCallback {
        void onItemClicked(View v, int itemPosition);
    }

}

