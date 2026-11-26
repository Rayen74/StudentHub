package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvUserNomPrenom = findViewById(R.id.tvUserNomPrenom);
        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        String prenom = getIntent().getStringExtra("USER_PRENOM");
        String nom = getIntent().getStringExtra("USER_NOM");
        String email = getIntent().getStringExtra("USER_EMAIL");

        if (prenom != null && !prenom.isEmpty()) {
            tvWelcome.setText("Bienvenue, " + prenom + " !");
        } else {
            tvWelcome.setText("Bienvenue !");
        }

        tvUserNomPrenom.setText("Nom & Prénom : " + (nom != null ? nom : "") + " " + (prenom != null ? prenom : ""));
        tvUserEmail.setText("E-mail : " + (email != null ? email : ""));

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
