package com.example.hostelmanagementsystem.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.data.FakeDatabase;
import com.example.hostelmanagementsystem.model.Student;

public class ApplyHostelActivity extends AppCompatActivity {
    HMSController controller = new HMSController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_hostel);

        EditText etSemester = findViewById(R.id.etSemester);
        EditText etCGPA = findViewById(R.id.etCGPA);
        Button btnSubmit = findViewById(R.id.btnSubmitApplication);

        btnSubmit.setOnClickListener(v -> {

            Student student = FakeDatabase.students.get(0); // logged-in student

//            boolean success = controller.submitApplication(
//                    student,
//                    Integer.parseInt(etSemester.getText().toString()),
//                    Float.parseFloat(etCGPA.getText().toString())
//            );

//            if (success) {
//                Toast.makeText(this, "Application Submitted", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                Toast.makeText(this, "You already have a pending application", Toast.LENGTH_LONG).show();
//            }
        });
    }
}
