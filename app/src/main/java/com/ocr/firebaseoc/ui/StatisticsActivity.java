package com.ocr.firebaseoc.ui;


import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.manager.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatisticsActivity extends AppCompatActivity {

    private static final String COLLECTION_NAME = "users" ;
    private final UserManager userManager = UserManager.getInstance();
    PieChart pieChart ;

    ProgressBar progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        pieChart = findViewById(R.id.statistics_pie_chart) ;
        progressBar = findViewById(R.id.progress_bar) ;
        setPieChart();

    }

    private void setPieChart(){
        FirebaseUser user = getCurrentUser();
        String uid = user.getUid() ;
        progressBar.setVisibility(View.VISIBLE);
        //Ici nous allons recuperer les donnees de nos absences et de nso presences et mettre notre PieChart à jour avec .
        Query query1 = this.getUsersCollection().document(uid).collection("absences") ;

        Query query2 = this.getUsersCollection().document(uid).collection("presences") ;

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(query1.get(), query2.get());
        allTasks.addOnSuccessListener(querySnapshots -> {
            int countMaladie = 0;
            int countFormation = 0;
            int countMaternite = 0  ;
            int countAutre = 0;
            int countPresences = querySnapshots.get(1).size();

            for (QueryDocumentSnapshot documentSnapshot : querySnapshots.get(0)) {
                if (Objects.equals(documentSnapshot.getString("reason"), "Maladie")) {
                    countMaladie++;
                } else if (Objects.equals(documentSnapshot.getString("reason"), "Formation")) {
                    countFormation++;
                }else if (Objects.equals(documentSnapshot.getString("reason"), "Maternité")) {
                    countMaternite++;
                }
                else if (Objects.equals(documentSnapshot.getString("reason"), "Autre")) {
                    countAutre++;
                }
            }

            progressBar.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            ArrayList<PieEntry> mystats = new ArrayList<>() ;

            mystats.add(new PieEntry(countMaladie,"Sickness")) ;
            mystats.add(new PieEntry(countFormation,"Course")) ;
            mystats.add(new PieEntry(countMaternite,"Maternity")) ;
            mystats.add(new PieEntry(countAutre,"Other")) ;
            mystats.add(new PieEntry(countPresences,"Presences")) ;

            PieDataSet pieDataSet = new PieDataSet(mystats,"") ;
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextColor(Color.BLACK);
            pieDataSet.setValueTextSize(15f);
            pieChart.getLegend().setTextSize(15f);


            PieData pieData = new PieData(pieDataSet) ;

            pieChart.setData(pieData);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("my stats");
            pieChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
            pieChart.animate() ;
        });
    }
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

}

