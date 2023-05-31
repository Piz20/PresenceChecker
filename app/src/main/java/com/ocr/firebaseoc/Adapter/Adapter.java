package com.ocr.firebaseoc.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ocr.firebaseoc.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private List<String> items ;

    public Adapter(List<String> items){
        this.items = items ;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_details_statistics_item,parent,false) ;

        return new ViewHolder(itemView) ;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = items.get(position);

        holder.titleTextView.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
