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
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

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

        // Avatar click -> ProfileActivity
        ivProfileAvatar.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        // Card 1: Profile -> ProfileActivity
        cardProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        // Card 2: Users -> UserListActivity
        cardUsers.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, UserListActivity.class)));

        // Card 3: Security -> ProfileActivity (Password management is inside Profile)
        cardSecurity.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

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
                Intent intent = new Intent(HomeActivity.this, UserListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
