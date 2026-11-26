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
            Toast.makeText(this, getString(R.string.register_required_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.register_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, getString(R.string.register_password_too_short), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.register_passwords_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkEmailExists(email)) {
            LottieFeedbackDialog.showError(this,
                    getString(R.string.register_email_exists_title),
                    getString(R.string.register_email_exists_message),
                    null);
            return;
        }

        boolean success = databaseHelper.addUser(nom, prenom, email, password);
        if (success) {
            LottieFeedbackDialog.showSuccess(this,
                    getString(R.string.register_success_title),
                    getString(R.string.register_success_message),
                    this::finish);
        } else {
            LottieFeedbackDialog.showError(this,
                    getString(R.string.register_error_title),
                    getString(R.string.register_error_message),
                    null);
        }
    }
}
