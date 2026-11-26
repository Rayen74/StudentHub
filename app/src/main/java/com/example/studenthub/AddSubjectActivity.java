package com.example.studenthub;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddSubjectActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT_ID = "extra_subject_id";

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private TextInputEditText etSubjectName, etSubjectCode, etSubjectTeacher, etSubjectDescription;
    private MaterialButton btnSaveSubject;
    private TextView tvBarTitle;

    private int subjectId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        ImageView ivBack = findViewById(R.id.ivBack);
        tvBarTitle = findViewById(R.id.tvBarTitle);
        etSubjectName = findViewById(R.id.etSubjectName);
        etSubjectCode = findViewById(R.id.etSubjectCode);
        etSubjectTeacher = findViewById(R.id.etSubjectTeacher);
        etSubjectDescription = findViewById(R.id.etSubjectDescription);
        btnSaveSubject = findViewById(R.id.btnSaveSubject);

        ivBack.setOnClickListener(v -> finish());

        if (getIntent().hasExtra(EXTRA_SUBJECT_ID)) {
            subjectId = getIntent().getIntExtra(EXTRA_SUBJECT_ID, -1);
            if (subjectId != -1) {
                isEditMode = true;
                tvBarTitle.setText(getString(R.string.edit_subject));
                btnSaveSubject.setText(getString(R.string.save_subject_changes));
                loadSubjectData(subjectId);
            }
        }

        btnSaveSubject.setOnClickListener(v -> saveSubject());
    }

    private void loadSubjectData(int id) {
        int userId = sessionManager.getUserId();
        Subject subject = databaseHelper.getSubjectByIdAndUser(id, userId);
        if (subject != null) {
            etSubjectName.setText(subject.getName());
            etSubjectCode.setText(subject.getCode());
            etSubjectTeacher.setText(subject.getTeacher());
            etSubjectDescription.setText(subject.getDescription());
        } else {
            Toast.makeText(this, getString(R.string.subject_not_found), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveSubject() {
        String name = etSubjectName.getText() != null ? etSubjectName.getText().toString().trim() : "";
        String code = etSubjectCode.getText() != null ? etSubjectCode.getText().toString().trim() : "";
        String teacher = etSubjectTeacher.getText() != null ? etSubjectTeacher.getText().toString().trim() : "";
        String description = etSubjectDescription.getText() != null ? etSubjectDescription.getText().toString().trim() : "";

        if (name.isEmpty()) {
            etSubjectName.setError(getString(R.string.subject_name_required));
            Toast.makeText(this, getString(R.string.subject_name_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.isEmpty()) {
            etSubjectCode.setError(getString(R.string.subject_code_required));
            Toast.makeText(this, getString(R.string.subject_code_required), Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();

        if (!isEditMode) {
            Subject newSubject = new Subject(name, code, teacher, description, userId);
            long rowId = databaseHelper.insertSubject(newSubject);
            if (rowId != -1) {
                LottieFeedbackDialog.showSuccess(this,
                        getString(R.string.subjects_title),
                        getString(R.string.subject_added_successfully),
                        this::finish);
            } else {
                LottieFeedbackDialog.showError(this,
                        getString(R.string.subjects_title),
                        getString(R.string.subject_save_error),
                        null);
            }
        } else {
            Subject updatedSubject = new Subject(subjectId, name, code, teacher, description, userId);
            boolean success = databaseHelper.updateSubject(updatedSubject, userId);
            if (success) {
                LottieFeedbackDialog.showSuccess(this,
                        getString(R.string.subjects_title),
                        getString(R.string.subject_updated_successfully),
                        this::finish);
            } else {
                LottieFeedbackDialog.showError(this,
                        getString(R.string.subjects_title),
                        getString(R.string.subject_save_error),
                        null);
            }
        }
    }
}
