package com.example.hostelmanagementsystem.ui.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.model.Room;

public class SubmitApplicationDialog extends Dialog {
    private Context context;
    private HMSController controller;
    private Room room;
    private String studentId;
    private OnApplicationSubmittedListener listener;

    private TextView tvRoomInfo;
    private EditText etSemester, etCgpa, etParentContact, etEmergencyContact;
    private Button btnSubmit, btnCancel;

    public interface OnApplicationSubmittedListener {
        void onSubmitted();
    }

    public SubmitApplicationDialog(Context context, HMSController controller,
                                   Room room, String studentId,
                                   OnApplicationSubmittedListener listener) {
        super(context);
        this.context = context;
        this.controller = controller;
        this.room = room;
        this.studentId = studentId;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_submit_application);

        initializeViews();
        setupRoomInfo();

        btnSubmit.setOnClickListener(v -> submitApplication());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void show() {
        super.show();

        if (getWindow() != null) {
            getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void initializeViews() {
        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        etSemester = findViewById(R.id.etSemester);
        etCgpa = findViewById(R.id.etCgpa);
        etParentContact = findViewById(R.id.etParentContact);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupRoomInfo() {
        String info = "Room: " + room.getRoomNumber() +
                ", Block " + room.getHostelBlock() +
                ", Floor " + room.getFloor() +
                " (" + room.getRoomType() + ")";
        tvRoomInfo.setText(info);
    }

    private void submitApplication() {
        if (!validateInputs()) {
            return;
        }

        int semester = Integer.parseInt(etSemester.getText().toString().trim());
        float cgpa = Float.parseFloat(etCgpa.getText().toString().trim());
        String parentContact = etParentContact.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();

        // Check eligibility
        if (cgpa < 2.0f) {
            Toast.makeText(context, "CGPA must be at least 2.0 to be eligible",
                    Toast.LENGTH_LONG).show();
            return;
        }

        controller.submitApplication(
                studentId,
                room,
                semester,
                cgpa,
                parentContact,
                emergencyContact,
                success -> {
                    if (success) {
                        Toast.makeText(context, "Application submitted successfully",
                                Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onSubmitted();
                        }
                        dismiss();
                    } else {
                        Toast.makeText(context, "Failed to submit application",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private boolean validateInputs() {
        if (etSemester.getText().toString().trim().isEmpty()) {
            etSemester.setError("Semester is required");
            return false;
        }
        if (etCgpa.getText().toString().trim().isEmpty()) {
            etCgpa.setError("CGPA is required");
            return false;
        }
        if (etParentContact.getText().toString().trim().isEmpty()) {
            etParentContact.setError("Parent contact is required");
            return false;
        }
        if (etEmergencyContact.getText().toString().trim().isEmpty()) {
            etEmergencyContact.setError("Emergency contact is required");
            return false;
        }

        try {
            int semester = Integer.parseInt(etSemester.getText().toString().trim());
            if (semester <= 0 || semester > 8) {
                etSemester.setError("Semester must be between 1 and 8");
                return false;
            }
        } catch (NumberFormatException e) {
            etSemester.setError("Invalid semester");
            return false;
        }

        try {
            float cgpa = Float.parseFloat(etCgpa.getText().toString().trim());
            if (cgpa < 0 || cgpa > 4.0) {
                etCgpa.setError("CGPA must be between 0 and 4.0");
                return false;
            }
        } catch (NumberFormatException e) {
            etCgpa.setError("Invalid CGPA");
            return false;
        }

        return true;
    }
}