package com.example.hostelmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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

    private TextView tvWelcome;
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
        rvRooms = findViewById(R.id.rvRooms);
        Button btnMyApplications = findViewById(R.id.btnMyApplications);

        controller = new HMSController();

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
        });
    }
}
