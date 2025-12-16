package com.example.hostelmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.model.HostelApplication;
import com.example.hostelmanagementsystem.ui.adapter.ApplicationAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private CardView btnApplications, btnRooms;
    private RecyclerView rvApplications;
    private ApplicationAdapter adapter;
    private List<HostelApplication> applicationsList = new ArrayList<>();
    private HMSController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnApplications = findViewById(R.id.btnApplications);
        btnRooms = findViewById(R.id.btnRooms);
        rvApplications = findViewById(R.id.rvApplications);

        LinearLayout innerLayout = (LinearLayout) btnApplications.getChildAt(0);
        TextView tvApplicationsTitle = (TextView) innerLayout.getChildAt(0);

        controller = new HMSController();

        // Setup RecyclerView
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplicationAdapter(this, applicationsList, controller);
        rvApplications.setAdapter(adapter);


        // Load all applications and update UI
        loadApplications();

        // Handle Rooms button
        btnRooms.setOnClickListener(v -> startActivity(new Intent(this, AdminRoomsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
    }

    private void loadApplications() {
        controller.viewPendingApplications(apps -> {
            applicationsList.clear();
            applicationsList.addAll(apps);
            adapter.notifyDataSetChanged();

            // Update Applications Card title safely
            String buttonText = "All Applications";
            if (apps.size() > 0) {
                buttonText += " (" + apps.size() + ")";
            }

            // Update TextView in CardView
            if (btnApplications.getChildCount() > 0) {
                View innerLayout = btnApplications.getChildAt(0);
                if (innerLayout instanceof LinearLayout && ((LinearLayout) innerLayout).getChildCount() > 0) {
                    View textView = ((LinearLayout) innerLayout).getChildAt(0);
                    if (textView instanceof TextView) {
                        ((TextView) textView).setText(buttonText);
                    }
                }
            }
        });
    }

}
