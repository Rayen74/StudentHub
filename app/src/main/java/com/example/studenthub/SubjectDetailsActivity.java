package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SubjectDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT_ID = "extra_subject_id";

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private TextView tvDetailName, tvDetailCode, tvDetailTeacher, tvDetailDescription;
    private int subjectId = -1;
    private Subject currentSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        ImageView ivBack = findViewById(R.id.ivBack);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCode = findViewById(R.id.tvDetailCode);
        tvDetailTeacher = findViewById(R.id.tvDetailTeacher);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        MaterialButton btnEditSubject = findViewById(R.id.btnEditSubject);
        MaterialButton btnDeleteSubject = findViewById(R.id.btnDeleteSubject);

        if (getIntent().hasExtra(EXTRA_SUBJECT_ID)) {
            subjectId = getIntent().getIntExtra(EXTRA_SUBJECT_ID, -1);
        }

        ivBack.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        btnEditSubject.setOnClickListener(v -> {
            if (currentSubject != null) {
                Intent intent = new Intent(SubjectDetailsActivity.this, AddSubjectActivity.class);
                intent.putExtra(AddSubjectActivity.EXTRA_SUBJECT_ID, currentSubject.getId());
                startActivity(intent);
            }
        });

        btnDeleteSubject.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubjectDetails();
    }

    private void loadSubjectDetails() {
        if (subjectId == -1) {
            Toast.makeText(this, getString(R.string.subject_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int userId = sessionManager.getUserId();
        currentSubject = databaseHelper.getSubjectByIdAndUser(subjectId, userId);

        if (currentSubject != null) {
            tvDetailName.setText(currentSubject.getName());
            tvDetailCode.setText(currentSubject.getCode());

            String teacher = currentSubject.getTeacher();
            if (teacher != null && !teacher.trim().isEmpty()) {
                tvDetailTeacher.setText(teacher);
            } else {
                tvDetailTeacher.setText(getString(R.string.teacher_not_specified));
            }

            String desc = currentSubject.getDescription();
            if (desc != null && !desc.trim().isEmpty()) {
                tvDetailDescription.setText(desc);
            } else {
                tvDetailDescription.setText(getString(R.string.description_not_available));
            }
        } else {
            Toast.makeText(this, getString(R.string.subject_not_found), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_subject_title)
                .setMessage(R.string.delete_subject_confirmation)
                .setPositiveButton(R.string.supprimer, (dialog, which) -> deleteSubject())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteSubject() {
        int userId = sessionManager.getUserId();
        boolean success = databaseHelper.deleteSubject(subjectId, userId);
        if (success) {
            LottieFeedbackDialog.showSuccess(this,
                    getString(R.string.subjects_title),
                    getString(R.string.subject_deleted_successfully),
                    this::finish);
        } else {
            LottieFeedbackDialog.showError(this,
                    getString(R.string.subjects_title),
                    getString(R.string.subject_delete_error),
                    null);
        }
    }
}
