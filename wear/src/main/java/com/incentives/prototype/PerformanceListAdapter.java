package com.incentives.prototype;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.BoxInsetLayout;

import java.util.ArrayList;
import java.util.List;

public class PerformanceListAdapter extends RecyclerView.Adapter<PerformanceListAdapter.ViewHolder> {

    final private AdapterCallback callback;
    private List<ItemsList> mItems = new ArrayList<>();

    public PerformanceListAdapter(List<ItemsList> _items, AdapterCallback _callback){
        this.mItems = _items;
        this.callback = _callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        BoxInsetLayout contenedor;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            contenedor = itemView.findViewById(R.id.contenedor);
            //imageView = itemView.findViewById(R.id.imageView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
        }
    } // class ViewHolder

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.performance_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    } // ListAdapter.ViewHolder

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i){
        ItemsList itemsList = mItems.get(i);
        //viewHolder.imageView.setImageResource(itemsList.getImageItem());
        viewHolder.textView1.setText(itemsList.getNameItem());
        viewHolder.textView2.setText(itemsList.getDescriptionItem());
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

