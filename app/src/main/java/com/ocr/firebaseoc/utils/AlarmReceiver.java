package com.ocr.firebaseoc.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.models.Absence;

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String COLLECTION_NAME = "users";
    private Date date;

    private Context mContext ;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context ;
        FirebaseUser user = getCurrentUser();
        Calendar calendar = Calendar.getInstance();

        date = calendar.getTime();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);


        if (user != null) {
            String uid = user.getUid();
            this.getUsersCollection().document(uid).collection("presences").orderBy("date", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Vérifier si la requête a retourné un document avec la date d'aujourd'hui
                    if (task.getResult().isEmpty()) {
                        AddToAbsenceCollection();
                    }
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("date");
                            Calendar FirestoreCalendar = Calendar.getInstance();
                            if (timestamp != null) {
                                FirestoreCalendar.setTimeInMillis(timestamp.getSeconds() * 1000);
                            }
                            int existingYear = FirestoreCalendar.get(Calendar.YEAR);
                            int existingMonth = FirestoreCalendar.get(Calendar.MONTH) + 1;
                            int existingDay = FirestoreCalendar.get(Calendar.DAY_OF_MONTH);

                            if (year == existingYear && month == existingMonth && dayOfMonth == existingDay) {

                                break;
                            } else if ((!(year == existingDay && month == existingMonth && dayOfMonth == existingDay))) {
                                AddToAbsenceCollection();
                                break;
                            }
                        }

                    }
                }
            });
        }

    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void AddToAbsenceCollection() {
        FirebaseUser user = getCurrentUser();
        Calendar calendar = Calendar.getInstance();

        date = calendar.getTime();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);


        if (user != null) {
            String uid = user.getUid();
            this.getUsersCollection().document(uid).collection("absences").orderBy("date", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        this.getUsersCollection().document(uid).collection("absences").add(new Absence("Other", date))
                                .addOnSuccessListener(view ->Toast.makeText(mContext,R.string.form_sent_successfully,Toast.LENGTH_LONG).show());                    }
                    if (!task.getResult().isEmpty()) {
                        // Créer un nouveau document dans la collection 1 avec la date d'aujourd'hui
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("date");
                            Calendar FirestoreCalendar = Calendar.getInstance();
                            if (timestamp != null) {
                                FirestoreCalendar.setTimeInMillis(timestamp.getSeconds() * 1000);
                            }
                            int existingYear = FirestoreCalendar.get(Calendar.YEAR);
                            int existingMonth = FirestoreCalendar.get(Calendar.MONTH) + 1;
                            int existingDay = FirestoreCalendar.get(Calendar.DAY_OF_MONTH);
                            if (year == existingYear && month == existingMonth && dayOfMonth == existingDay) {
                                break;
                            } else if ((!(year == existingDay && month == existingMonth && dayOfMonth == existingDay))) {
                                this.getUsersCollection().document(uid).collection("absences").add(new Absence("Other", date)).addOnSuccessListener(view -> Toast.makeText(mContext,R.string.form_sent_successfully,Toast.LENGTH_LONG).show());
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

}
