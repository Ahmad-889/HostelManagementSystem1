package com.example.hostelmanagementsystem.ui.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.enums.Facility;
import com.example.hostelmanagementsystem.model.Room;
import com.example.hostelmanagementsystem.enums.RoomType;

import java.util.ArrayList;
import java.util.List;

public class AddUpdateRoomDialog extends Dialog {
    private Context context;
    private HMSController controller;
    private Room existingRoom;

    private EditText etRoomNumber, etHostelBlock, etFloor, etCapacity, etRent;
    private Spinner spinnerRoomType;
    private CheckBox cbAC, cbWiFi, cbAttachedBath, cbHeater, cbStudyTable;
    private Button btnSave, btnCancel;

    public AddUpdateRoomDialog(Context context, HMSController controller, Room existingRoom) {
        super(context);
        this.context = context;
        this.controller = controller;
        this.existingRoom = existingRoom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_update_room);

        initializeViews();
        setupSpinner();

        if (existingRoom != null) {
            populateFields();
        }

        btnSave.setOnClickListener(v -> saveRoom());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void initializeViews() {
        etRoomNumber = findViewById(R.id.etRoomNumber);
        etHostelBlock = findViewById(R.id.etHostelBlock);
        etFloor = findViewById(R.id.etFloor);
        etCapacity = findViewById(R.id.etCapacity);
        etRent = findViewById(R.id.etRent);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);

        cbAC = findViewById(R.id.cbAC);
        cbWiFi = findViewById(R.id.cbWiFi);
        cbAttachedBath = findViewById(R.id.cbAttachedBath);
        cbHeater = findViewById(R.id.cbHeater);
        cbStudyTable = findViewById(R.id.cbStudyTable);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupSpinner() {
        ArrayAdapter<RoomType> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                RoomType.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(adapter);
    }

    private void populateFields() {
        etRoomNumber.setText(existingRoom.getRoomNumber());
        etHostelBlock.setText(existingRoom.getHostelBlock());
        etFloor.setText(String.valueOf(existingRoom.getFloor()));
        etCapacity.setText(String.valueOf(existingRoom.getCapacity()));
        etRent.setText(String.valueOf(existingRoom.getHostelRent()));

        // Set room type spinner
        int position = 0;
        for (int i = 0; i < RoomType.values().length; i++) {
            if (RoomType.values()[i] == existingRoom.getRoomType()) {
                position = i;
                break;
            }
        }
        spinnerRoomType.setSelection(position);

        // Set facility checkboxes
        if (existingRoom.getFacilities() != null) {
            for (Facility facility : existingRoom.getFacilities()) {
                switch (facility) {
                    case AC:
                        cbAC.setChecked(true);
                        break;
                    case WIFI:
                        cbWiFi.setChecked(true);
                        break;
                    case ATTACHED_BATH:
                        cbAttachedBath.setChecked(true);
                        break;
                    case HEATER:
                        cbHeater.setChecked(true);
                        break;
                    case STUDY_TABLE:
                        cbStudyTable.setChecked(true);
                        break;
                }
            }
        }
    }

    private void saveRoom() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Create Room object
        Room room = new Room();

        if (existingRoom != null) {
            room.setRoomId(existingRoom.getRoomId());
            room.setCurrentOccupancy(existingRoom.getCurrentOccupancy());
        } else {
            room.setCurrentOccupancy(0);
        }

        room.setRoomNumber(etRoomNumber.getText().toString().trim());
        room.setHostelBlock(etHostelBlock.getText().toString().trim());
        room.setFloor(Integer.parseInt(etFloor.getText().toString().trim()));
        room.setCapacity(Integer.parseInt(etCapacity.getText().toString().trim()));
        room.setHostelRent(Double.parseDouble(etRent.getText().toString().trim()));
        room.setRoomType((RoomType) spinnerRoomType.getSelectedItem());

        // Set facilities
        List<Facility> facilities = new ArrayList<>();
        if (cbAC.isChecked()) facilities.add(Facility.AC);
        if (cbWiFi.isChecked()) facilities.add(Facility.WIFI);
        if (cbAttachedBath.isChecked()) facilities.add(Facility.ATTACHED_BATH);
        if (cbHeater.isChecked()) facilities.add(Facility.HEATER);
        if (cbStudyTable.isChecked()) facilities.add(Facility.STUDY_TABLE);
        room.setFacilities(facilities);

        // Set availability based on occupancy
        room.setAvailable(room.getCurrentOccupancy() < room.getCapacity());

        // Save to database
        if (existingRoom != null) {
            controller.updateRoom(room, success -> {
                if (success) {
                    Toast.makeText(context, "Room updated successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                    // Refresh the list
                    if (context instanceof com.example.hostelmanagementsystem.ui.AdminRoomsActivity) {
                        ((com.example.hostelmanagementsystem.ui.AdminRoomsActivity) context).loadRooms();
                    }
                } else {
                    Toast.makeText(context, "Failed to update room", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            controller.addRoom(room, success -> {
                if (success) {
                    Toast.makeText(context, "Room added successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                    // Refresh the list
                    if (context instanceof com.example.hostelmanagementsystem.ui.AdminRoomsActivity) {
                        ((com.example.hostelmanagementsystem.ui.AdminRoomsActivity) context).loadRooms();
                    }
                } else {
                    Toast.makeText(context, "Failed to add room", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateInputs() {
        if (etRoomNumber.getText().toString().trim().isEmpty()) {
            etRoomNumber.setError("Room number is required");
            return false;
        }
        if (etHostelBlock.getText().toString().trim().isEmpty()) {
            etHostelBlock.setError("Hostel block is required");
            return false;
        }
        if (etFloor.getText().toString().trim().isEmpty()) {
            etFloor.setError("Floor is required");
            return false;
        }
        if (etCapacity.getText().toString().trim().isEmpty()) {
            etCapacity.setError("Capacity is required");
            return false;
        }
        if (etRent.getText().toString().trim().isEmpty()) {
            etRent.setError("Rent is required");
            return false;
        }

        try {
            int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
            if (capacity <= 0) {
                etCapacity.setError("Capacity must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            etCapacity.setError("Invalid capacity");
            return false;
        }

        try {
            double rent = Double.parseDouble(etRent.getText().toString().trim());
            if (rent < 0) {
                etRent.setError("Rent cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            etRent.setError("Invalid rent amount");
            return false;
        }

        return true;
    }
}