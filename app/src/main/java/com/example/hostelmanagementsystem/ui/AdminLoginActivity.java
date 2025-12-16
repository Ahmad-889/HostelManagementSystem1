package com.example.hostelmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.enums.UserRole;
public class AdminLoginActivity extends AppCompatActivity{

    HMSController controller = new HMSController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        EditText email = findViewById(R.id.etEmail);
        EditText password = findViewById(R.id.etPassword);
        Button loginBtn = findViewById(R.id.btnAdminLogin);

        loginBtn.setOnClickListener(v -> {

            controller.login(
                    email.getText().toString(),
                    password.getText().toString(),
                    UserRole.ADMIN,
                    success -> {
                        if (success) {
                            Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, AdminDashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid Admin Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }
}
