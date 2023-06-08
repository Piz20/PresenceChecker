package com.ocr.firebaseoc.ui;

import static android.content.ContentValues.TAG;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocr.firebaseoc.Adapter.MyAdapter;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.models.Document;

import java.util.ArrayList;
import java.util.List;

public class DetailsStatisticsActivity extends AppCompatActivity {

    private static final String COLLECTION_NAME = "users";

    ProgressBar progressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_statistics);
        progressBar = findViewById(R.id.progress_bar) ;
        FirebaseUser user = getCurrentUser();
        String uid;
        if (user != null) {
             uid = user.getUid();
            Query query1 = this.getUsersCollection().document(uid).collection("absences").orderBy("date", Query.Direction.DESCENDING) ;
            Query query2 = this.getUsersCollection().document(uid).collection("presences").orderBy("date", Query.Direction.DESCENDING)
                    .whereEqualTo("confirmed",true) ;
            Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(query1.get(), query2.get());
             allTasks.addOnSuccessListener(querySnapshots -> {
                 progressBar.setVisibility(View.GONE);
                 List<Document> documents = new ArrayList<>();
                 for (QueryDocumentSnapshot document : querySnapshots.get(0)) {
                     String reason = document.getString("reason");
                     Timestamp date = document.getTimestamp("date");
                     documents.add(new Document(reason, date));
                 }
                 for (QueryDocumentSnapshot document : querySnapshots.get(1)) {
                     String reason = document.getString("reason");
                     Timestamp date = document.getTimestamp("date");
                     documents.add(new Document(reason, date));
                 }
                 // Obtention de la RecyclerView
                 RecyclerView recyclerView = findViewById(R.id.recyclerView);
                 recyclerView.setVisibility(View.VISIBLE);
                 // Configuration de la RecyclerView
                 MyAdapter adapter = new MyAdapter(documents);
                 recyclerView.setAdapter(adapter);
                 recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));}) ;

        }
    }
    private CollectionReference getUsersCollection () {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


}