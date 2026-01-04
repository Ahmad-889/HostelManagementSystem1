package com.example.hostelmanagementsystem.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.model.HostelApplication;
import com.example.hostelmanagementsystem.ui.adapter.ApplicationAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminApplicationsActivity extends AppCompatActivity
        implements ApplicationAdapter.OnApplicationActionListener{

    private List<HostelApplication> applicationList = new ArrayList<>();
    private ApplicationAdapter adapter;
    private HMSController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_applications);

        controller = new HMSController();

        RecyclerView rv = findViewById(R.id.rvApplications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ApplicationAdapter(
                this,
                applicationList,
                controller,
                this
        );
        rv.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadApplications();
    }

    public void loadApplications() {
        controller.viewAllApplications( apps -> {
            applicationList.clear();
            applicationList.addAll(apps);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onApplicationUpdated() {
        loadApplications();
    }
}
