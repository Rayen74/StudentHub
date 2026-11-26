package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;
    private MaterialButton btnToggleTheme;
    private MaterialButton btnToggleLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnToggleTheme = findViewById(R.id.btnToggleTheme);
        btnToggleLang = findViewById(R.id.btnToggleLang);
        ImageView ivProfileAvatar = findViewById(R.id.ivProfileAvatar);

        MaterialCardView cardMatieres = findViewById(R.id.cardMatieres);
        MaterialCardView cardTaches = findViewById(R.id.cardTaches);
        MaterialCardView cardEcheances = findViewById(R.id.cardEcheances);
        MaterialCardView cardProfile = findViewById(R.id.cardProfile);
        MaterialCardView cardUsers = findViewById(R.id.cardUsers);
        MaterialButton btnHomeLogout = findViewById(R.id.btnHomeLogout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        updateUI();

        // Theme Toggle
        btnToggleTheme.setOnClickListener(v -> {
            boolean currentDark = sessionManager.isDarkMode();
            boolean newDark = !currentDark;
            sessionManager.setDarkMode(newDark);
            if (newDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, getString(R.string.toast_theme_dark), Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, getString(R.string.toast_theme_light), Toast.LENGTH_SHORT).show();
            }
        });

        // Language Toggle (FR / EN)
        btnToggleLang.setOnClickListener(v -> {
            String currentLang = sessionManager.getLanguage();
            String newLang = "en".equalsIgnoreCase(currentLang) ? "fr" : "en";
            sessionManager.setLanguage(newLang);

            LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(newLang);
            AppCompatDelegate.setApplicationLocales(appLocales);
        });

        ivProfileAvatar.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        // Academic Feature Cards
        cardMatieres.setOnClickListener(v -> showAcademicInfoDialog(getString(R.string.section_matieres_title), getString(R.string.section_matieres_info_message)));
        cardTaches.setOnClickListener(v -> showAcademicInfoDialog(getString(R.string.section_taches_title), getString(R.string.section_taches_info_message)));
        cardEcheances.setOnClickListener(v -> showAcademicInfoDialog(getString(R.string.section_echeances_title), getString(R.string.section_echeances_info_message)));

        // Primary Destination Cards
        cardProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        cardUsers.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, UserListActivity.class)));

        // Logout
        btnHomeLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_users) {
                Intent intent = new Intent(HomeActivity.this, UserListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        String prenom = sessionManager.getPrenom();
        if (prenom != null && !prenom.isEmpty()) {
            tvWelcome.setText(getString(R.string.welcome_user, prenom));
        } else {
            tvWelcome.setText(getString(R.string.welcome));
        }

        if (sessionManager.isDarkMode()) {
            btnToggleTheme.setText(getString(R.string.theme_dark));
        } else {
            btnToggleTheme.setText(getString(R.string.theme_light));
        }

        String currentLang = sessionManager.getLanguage();
        if ("en".equalsIgnoreCase(currentLang)) {
            btnToggleLang.setText(getString(R.string.lang_en));
        } else {
            btnToggleLang.setText(getString(R.string.lang_fr));
        }
    }

    private void showAcademicInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setPositiveButton(R.string.logout_button, (dialog, which) -> {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
