package com.example.studenthub;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private TextInputEditText etProfileNom, etProfilePrenom, etProfileEmail;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private TextView tvAvatarInitials, tvProfileFullName, tvProfileHeaderEmail, tvProfileCreatedAt;
    private MaterialButton btnEditSaveProfile, btnCancelEdit, btnUpdatePassword;

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_profile);

        ImageView ivBack = findViewById(R.id.ivBack);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        tvProfileFullName = findViewById(R.id.tvProfileFullName);
        tvProfileHeaderEmail = findViewById(R.id.tvProfileHeaderEmail);
        tvProfileCreatedAt = findViewById(R.id.tvProfileCreatedAt);

        etProfileNom = findViewById(R.id.etProfileNom);
        etProfilePrenom = findViewById(R.id.etProfilePrenom);
        etProfileEmail = findViewById(R.id.etProfileEmail);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);

        btnEditSaveProfile = findViewById(R.id.btnEditSaveProfile);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        loadProfileData();

        ivBack.setOnClickListener(v -> finish());

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

        btnUpdatePassword.setOnClickListener(v -> handlePasswordUpdate());

        // Persistent Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_users) {
                Intent intent = new Intent(ProfileActivity.this, UserListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void loadProfileData() {
        String nom = sessionManager.getNom();
        String prenom = sessionManager.getPrenom();
        String email = sessionManager.getEmail();

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

        String createdAt = fetchCreatedAtFromDb(email);
        if (createdAt != null && !createdAt.isEmpty()) {
            tvProfileCreatedAt.setText("Compte créé le : " + createdAt);
        } else {
            tvProfileCreatedAt.setText("Compte créé le : Non disponible");
        }
    }

    private String fetchCreatedAtFromDb(String email) {
        String date = "";
        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                    new String[]{DatabaseHelper.COLUMN_CREATED_AT},
                    DatabaseHelper.COLUMN_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                date = cursor.getString(0);
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
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
        boolean success = databaseHelper.updateUserProfile(userId, newNom, newPrenom);

        if (success) {
            sessionManager.updateUserInfo(newNom, newPrenom);
            Toast.makeText(this, "Profil mis à jour avec succès.", Toast.LENGTH_SHORT).show();
            loadProfileData();
            setEditingMode(false);
        } else {
            Toast.makeText(this, "Erreur lors de la mise à jour du profil.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePasswordUpdate() {
        String currentPass = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString().trim() : "";
        String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPass = etConfirmNewPassword.getText() != null ? etConfirmNewPassword.getText().toString().trim() : "";

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs de sécurité.", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();

        boolean isCurrentValid = databaseHelper.verifyCurrentPassword(userId, currentPass);
        if (!isCurrentValid) {
            Toast.makeText(this, "Le mot de passe actuel est incorrect.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Le nouveau mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Les nouveaux mots de passe ne correspondent pas.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.equals(currentPass)) {
            Toast.makeText(this, "Le nouveau mot de passe doit être différent de l'actuel.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = databaseHelper.updateUserPassword(userId, newPass);
        if (success) {
            Toast.makeText(this, "Mot de passe mis à jour avec succès.", Toast.LENGTH_LONG).show();
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmNewPassword.setText("");
        } else {
            Toast.makeText(this, "Erreur lors de la mise à jour du mot de passe.", Toast.LENGTH_SHORT).show();
        }
    }
}
