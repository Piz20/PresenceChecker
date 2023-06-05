package com.ocr.firebaseoc.ui;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.ocr.firebaseoc.databinding.ActivityLocationBinding;
import com.ocr.firebaseoc.models.Presence;
import com.ocr.firebaseoc.utils.LocationCalcul;

import java.util.Calendar;
import java.util.Date;

public class LocationActivity extends BaseActivity<ActivityLocationBinding> implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 210;
    private static final String MAPVIEW_BUNDLE_KEY = "211";
    private static final String COLLECTION_NAME = "users";

    private static final String SUB_COLLECTION_NAME = "presences";

    private final double latitudeEnspd = 4.0657954;
    private final double longitudeEnspd = 9.7088116;

    private LocationCalcul mLocationCalcul;
    private GoogleMap mMap;

    private MapView mMapView;

    public TextView mTextView;

    Button mButtonAbsence;

    Button mButtonPresence;

    ImageView mLocationImage;

    ImageView mredCrossImage;

    ProgressBar mProgressbar;

    Date date;


    @Override
    ActivityLocationBinding getViewBinding() {
        return ActivityLocationBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        //initialisation de la mapView et du TextView ainsi que les boutons pour la présence te l'absence
        mMapView = findViewById(R.id.map_view);
        mTextView = findViewById(R.id.location_text_view);
        mButtonAbsence = findViewById(R.id.absence_button);
        mButtonPresence = findViewById(R.id.presence_button);
        mLocationImage = findViewById(R.id.location_image);
        mredCrossImage = findViewById(R.id.location_red_cross);
        mProgressbar = findViewById(R.id.progress_bar);
        //Rend par défaut les boutons d'absences et de présences  invisibles
        mButtonAbsence.setVisibility(View.GONE);
        mButtonPresence.setVisibility(View.GONE);
        //Place des écouteurs aux  pressions des boutons d'absences et de présences
        mButtonAbsence.setOnClickListener(v -> startAbsenceActivity());
        mButtonPresence.setOnClickListener(this::createPresence);

        //initialisation de la carte
        initGoogleMap(savedInstanceState);


    }


    private void startAbsenceActivity() {
        Intent intent = new Intent(this, AbsenceActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    private void initGoogleMap(Bundle savedInstanceState) {

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);

            checkLocationAndUpdateTextView();


        }
    }

    private void checkLocationAndUpdateTextView() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                mLocationCalcul = new LocationCalcul(latitudeEnspd, longitudeEnspd, latitude, longitude, 1);
                System.out.println("********************************************************" + latitude + " " + longitude);
                if (mLocationCalcul.dansLeRayon()) {
                    mTextView.setText(R.string.positive_location);
                    mLocationImage.setImageResource(R.drawable.ic_good_location);
                    mredCrossImage.setVisibility(View.GONE);
                    //Effectue un Zoom sur la position actuelle de l'utilisateur
                    LatLng currentLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Votre position"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    //Rend le bouton de présence visible étant donné la localisation positive
                    mButtonPresence.setVisibility(View.VISIBLE);

                } else if (!mLocationCalcul.dansLeRayon()) {
                    mTextView.setText(R.string.negative_location);
                    mLocationImage.setImageResource(R.drawable.ic_wrong_location);
                    mredCrossImage.setVisibility(View.VISIBLE);
                    //Effectue un Zoom sur la position actuelle de l'utilisateur
                    LatLng currentLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Votre position"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    //Rend le bouton d'absence visible étant donné la localistion négative
                    mButtonAbsence.setVisibility(View.VISIBLE);

                }
            } else {
                mTextView.setText(R.string.neutral_position);
                mLocationImage.setImageResource(R.drawable.ic_update_loation);
                mredCrossImage.setVisibility(View.GONE);
            }
        });
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

    //Gere l'envoi des données sur Firebase afin de créer un document dans la collection presence
    public void createPresence(View view) {
        mProgressbar.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        //Pour convertir l'objet datePicker en date

        date = calendar.getTime();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            this.getUsersCollection().document(uid).collection(SUB_COLLECTION_NAME).orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            this.getUsersCollection().document(uid).collection(SUB_COLLECTION_NAME).add(this.makePresence())
                                    .addOnSuccessListener(documentReference -> {
                                        Snackbar.make(view, R.string.presence_noticed_successfully, Snackbar.LENGTH_LONG).show();
                                        mProgressbar.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener(e -> {
                                        Snackbar.make(view, R.string.presence_noticed_error, Snackbar.LENGTH_LONG)
                                                .show();
                                        mProgressbar.setVisibility(View.GONE);
                                    });

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
                                    mProgressbar.setVisibility(View.GONE);
                                    Snackbar.make(view, R.string.presence_already_noticed, Snackbar.LENGTH_LONG)
                                            .show();
                                    break;
                                } else if (!(year == existingDay && month == existingMonth && dayOfMonth == existingDay)) {
                                    this.getUsersCollection().document(uid).collection(SUB_COLLECTION_NAME).add(this.makePresence())
                                            .addOnSuccessListener(documentReference -> {
                                                Snackbar.make(view, R.string.presence_noticed_successfully , Snackbar.LENGTH_LONG).show();
                                                mProgressbar.setVisibility(View.GONE);
                                            })
                                            .addOnFailureListener(e -> {
                                                Snackbar.make(view, R.string.presence_noticed_error, Snackbar.LENGTH_LONG)
                                                        .show();
                                                mProgressbar.setVisibility(View.GONE);
                                            });
                                    break;
                                }
                            }
                        } else {

                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                }
                // Cree un objet presence
                public Presence makePresence() {
                    return new Presence(date);
                }
                //Retourne l'utilisateur actuellement connecté
                private CollectionReference getUsersCollection() {
                    return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);

                }
            });

        }


    }
}


