package com.example.hostelmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.enums.UserRole;
import com.example.hostelmanagementsystem.model.Room;
import com.example.hostelmanagementsystem.ui.adapter.RoomAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvNoRooms;
    private RecyclerView rvRooms;
    private RoomAdapter adapter;
    private List<Room> roomList = new ArrayList<>();
    private HMSController controller;
    private String loggedInStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        tvWelcome = findViewById(R.id.tvWelcomeStudent);
        tvNoRooms = findViewById(R.id.tvNoRooms);
        rvRooms = findViewById(R.id.rvRooms);
        Button btnMyApplications = findViewById(R.id.btnMyApplications);

        controller = new HMSController();

        Toolbar toolbar = findViewById(R.id.studentToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        // Get student id from login
        loggedInStudentId = getIntent().getStringExtra("studentId");

        tvWelcome.setText("Welcome, Student");

        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RoomAdapter(
                this,
                roomList,
                controller,
                UserRole.STUDENT,
                loggedInStudentId
        );

        rvRooms.setAdapter(adapter);

//        btnMyApplications.setOnClickListener(v ->
//                startActivity(
//                        new Intent(this, StudentApplicationsActivity.class)
//                                .putExtra("studentId", loggedInStudentId)
//                )
//        );

        loadAvailableRooms();
    }

    private void loadAvailableRooms() {
        controller.viewAvailableRooms(rooms -> {
            roomList.clear();
            roomList.addAll(rooms);
            adapter.notifyDataSetChanged();

            if (rooms.isEmpty()) {
                tvNoRooms.setVisibility(View.VISIBLE);
                rvRooms.setVisibility(View.GONE);
            } else {
                tvNoRooms.setVisibility(View.GONE);
                rvRooms.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            startActivity(new Intent(this, StudentLoginActivity.class));
            finish();
            return true;
        }

        if (id == R.id.menu_edit_profile) {
//            startActivity(
//                    new Intent(this, EditProfileActivity.class)
//                            .putExtra("studentId", loggedInStudentId)
//            );
//            return true;

            Toast.makeText(this, "Edit profile clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.menu_theme) {
            // Toggle theme
            int currentNightMode = AppCompatDelegate.getDefaultNightMode();

            if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "Light mode activated", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "Dark mode activated", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
