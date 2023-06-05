package com.ocr.firebaseoc.ui;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
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
    AnyChartView anyChartView ;
    ImageView imageView ;
    ProgressBar progressBar ;
    Pie pie ;

    Button ButtonDetailsStatistics ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        anyChartView = findViewById(R.id.statistics_pie_chart) ;
        progressBar = findViewById(R.id.progress_bar) ;
        imageView = findViewById(R.id.imageView_piechart) ;
        ButtonDetailsStatistics = findViewById(R.id.button_statistics_details) ;
        ButtonDetailsStatistics.setVisibility(View.GONE);
        pie = AnyChart.pie();
        setPieChart();
       setupListerners();
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
            anyChartView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            ButtonDetailsStatistics.setVisibility(View.VISIBLE);
            ArrayList<DataEntry> pieData = new ArrayList<>() ;

            pieData.add(new ValueDataEntry(getString(R.string.presences),countPresences)) ;
            pieData.add(new ValueDataEntry(getString(R.string.reason_sickness), countMaladie)) ;
            pieData.add(new ValueDataEntry(getString(R.string.reason_course),countFormation));
            pieData.add(new ValueDataEntry(getString(R.string.reason_maternity),countMaternite)) ;
            pieData.add(new ValueDataEntry(getString(R.string.reason_other),countAutre)) ;


            pie.data(pieData) ;

            pie.title(getString(R.string.toolbar_title_statistics_activity)) ;
            pie.legend().enabled(true);
            pie.legend().position("bottom");
            pie.legend().padding(10d, 10d, 10d, 10d);
            pie.animation() ;

            anyChartView.setChart(pie);
        });
    }
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
    private void setupListerners(){
        ButtonDetailsStatistics.setOnClickListener(view -> {
            startDetailsStatisticsActivity();
        });
    }

    private void startDetailsStatisticsActivity(){
        Intent intent = new Intent(this, DetailsStatisticsActivity.class) ;
        startActivity(intent);
    }

}

