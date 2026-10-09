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

import com.incentives.prototype.ItemsListMenu;
import com.incentives.prototype.R;

import java.util.ArrayList;
import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    final private AdapterCallback callback;
    private List<ItemsListMenu> mItems = new ArrayList<>();

    public AnswerAdapter(List<ItemsListMenu> _items, AdapterCallback _callback){
        this.mItems = _items;
        this.callback = _callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        ImageView imageView1;
        ImageView imageView2;
        BoxInsetLayout contenedor;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            contenedor = itemView.findViewById(R.id.contenedor);
            imageView1 = itemView.findViewById(R.id.imageView1);
            textView1 = itemView.findViewById(R.id.textView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
        }
    } // class ViewHolder

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.answer_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    } // ListAdapter.ViewHolder

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i){
        ItemsListMenu itemsList = mItems.get(i);
        viewHolder.imageView1.setImageResource(itemsList.getImageItem1());
        viewHolder.imageView2.setImageResource(itemsList.getImageItem2());
        viewHolder.textView1.setText(itemsList.getNameItem());
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

