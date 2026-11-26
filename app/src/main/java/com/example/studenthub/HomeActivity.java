package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;
    private MaterialButton btnToggleTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnToggleTheme = findViewById(R.id.btnToggleTheme);
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
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        ivProfileAvatar.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        // Academic Feature Cards (Exercice 2 du TP)
        cardMatieres.setOnClickListener(v -> showAcademicInfoDialog("Mes matières", "Retrouvez ici la liste de vos matières, coefficients et cours associés."));
        cardTaches.setOnClickListener(v -> showAcademicInfoDialog("Mes tâches", "Consultez vos devoirs à accomplir, priorités et niveaux d'avancement."));
        cardEcheances.setOnClickListener(v -> showAcademicInfoDialog("Prochaines échéances", "Planifiez votre charge de travail grâce aux dates limites de vos examens et projets."));

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
            tvWelcome.setText("Bienvenue, " + prenom + " !");
        } else {
            tvWelcome.setText("Bienvenue !");
        }

        if (sessionManager.isDarkMode()) {
            btnToggleTheme.setText("☾ Dark");
        } else {
            btnToggleTheme.setText("☀ Light");
        }
    }

    private void showAcademicInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
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
