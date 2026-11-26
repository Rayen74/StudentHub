package com.example.studenthub;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView listViewUsers;
    private TextInputEditText etSearchEmail;
    private List<User> userList;
    private ArrayAdapter<String> adapter;
    private List<String> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        databaseHelper = new DatabaseHelper(this);
        listViewUsers = findViewById(R.id.listViewUsers);
        etSearchEmail = findViewById(R.id.etSearchEmail);

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
    }

    private void loadUsers(String query) {
        if (query.isEmpty()) {
            userList = databaseHelper.getAllUsers();
        } else {
            userList = databaseHelper.searchUsersByEmail(query);
        }

        displayList = new ArrayList<>();
        for (User u : userList) {
            String item = "Nom : " + u.getNom() + " " + u.getPrenom() + "\nE-mail : " + u.getEmail();
            displayList.add(item);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewUsers.setAdapter(adapter);
    }
}
