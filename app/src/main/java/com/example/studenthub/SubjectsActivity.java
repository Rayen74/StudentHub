package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SubjectsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private RecyclerView recyclerViewSubjects;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnAddSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        ImageView ivBack = findViewById(R.id.ivBack);
        recyclerViewSubjects = findViewById(R.id.recyclerViewSubjects);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        MaterialButton btnAddFirstSubject = findViewById(R.id.btnAddFirstSubject);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(this));

        ivBack.setOnClickListener(v -> navigateToHome());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        btnAddSubject.setOnClickListener(v -> openAddSubject());
        btnAddFirstSubject.setOnClickListener(v -> openAddSubject());

        // Persistent Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                navigateToHome();
                return true;
            } else if (id == R.id.nav_users) {
                Intent intent = new Intent(SubjectsActivity.this, UserListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(SubjectsActivity.this, ProfileActivity.class);
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
        loadSubjects();
    }

    private void loadSubjects() {
        int userId = sessionManager.getUserId();
        List<Subject> subjectList = databaseHelper.getSubjectsByUser(userId);

        SubjectAdapter adapter = new SubjectAdapter(subjectList, new SubjectAdapter.OnSubjectActionListener() {
            @Override
            public void onView(Subject subject) {
                Intent intent = new Intent(SubjectsActivity.this, SubjectDetailsActivity.class);
                intent.putExtra(SubjectDetailsActivity.EXTRA_SUBJECT_ID, subject.getId());
                startActivity(intent);
            }

            @Override
            public void onEdit(Subject subject) {
                Intent intent = new Intent(SubjectsActivity.this, AddSubjectActivity.class);
                intent.putExtra(AddSubjectActivity.EXTRA_SUBJECT_ID, subject.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Subject subject) {
                showDeleteConfirmationDialog(subject);
            }
        });

        recyclerViewSubjects.setAdapter(adapter);

        if (subjectList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewSubjects.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewSubjects.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmationDialog(Subject subject) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_subject_title)
                .setMessage(R.string.delete_subject_confirmation)
                .setPositiveButton(R.string.supprimer, (dialog, which) -> deleteSubject(subject))
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteSubject(Subject subject) {
        int userId = sessionManager.getUserId();
        boolean success = databaseHelper.deleteSubject(subject.getId(), userId);
        if (success) {
            LottieFeedbackDialog.showSuccess(this,
                    getString(R.string.subjects_title),
                    getString(R.string.subject_deleted_successfully),
                    this::loadSubjects);
        } else {
            LottieFeedbackDialog.showError(this,
                    getString(R.string.subjects_title),
                    getString(R.string.subject_delete_error),
                    null);
        }
    }

    private void openAddSubject() {
        Intent intent = new Intent(SubjectsActivity.this, AddSubjectActivity.class);
        startActivity(intent);
    }

    private void navigateToHome() {
        Intent intent = new Intent(SubjectsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
