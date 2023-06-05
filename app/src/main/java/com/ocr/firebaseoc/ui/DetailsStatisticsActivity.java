package com.ocr.firebaseoc.ui;

import static android.content.ContentValues.TAG;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
           this.getUsersCollection().document(uid).collection("absences").orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   List<Document> documents = new ArrayList<>();
                   for (QueryDocumentSnapshot document : task.getResult()) {
                       String reason = document.getString("reason");
                       Timestamp date = document.getTimestamp("date");
                       documents.add(new Document(reason, date));
                   }

                   progressBar.setVisibility(View.GONE);
                   // Obtention de la RecyclerView
                   RecyclerView recyclerView = findViewById(R.id.recyclerView);
                   recyclerView.setVisibility(View.VISIBLE);
                   // Configuration de la RecyclerView
                   MyAdapter adapter = new MyAdapter(documents);
                   recyclerView.setAdapter(adapter);
                   recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
               } else {
                   Log.e(TAG, "Error getting documents: ", task.getException());
               }
           });
        }
    }
    private CollectionReference getUsersCollection () {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


}