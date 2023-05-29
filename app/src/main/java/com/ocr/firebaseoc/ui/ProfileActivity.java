package com.ocr.firebaseoc.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.databinding.ActivityProfileBinding;
import com.ocr.firebaseoc.manager.UserManager;

public class ProfileActivity extends BaseActivity<ActivityProfileBinding> {

    private final UserManager userManager = UserManager.getInstance();

    private int i ;

    @Override
    ActivityProfileBinding getViewBinding() {

        return ActivityProfileBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupListeners();
        updateUIWithUserData();
    }

    private void setupListeners() {
        //Bouton de déconnexion
        binding.signOutButton.setOnClickListener(view ->{ userManager.signOut(this).addOnSuccessListener(aVoid ->finish() ) ;});

        // Bouton pour supprimer le compte
        binding.deleteButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> {
                          userManager.deleteUserFromFirestore();
                            userManager.deleteUser(ProfileActivity.this)
                                    .addOnSuccessListener(aVoid -> finish());
                            Snackbar.make(binding.profileLayout, "Suppression de compte réussie", Snackbar.LENGTH_SHORT).show();
                        }
                )
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show());
        //Pour modifier le nom d'utilisateur
        binding.updateButton.setOnClickListener(view -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            userManager.updateUsername(binding.usernameEditText.getText().toString())
                    .addOnSuccessListener(aVoid -> binding.progressBar.setVisibility(View.INVISIBLE));
        });

    }

    private void updateUIWithUserData() {
        if (userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();
            if (user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);
            getUserData();
        }
    }

    private void getUserData() {
        userManager.getUserData().addOnSuccessListener(user -> {
            String username = TextUtils.isEmpty(user.getUsername()) ? getString(R.string.info_no_username_found) : user.getUsername();
            binding.usernameEditText.setText(username);
            binding.usernameEditText.setSelection(username.length()) ;
        });
    }

    private void setProfilePicture(Uri profilePictureUrl) {
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImageView);
    }

    private void setTextUserData(FirebaseUser user) {

        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) :
                user.getEmail();
        binding.emailTextView.setText(email);
    }
}
