package com.ocr.firebaseoc.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ocr.firebaseoc.Adapter.Adapter;
import com.ocr.firebaseoc.R;

import java.util.ArrayList;
import java.util.List;

public class DetailsStatisticsActivity extends AppCompatActivity {
    List<String> liste = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_statistics);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        liste.add("Pisani" );
        liste.add("dsdsdsds");
        liste.add("fddefdfdef") ;
        Adapter adapter = new Adapter(liste);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
