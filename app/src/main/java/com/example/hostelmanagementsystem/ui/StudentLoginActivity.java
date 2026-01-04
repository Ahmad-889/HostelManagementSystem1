package com.example.hostelmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.data.FirebaseManager;
import com.example.hostelmanagementsystem.enums.UserRole;

public class StudentLoginActivity extends AppCompatActivity {
    HMSController controller = new HMSController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        controller.createDefaultAdmin();

        EditText regNo = findViewById(R.id.etRegNo);
        EditText password = findViewById(R.id.etPassword);
        Button login = findViewById(R.id.btnLogin);

        TextView register = findViewById(R.id.tvRegister);
        TextView adminLogin = findViewById(R.id.tvAdminLogin);

        TextView tvRegister = findViewById(R.id.tvRegister);

// Full text
        String fullText = "Don’t have an account? Register here";

// The part we want to color
        String coloredPart = "Register here";

// Create a SpannableString
        SpannableString spannable = new SpannableString(fullText);

// Find start and end indices of the colored part
        int start = fullText.indexOf(coloredPart);
        int end = start + coloredPart.length();

// Apply color
        spannable.setSpan(new ForegroundColorSpan(
                        getResources().getColor(R.color.colorPrimary)), // teal color
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

// Set the text
        tvRegister.setText(spannable);

        login.setOnClickListener(v -> {
            String regNumber = regNo.getText().toString().trim();

            controller.login(
                    regNumber,
                    password.getText().toString(),
                    UserRole.STUDENT,
                    success -> {
                        if (success) {
                            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(
                                    StudentLoginActivity.this,
                                    StudentDashboardActivity.class
                            );
                            intent.putExtra("studentId", regNumber);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        register.setOnClickListener(v ->
                startActivity(new Intent(this, StudentRegisterActivity.class)));

        adminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
