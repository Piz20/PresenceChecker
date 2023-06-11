package com.ocr.firebaseoc.Adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.models.Document;

import java.util.List;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final List<Document> documents;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView reasonTextView;


        public LinearLayout linearLayout ;
        public MyViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            reasonTextView = itemView.findViewById(R.id.reason_text_view);
            linearLayout = itemView.findViewById(R.id.details_linear_layout) ;
        }
    }

    public MyAdapter(List<Document> documents) {
        this.documents = documents;
    }
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_details_statistics_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Timestamp date = documents.get(position).getDate();
        String reason = documents.get(position).getReason();
        String dateString = DateFormat.format("dd/MM/yyyy", date.toDate()).toString();
        holder.dateTextView.setText(dateString);
        holder.reasonTextView.setText(reason);
        if (!Objects.equals(reason, "Presence") && !Objects.equals(reason, "Présence")){

            holder.linearLayout.setBackgroundColor(Color.parseColor("#FF5252"));
        }
        else {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#6BB18C"));
        }
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

}