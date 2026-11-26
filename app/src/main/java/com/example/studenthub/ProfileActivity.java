package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private TextInputEditText etProfileNom, etProfilePrenom, etProfileEmail;
    private TextView tvAvatarInitials, tvProfileFullName, tvProfileHeaderEmail;
    private MaterialButton btnEditSaveProfile, btnCancelEdit, btnGoToSecurity;

    private boolean isEditing = false;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        setContentView(R.layout.activity_profile);

        ImageView ivBack = findViewById(R.id.ivBack);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        tvProfileFullName = findViewById(R.id.tvProfileFullName);
        tvProfileHeaderEmail = findViewById(R.id.tvProfileHeaderEmail);

        etProfileNom = findViewById(R.id.etProfileNom);
        etProfilePrenom = findViewById(R.id.etProfilePrenom);
        etProfileEmail = findViewById(R.id.etProfileEmail);

        btnEditSaveProfile = findViewById(R.id.btnEditSaveProfile);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnGoToSecurity = findViewById(R.id.btnGoToSecurity);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        loadProfileData();

        ivBack.setOnClickListener(v -> navigateToHome());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        btnEditSaveProfile.setOnClickListener(v -> {
            if (!isEditing) {
                setEditingMode(true);
            } else {
                saveProfileChanges();
            }
        });

        btnCancelEdit.setOnClickListener(v -> {
            loadProfileData();
            setEditingMode(false);
        });

        btnGoToSecurity.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SecurityActivity.class);
            startActivity(intent);
        });

        // Persistent Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                navigateToHome();
                return true;
            } else if (id == R.id.nav_users) {
                Intent intent = new Intent(ProfileActivity.this, UserListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void loadProfileData() {
        executorService.execute(() -> {
            int userId = sessionManager.getUserId();
            User user = databaseHelper.getUserById(userId);
            if (user == null) {
                user = new User(userId, sessionManager.getNom(), sessionManager.getPrenom(), sessionManager.getEmail());
            }

            final User finalUser = user;
            mainHandler.post(() -> {
                String nom = finalUser.getNom();
                String prenom = finalUser.getPrenom();
                String email = finalUser.getEmail();

                etProfileNom.setText(nom);
                etProfilePrenom.setText(prenom);
                etProfileEmail.setText(email);

                tvProfileFullName.setText(nom + " " + prenom);
                tvProfileHeaderEmail.setText(email);

                String initials = "";
                if (prenom != null && !prenom.isEmpty()) {
                    initials += prenom.substring(0, 1).toUpperCase();
                }
                if (nom != null && !nom.isEmpty()) {
                    initials += nom.substring(0, 1).toUpperCase();
                }
                if (initials.isEmpty()) {
                    initials = "U";
                }
                tvAvatarInitials.setText(initials);
            });
        });
    }

    private void setEditingMode(boolean enabled) {
        isEditing = enabled;
        etProfileNom.setEnabled(enabled);
        etProfilePrenom.setEnabled(enabled);

        if (enabled) {
            btnEditSaveProfile.setText("Enregistrer les modifications");
            btnCancelEdit.setVisibility(View.VISIBLE);
        } else {
            btnEditSaveProfile.setText("Modifier le profil");
            btnCancelEdit.setVisibility(View.GONE);
        }
    }

    private void saveProfileChanges() {
        String newNom = etProfileNom.getText() != null ? etProfileNom.getText().toString().trim() : "";
        String newPrenom = etProfilePrenom.getText() != null ? etProfilePrenom.getText().toString().trim() : "";

        if (newNom.isEmpty() || newPrenom.isEmpty()) {
            Toast.makeText(this, "Le nom et le prénom ne peuvent pas être vides.", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        executorService.execute(() -> {
            boolean success = databaseHelper.updateUserProfile(userId, newNom, newPrenom);
            mainHandler.post(() -> {
                if (success) {
                    sessionManager.updateUserInfo(newNom, newPrenom);
                    Toast.makeText(this, "Profil mis à jour avec succès.", Toast.LENGTH_SHORT).show();
                    loadProfileData();
                    setEditingMode(false);
                } else {
                    Toast.makeText(this, "Erreur lors de la mise à jour du profil.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
