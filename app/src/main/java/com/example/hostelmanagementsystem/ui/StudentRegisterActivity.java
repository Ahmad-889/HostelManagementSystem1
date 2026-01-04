package com.example.hostelmanagementsystem.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;

public class StudentRegisterActivity extends AppCompatActivity {

    HMSController controller = new HMSController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        EditText regNo = findViewById(R.id.etRegNo);
        EditText name = findViewById(R.id.etName);
        EditText pass = findViewById(R.id.etPassword);
        Button register = findViewById(R.id.btnRegister);

        // Find the TextView
        TextView tvLogin = findViewById(R.id.tvLogin);

// Full text
        String fullText = "Already have an account? Login here";

// Part to color
        String coloredPart = "Login here";

// Create SpannableString
        SpannableString spannable = new SpannableString(fullText);

// Get start and end indices
        int start = fullText.indexOf(coloredPart);
        int end = start + coloredPart.length();

// Apply color using theme color (colorPrimary)
        spannable.setSpan(new ForegroundColorSpan(
                        getResources().getColor(R.color.colorPrimary)), // teal color
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

// Set the Spannable to the TextView
        tvLogin.setText(spannable);


        register.setOnClickListener(v -> {
            controller.registerStudent(
                    regNo.getText().toString(),
                    name.getText().toString(),
                    pass.getText().toString(),
                    (success, message) -> {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            showNotification();
                            finish(); // return to login
                        }
                    }
            );
        });

        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, StudentLoginActivity.class)));

    }

    private void showNotification() {
        String channelId = "HMS";

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(
                    new NotificationChannel(channelId, "HMS", NotificationManager.IMPORTANCE_DEFAULT));
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Hostel Management System")
                        .setContentText("Account Created Successfully");

        manager.notify(1, builder.build());
    }
}
