package com.example.studenthub;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SecurityActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;

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

        setContentView(R.layout.activity_security);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        MaterialButton btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        btnUpdatePassword.setOnClickListener(v -> handlePasswordUpdate());
    }

    private void handlePasswordUpdate() {
        String currentPass = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString().trim() : "";
        String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPass = etConfirmNewPassword.getText() != null ? etConfirmNewPassword.getText().toString().trim() : "";

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();

        // Verify current password
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
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de la mise à jour du mot de passe.", Toast.LENGTH_SHORT).show();
        }
    }
}
