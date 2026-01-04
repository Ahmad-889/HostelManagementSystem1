package com.example.hostelmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.enums.UserRole;
import com.example.hostelmanagementsystem.model.Room;
import com.example.hostelmanagementsystem.ui.adapter.RoomAdapter;
import com.example.hostelmanagementsystem.ui.helpers.AddUpdateRoomDialog;

import java.util.ArrayList;
import java.util.List;

public class AdminRoomsActivity extends AppCompatActivity {
    private RecyclerView rvRooms;
    private RoomAdapter adapter;
    private List<Room> roomList = new ArrayList<>();
    private HMSController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rooms);

        rvRooms = findViewById(R.id.rvRooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        controller = new HMSController();
        adapter = new RoomAdapter(this, roomList, controller, UserRole.ADMIN,null);
        rvRooms.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.adminToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddRoom).setOnClickListener(v -> {
            AddUpdateRoomDialog dialog = new AddUpdateRoomDialog(this, controller, null);
            dialog.show();
        });

        loadRooms();
    }

    public void loadRooms() {
        controller.viewAllRooms(rooms -> {
            roomList.clear();
            roomList.addAll(rooms);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_rooms, menu);
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

        if (id == R.id.menu_portfolio) {
            Intent intent = new Intent(this, PortfolioActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_theme) {
            int currentNightMode = AppCompatDelegate.getDefaultNightMode();

            if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveThemeMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "Light mode activated", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveThemeMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "Dark mode activated", Toast.LENGTH_SHORT).show();
            }

            recreate();
            return true;
        }

        if (id == R.id.menu_add_sample_rooms) {

            addSampleRooms();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addSampleRooms() {
        controller.addSampleRooms(success -> {
            if (success) {
                Toast.makeText(this, "Sample rooms added successfully", Toast.LENGTH_SHORT).show();
                loadRooms(); // Refresh the list
            } else {
                Toast.makeText(this, "Failed to add sample rooms", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveThemeMode(int mode) {
        getSharedPreferences("app_settings", MODE_PRIVATE)
                .edit()
                .putInt("theme_mode", mode)
                .apply();
    }

}
