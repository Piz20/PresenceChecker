package com.ocr.firebaseoc.ui;


import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.manager.UserManager;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private static final String COLLECTION_NAME = "users" ;
    private final UserManager userManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        PieChart pieChart = findViewById(R.id.statistics_pie_chart) ;
        ArrayList<PieEntry> mystats = new ArrayList<>() ;
        mystats.add(new PieEntry(508,"Presences")) ;
        mystats.add(new PieEntry(600,"Sickness")) ;
        mystats.add(new PieEntry(750,"Maternity")) ;
        mystats.add(new PieEntry(600,"Course")) ;
        mystats.add(new PieEntry(670,"Other")) ;

        PieDataSet pieDataSet = new PieDataSet(mystats,"") ;
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(15f);
        pieChart.getLegend().setTextSize(15f);


        PieData pieData = new PieData(pieDataSet) ;

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("my stats");
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.animate() ;
        System.out.println(countDocuments("Sickness","absences")+"*******************************************") ;
    }
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
    private int countDocuments(String reason,String SUB_COLLECTION_NAME) {
        FirebaseUser user = getCurrentUser();
        String uid = user.getUid() ;

        Task<QuerySnapshot> task = this.getUsersCollection().document(uid).collection(SUB_COLLECTION_NAME)
                .whereEqualTo("reason", reason)
                .get();
        int count = 1;
        try {
            count = task.getResult().size();
        } catch (Exception e) {
            Log.d(TAG, "Error getting documents: ", e);
        }
        return count;
    }

}

