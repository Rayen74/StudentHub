package com.example.studenthub;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNom, etPrenom, etEmail, etPassword, etConfirmPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Respects the active application theme
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        etNom = findViewById(R.id.etRegisterNom);
        etPrenom = findViewById(R.id.etRegisterPrenom);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> handleRegister());

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String nom = etNom.getText() != null ? etNom.getText().toString().trim() : "";
        String prenom = etPrenom.getText() != null ? etPrenom.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Veuillez saisir une adresse e-mail valide.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkEmailExists(email)) {
            LottieFeedbackDialog.showError(this, "E-mail déjà utilisé", "Cet e-mail est déjà associé à un compte existant.", null);
            return;
        }

        boolean success = databaseHelper.addUser(nom, prenom, email, password);
        if (success) {
            LottieFeedbackDialog.showSuccess(this, "Inscription réussie !", "Votre compte StudentHub a été créé avec succès.", this::finish);
        } else {
            LottieFeedbackDialog.showError(this, "Échec de l'inscription", "Une erreur est survenue lors de la création de votre compte.", null);
        }
    }
}
