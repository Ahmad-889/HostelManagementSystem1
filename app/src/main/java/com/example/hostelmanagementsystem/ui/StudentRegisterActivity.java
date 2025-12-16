package com.example.hostelmanagementsystem.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
