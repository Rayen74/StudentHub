package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private RecyclerView recyclerViewUsers;
    private TextView tvUserCount, tvEmptyState;
    private TextInputEditText etSearchEmail;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_user_list);

        databaseHelper = new DatabaseHelper(this);
        ImageView ivBack = findViewById(R.id.ivBack);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        tvUserCount = findViewById(R.id.tvUserCount);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        etSearchEmail = findViewById(R.id.etSearchEmail);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Back navigation returns to Home
        ivBack.setOnClickListener(v -> navigateToHome());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        loadUsers("");

        etSearchEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUsers(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Persistent Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_users);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                navigateToHome();
                return true;
            } else if (id == R.id.nav_users) {
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(UserListActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(UserListActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void loadUsers(String query) {
        if (query.isEmpty()) {
            userList = databaseHelper.getAllUsers();
        } else {
            userList = databaseHelper.searchUsersByEmail(query);
        }

        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);

        tvUserCount.setText(userList.size() + " utilisateur(s)");

        if (userList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerViewUsers.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewUsers.setVisibility(View.VISIBLE);
        }
    }
}
