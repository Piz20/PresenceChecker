package com.ocr.firebaseoc.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ocr.firebaseoc.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTextView ;
    public ViewHolder(View itemView){
        super(itemView) ;
        titleTextView = itemView.findViewById(R.id.item_title) ;
    }
}
