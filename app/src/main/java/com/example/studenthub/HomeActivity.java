package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        // Apply theme
        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        ImageView ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        MaterialCardView cardProfile = findViewById(R.id.cardProfile);
        MaterialCardView cardUsers = findViewById(R.id.cardUsers);
        MaterialCardView cardSecurity = findViewById(R.id.cardSecurity);
        MaterialCardView cardSettings = findViewById(R.id.cardSettings);
        MaterialCardView cardLogout = findViewById(R.id.cardLogout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        updateUI();

        // Avatar click -> Profile
        ivProfileAvatar.setOnClickListener(v -> showEditProfileDialog());

        // Card 1: Profile
        cardProfile.setOnClickListener(v -> showEditProfileDialog());

        // Card 2: Users
        cardUsers.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UserListActivity.class);
            startActivity(intent);
        });

        // Card 3: Security (Change password)
        cardSecurity.setOnClickListener(v -> showChangePasswordDialog());

        // Card 4: Settings (Toggle Dark Mode)
        cardSettings.setOnClickListener(v -> {
            boolean currentDark = sessionManager.isDarkMode();
            boolean newDark = !currentDark;
            sessionManager.setDarkMode(newDark);
            if (newDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "Mode sombre activé", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "Mode clair activé", Toast.LENGTH_SHORT).show();
            }
        });

        // Card 5: Logout with confirmation
        cardLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already on home dashboard
                return true;
            } else if (id == R.id.nav_users) {
                startActivity(new Intent(HomeActivity.this, UserListActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                showEditProfileDialog();
                return true;
            }
            return false;
        });
    }

    private void updateUI() {
        String prenom = sessionManager.getPrenom();
        if (prenom != null && !prenom.isEmpty()) {
            tvWelcome.setText("Bienvenue, " + prenom + " !");
        } else {
            tvWelcome.setText("Bienvenue !");
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        TextInputEditText etNom = dialogView.findViewById(R.id.etEditNom);
        TextInputEditText etPrenom = dialogView.findViewById(R.id.etEditPrenom);

        etNom.setText(sessionManager.getNom());
        etPrenom.setText(sessionManager.getPrenom());

        builder.setTitle("Modifier mon profil")
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String newNom = etNom.getText() != null ? etNom.getText().toString().trim() : "";
                    String newPrenom = etPrenom.getText() != null ? etPrenom.getText().toString().trim() : "";

                    if (newNom.isEmpty() || newPrenom.isEmpty()) {
                        Toast.makeText(this, "Le nom et le prénom ne peuvent pas être vides.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int userId = sessionManager.getUserId();
                    boolean success = databaseHelper.updateUserProfile(userId, newNom, newPrenom);
                    if (success) {
                        sessionManager.updateUserInfo(newNom, newPrenom);
                        updateUI();
                        Toast.makeText(this, "Profil mis à jour avec succès.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erreur lors de la mise à jour du profil.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        builder.setTitle("Changer le mot de passe")
                .setPositiveButton("Modifier", (dialog, which) -> {
                    String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
                    String confirmPass = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

                    if (newPass.isEmpty() || confirmPass.isEmpty()) {
                        Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPass.equals(confirmPass)) {
                        Toast.makeText(this, "Les mots de passe ne correspondent pas.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int userId = sessionManager.getUserId();
                    boolean success = databaseHelper.updateUserPassword(userId, newPass);
                    if (success) {
                        Toast.makeText(this, "Mot de passe modifié avec succès.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erreur lors de la modification du mot de passe.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment vous déconnecter de StudentHub ?")
                .setPositiveButton("Se déconnecter", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
