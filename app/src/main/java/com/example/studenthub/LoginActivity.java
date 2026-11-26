package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionManager = new SessionManager(this);

        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        // Persistent Session Check: If already logged in, skip login
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> handleLogin());

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.login_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        User user = databaseHelper.checkUser(email, password);
        if (user != null) {
            sessionManager.createLoginSession(user.getId(), user.getNom(), user.getPrenom(), user.getEmail());
            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            LottieFeedbackDialog.showError(this,
                    getString(R.string.login_failed_title),
                    getString(R.string.login_failed_message),
                    null);
        }
    }
}
