package com.example.hostelmanagementsystem.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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

}
