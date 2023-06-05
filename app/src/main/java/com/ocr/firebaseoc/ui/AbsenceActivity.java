package com.ocr.firebaseoc.ui;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.databinding.ActivityAbsenceBinding;
import com.ocr.firebaseoc.manager.UserManager;
import com.ocr.firebaseoc.models.Absence;

import java.util.Calendar;
import java.util.Date;

public class AbsenceActivity extends BaseActivity<ActivityAbsenceBinding> {

    private static final String SUB_COLLECTION_NAME = "absences";
    private static final String COLLECTION_NAME = "users";
    private final UserManager userManager = UserManager.getInstance();

    ProgressBar mProgressBar ;
    TextView mUsernameTextView;

    TextView mDateAbsence ;
    Spinner mMotifAbsence;

    String reason;

    Date date;

    Button sendAbsenceButton;

    @Override
    ActivityAbsenceBinding getViewBinding() {

        return ActivityAbsenceBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence);
        mUsernameTextView = findViewById(R.id.username_text_view) ;
        mDateAbsence = findViewById(R.id.date_absence);
        mMotifAbsence = findViewById(R.id.motif_absence);
        mProgressBar = findViewById(R.id.progress_bar) ;
        sendAbsenceButton = findViewById(R.id.send_absence_button);
        updateUIWithUserData();
        setupListeners();
    }

    private void updateUIWithUserData() {
        if (userManager.isCurrentUserLogged()) {
            setLegitText();
        }
    }

    private void setLegitText() {
        //Mettre le nom de l'utilisateur dans le champ approprié
        userManager.getUserData().addOnSuccessListener(user -> {
            String username = TextUtils.isEmpty(user.getUsername()) ? getString(R.string.info_no_username_found) : user.getUsername();
            mUsernameTextView.setText(username);
        });

        // Obtenir la date actuelle
        Calendar calendar = Calendar.getInstance();
        //Pour convertir l'objet datePicker en date

        date = calendar.getTime();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Initialiser la date avec la date actuelle et le rendre impossible à mettre à jour
        mDateAbsence.setText(dayOfMonth+"/"+month+"/"+year) ;

        mMotifAbsence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                reason = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

    }


    //Ecoute le bouton pour envoyer le formulaire d'absence
    private void setupListeners() {
        sendAbsenceButton.setOnClickListener(this::createAbsence);
    }

    //Permet de récupérer la collection sur laquelle on travaille , en l'occurence "users"
    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //Retourne l'utilisateur actuellement connecté
    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //Gere l'envoi des données sur Firebase afin de créer un document dans la collection absence
    public void createAbsence(View view) {
        FirebaseUser user = getCurrentUser();
        Calendar calendar = Calendar.getInstance();
        //Pour convertir l'objet datePicker en date

        date = calendar.getTime();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        mProgressBar.setVisibility(View.VISIBLE);

        if (user != null) {
            String uid = user.getUid();

            this.getUsersCollection().document(uid).collection(SUB_COLLECTION_NAME).orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            this.getUsersCollection().document(uid).collection(SUB_COLLECTION_NAME).add(this.makeAbsence())
                                    .addOnSuccessListener(documentReference -> { Snackbar.make(view, R.string.form_sent_successfully, Snackbar.LENGTH_LONG).show();mProgressBar.setVisibility(View.GONE);})
                                    .addOnFailureListener(e -> {Snackbar.make(view, "Erreur lors de l'envoi du formulaire d'absence.", Snackbar.LENGTH_LONG)
                                            .show();mProgressBar.setVisibility(View.GONE);});

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
                                System.out.println(existingDay + " " + existingMonth + " " + existingYear + "****************************");
                                System.out.println(dayOfMonth + " " + month + " " + year + "****************************");

                                if (year == existingYear && month == existingMonth && dayOfMonth == existingDay) {
                                    mProgressBar.setVisibility(View.GONE);
                                    Snackbar.make(view, R.string.already_sent_absence_form, Snackbar.LENGTH_LONG)
                                            .show();
                                    break ;
                                } else if ((!(year == existingDay && month == existingMonth && dayOfMonth == existingDay))){
                                    this.getUsersCollection().document(uid).collection(SUB_COLLECTION_NAME).add(this.makeAbsence())
                                            .addOnSuccessListener(documentReference -> { Snackbar.make(view, R.string.form_sent_successfully, Snackbar.LENGTH_LONG).show();mProgressBar.setVisibility(View.GONE);})
                                            .addOnFailureListener(e -> {Snackbar.make(view, R.string.form_sent_error, Snackbar.LENGTH_LONG)
                                                    .show();mProgressBar.setVisibility(View.GONE);});
                                    break;
                                }
                            }
                        } else {

                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                }

                public Absence makeAbsence() {
                    return new Absence(Absence.reasonToFrench(reason), date);
                }

                private CollectionReference getUsersCollection() {
                    return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
                }
            });


        }

    }

}

