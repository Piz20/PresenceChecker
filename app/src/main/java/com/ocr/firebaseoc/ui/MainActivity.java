package com.ocr.firebaseoc.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.databinding.ActivityMainBinding;
import com.ocr.firebaseoc.manager.UserManager;
import com.ocr.firebaseoc.utils.AlarmReceiver;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static final int RC_SIGN_IN = 123;
    private static final int REQUEST_CHECK_SETTINGS = 212;



    private final UserManager userManager = UserManager.getInstance();

        @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);


        setupListeners();
        setAlarmanager();
    }
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupListeners() {
        // Bouton de connexion
        binding.loginButton.setOnClickListener(view -> {
            if (userManager.isCurrentUserLogged()) {
                startProfileActivity();
            } else {
                startSignInActivity();
            }
        });
        //Bouton de localisation
        binding.locationButton.setOnClickListener(view -> {
            if (userManager.isCurrentUserLogged() && checkLocationEnabled()) {
                startLocationActivity();
            } else if (!userManager.isCurrentUserLogged()) {
                Toast.makeText(this, getString(R.string.error_not_connected) ,Toast.LENGTH_SHORT).show();
            }
        });
        //Bouton pour les statisques
        binding.statisticsButton.setOnClickListener(view -> {
            if(userManager.isCurrentUserLogged()){
                startStatisticsActivity();
            }
            else if (!userManager.isCurrentUserLogged()){
                Toast.makeText(this,getString(R.string.error_not_connected),Toast.LENGTH_SHORT).show() ;
            }
        });

    }

    private void startSignInActivity() {

        // Pour choisir le fournisseur d'authentifications
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );


        // Lance l'activité
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN) ;
    }

    // Sert à lancer la Profile Activity
    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void startLocationActivity() {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    private void startStatisticsActivity(){
        Intent intent = new Intent(this,StatisticsActivity.class) ;
        startActivity(intent);
    }

    private boolean checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Pour vérifier si la localisation est activé sur l'appareil de l'utilisateur
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

       task.addOnFailureListener(exception -> {
            if (exception instanceof ResolvableApiException) {

                try {
                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                    resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS) ;
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }

            }

        });
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // Mets à jour le bouton de connexion lorsqu'on fait un retour sur la Main Activity

    private void updateLoginButton() {
        binding.loginButton.setText(userManager.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }

    // Affiche une Snackbar avec un message
    private void showSnackBar(String message) {
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    // Methode qui gere la reponse après l'enregistrement d'un utilisateur
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCES
            if (resultCode == RESULT_OK) {
                userManager.createUser();
                showSnackBar(getString(R.string.connection_succeed));
            } else {
                // ERREURS
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError() != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }
  private void setAlarmanager(){
      Calendar startTime = Calendar.getInstance();
      startTime.set(Calendar.HOUR_OF_DAY, 20);
      startTime.set(Calendar.MINUTE, 0);
      startTime.set(Calendar.SECOND, 0);

// Si l'heure de départ est passée aujourd'hui, ajoutez un jour
      if (startTime.before(Calendar.getInstance())) {
          startTime.add(Calendar.DATE, 1);
      }

// Créez un calendrier pour définir l'heure de fin de l'alarme
      Calendar endTime = Calendar.getInstance();
      endTime.set(Calendar.HOUR_OF_DAY, 23);
      endTime.set(Calendar.MINUTE, 40);
      endTime.set(Calendar.SECOND, 0);

// Créez un intent pour votre action à effectuer
      Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
      PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

// Obtenez l'AlarmManager
      AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

// Planifiez votre action pour la première fois
      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

// Si l'heure de fin est avant la prochaine alarme, annulez la répétition
      if (endTime.before(startTime)) {
          pendingIntent.cancel();
      } else {
          // Planifiez votre action pour toutes les 20 minutes jusqu'à l'heure de fin
          alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
      }

}
}