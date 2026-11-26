package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecurityActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private MaterialButton btnUpdatePassword;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        setContentView(R.layout.activity_security);

        ImageView ivBack = findViewById(R.id.ivBack);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ivBack.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        btnUpdatePassword.setOnClickListener(v -> handlePasswordUpdate());

        // Persistent Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(SecurityActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_users) {
                Intent intent = new Intent(SecurityActivity.this, UserListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(SecurityActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void handlePasswordUpdate() {
        String currentPass = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString() : "";
        String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";
        String confirmPass = etConfirmNewPassword.getText() != null ? etConfirmNewPassword.getText().toString() : "";

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, getString(R.string.register_required_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.equals(currentPass)) {
            Toast.makeText(this, getString(R.string.password_same_as_current), Toast.LENGTH_SHORT).show();
            return;
        }

        btnUpdatePassword.setEnabled(false);
        btnUpdatePassword.setText(getString(R.string.loading));

        int userId = sessionManager.getUserId();
        executorService.execute(() -> {
            boolean isCurrentValid = databaseHelper.verifyCurrentPassword(userId, currentPass);
            if (!isCurrentValid) {
                mainHandler.post(() -> {
                    Toast.makeText(SecurityActivity.this, getString(R.string.password_current_incorrect), Toast.LENGTH_SHORT).show();
                    btnUpdatePassword.setEnabled(true);
                    btnUpdatePassword.setText(getString(R.string.update_password_button));
                });
                return;
            }

            boolean success = databaseHelper.updateUserPassword(userId, newPass);
            mainHandler.post(() -> {
                if (success) {
                    Toast.makeText(SecurityActivity.this, getString(R.string.password_updated_successfully), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(SecurityActivity.this, getString(R.string.password_update_error), Toast.LENGTH_SHORT).show();
                    btnUpdatePassword.setEnabled(true);
                    btnUpdatePassword.setText(getString(R.string.update_password_button));
                }
            });
        });
    }
}
