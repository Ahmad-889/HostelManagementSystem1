package com.example.hostelmanagementsystem.controller;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hostelmanagementsystem.callbacks.CountCallback;
import com.example.hostelmanagementsystem.data.FakeDatabase;
import com.example.hostelmanagementsystem.enums.Facility;
import com.example.hostelmanagementsystem.enums.RoomType;
import com.example.hostelmanagementsystem.model.Room;
import com.google.firebase.database.*;
import com.example.hostelmanagementsystem.data.FirebaseManager;
import com.example.hostelmanagementsystem.enums.ApplicationStatus;
import com.example.hostelmanagementsystem.enums.UserRole;
import com.example.hostelmanagementsystem.model.Admin;
import com.example.hostelmanagementsystem.model.HostelApplication;
import com.example.hostelmanagementsystem.model.Notification;
import com.example.hostelmanagementsystem.model.Student;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

 import java.io.BufferedInputStream;
 import java.io.IOException;
 import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
 import java.net.SocketTimeoutException;
 import java.net.URL;
 import java.util.Scanner;
 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;
 import android.os.AsyncTask;
 import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class HMSController {

    private FirebaseManager firebaseManager = new FirebaseManager();
    public void login(
            String username,
            String password,
            UserRole role,
            LoginCallback callback) {

        DatabaseReference ref;

        if (role == UserRole.STUDENT) {
            ref = FirebaseManager.getRootRef().child("students");
        } else {
            ref = FirebaseManager.getRootRef().child("admins");
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {

                    String dbUsername;
                    String dbPassword;

                    if (role == UserRole.STUDENT) {
                        dbUsername = child.child("registrationNo").getValue(String.class);
                    } else {
                        dbUsername = child.child("email").getValue(String.class);
                    }

                    dbPassword = child.child("password").getValue(String.class);

                    if (username.equals(dbUsername) && password.equals(dbPassword)) {
                        callback.onResult(true);
                        return;
                    }
                }
                callback.onResult(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onResult(false);
            }
        });
    }


    // Get student by ID
    public void getStudentById(String studentId, StudentCallback callback) {
        FirebaseManager.getRootRef().child("students").child(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Student student = snapshot.getValue(Student.class);
                        callback.onResult(student);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onResult(null);
                    }
                });
    }
    public void registerStudent(String regNo, String name, String password, RegisterCallback callback) {
        DatabaseReference studentsRef = FirebaseManager.getRootRef().child("students");

        // Check if registration number already exists
        studentsRef.orderByChild("registrationNo").equalTo(regNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Registration number already used
                            callback.onResult(false, "Registration number already exists");
                        } else {
                            // Create new student object
                            String studentId = studentsRef.push().getKey(); // unique Firebase ID

                            Student student = new Student();
                            student.setStudentId(studentId);
                            student.setRegistrationNo(regNo);
                            student.setName(name);
                            student.setPassword(password); // For production, use hashed password

                            // Optional: initialize other fields
                            student.setCgpa(0.0f);
                            student.setSemester(1);
                            student.setIsEligible(true);

                            // Save to Firebase
                            studentsRef.child(studentId).setValue(student)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            callback.onResult(true, "Student registered successfully");
                                        } else {
                                            callback.onResult(false, "Failed to register student");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(false, error.getMessage());
                    }
                });
    }

    // View pending applications
    public void viewPendingApplications(ApplicationListCallback callback) {
        FirebaseManager.getRootRef()
                .child("applications")
                .orderByChild("status")
                .equalTo(ApplicationStatus.PENDING.name())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<HostelApplication> apps = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            HostelApplication app = child.getValue(HostelApplication.class);
                            if (app != null) {
                                apps.add(app);
                            }
                        }
                        callback.onResult(apps);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(new ArrayList<>());
                    }
                });
    }

    // View all applications
    public void viewAllApplications(ApplicationListCallback callback) {
        FirebaseManager.getRootRef()
                .child("applications")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<HostelApplication> apps = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            HostelApplication app = child.getValue(HostelApplication.class);
                            if (app != null) {
                                apps.add(app);
                            }
                        }
                        callback.onResult(apps);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(new ArrayList<>());
                    }
                });
    }

    public void approveApplication(Context context, HostelApplication app, OperationCallback callback) {
        app.updateStatus(ApplicationStatus.APPROVED);

        DatabaseReference appRef = FirebaseManager.getRootRef()
                .child("applications")
                .child(app.getApplicationId());

        appRef.child("status").setValue(ApplicationStatus.APPROVED.name());
        appRef.child("approvalDate").setValue(System.currentTimeMillis());

        // Allocate student to the room
        String roomId = app.getRequestedRoomId();
        DatabaseReference roomRef = FirebaseManager.getRootRef()
                .child("rooms")
                .child(roomId);

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Room room = snapshot.getValue(Room.class);
                if (room != null) {
                    // Increment occupancy
                    int newOccupancy = room.getCurrentOccupancy() + 1;
                    room.setCurrentOccupancy(newOccupancy);
                    room.setAvailable(newOccupancy < room.getCapacity());

                    // Update room in Firebase
                    roomRef.setValue(room)
                            .addOnSuccessListener(aVoid -> callback.onComplete(true))
                            .addOnFailureListener(e -> callback.onComplete(false));
                } else {
                    callback.onComplete(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onComplete(false);
            }
        });
    }

    public void rejectApplication(Context context, HostelApplication app, String reason, OperationCallback callback) {
        app.setRejection(reason);

        DatabaseReference appRef = FirebaseManager.getRootRef()
                .child("applications")
                .child(app.getApplicationId());

        appRef.child("status").setValue(ApplicationStatus.REJECTED.name());
        appRef.child("rejectionReason").setValue(reason)
                .addOnSuccessListener(aVoid -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    public void submitApplication(
            String studentId,
            Room room,
            int semester,
            float cgpa,
            String parentContact,
            String emergencyContact,
            OperationCallback callback
    ) {
        // Add validation
        if (studentId == null || studentId.isEmpty()) {
            Log.e("HMSController", "Cannot submit application: studentId is null or empty");
            callback.onComplete(false);
            return;
        }

        if (room == null || room.getRoomId() == null || room.getRoomId().isEmpty()) {
            Log.e("HMSController", "Cannot submit application: room or roomId is null or empty");
            callback.onComplete(false);
            return;
        }

        DatabaseReference ref = FirebaseManager.getRootRef().child("applications").push();
        String applicationId = ref.getKey();

        HostelApplication app = new HostelApplication(
                applicationId,
                studentId,
                room.getRoomId()
        );

        // Set additional details
        app.setSemester(semester);
        app.setCgpa(cgpa);
        app.setParentContact(parentContact);
        app.setEmergencyContact(emergencyContact);

        Log.d("HMSController", "Submitting application - StudentID: " + studentId +
                ", RoomID: " + room.getRoomId() + ", AppID: " + applicationId);

        ref.setValue(app)
                .addOnSuccessListener(aVoid -> {
                    Log.d("HMSController", "Application submitted successfully");
                    callback.onComplete(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("HMSController", "Failed to submit application: " + e.getMessage());
                    callback.onComplete(false);
                });
    }

    public void getAllApplicationsCount(CountCallback callback) {
        firebaseManager.getAllApplicationsCount(callback);
    }
    public void createDefaultAdmin() {

        DatabaseReference adminsRef =
                FirebaseManager.getRootRef().child("admins");

        // Check if any admin already exists
        adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    return; // Admin already present
                }

                // Create admin like student (push)
                DatabaseReference newAdminRef = adminsRef.push();
                String adminId = newAdminRef.getKey();

                Admin admin = new Admin(
                        adminId,
                        "Super Admin",
                        "admin@hms.com",
                        "03000000000",
                        "Admin@123"
                );

                newAdminRef.setValue(admin);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    // Add a new room
    public void addRoom(Room room, OperationCallback callback) {
        DatabaseReference ref = FirebaseManager.getRootRef().child("rooms").push();
        ref.setValue(room)
                .addOnSuccessListener(aVoid -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    // Update an existing room
    public void updateRoom(Room room, OperationCallback callback) {
        DatabaseReference ref = FirebaseManager.getRootRef().child("rooms").child(room.getRoomId());
        ref.setValue(room)
                .addOnSuccessListener(aVoid -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    // Remove a room
    public void removeRoom(String roomId, OperationCallback callback) {
        DatabaseReference ref = FirebaseManager.getRootRef().child("rooms").child(roomId);
        ref.removeValue()
                .addOnSuccessListener(aVoid -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    // Get all rooms
    public void viewAllRooms(RoomListCallback callback) {
        FirebaseManager.getRootRef().child("rooms")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Room> rooms = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Room room = child.getValue(Room.class);
                            room.setRoomId(child.getKey());
                            rooms.add(room);
                        }
                        callback.onResult(rooms);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onResult(new ArrayList<>());
                    }
                });
    }
    // Get room by ID
    public void getRoomById(String roomId, RoomCallback callback) {
        FirebaseManager.getRootRef().child("rooms").child(roomId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Room room = snapshot.getValue(Room.class);
                        if (room != null) {
                            room.setRoomId(snapshot.getKey());
                        }
                        callback.onResult(room);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onResult(null);
                    }
                });
    }
    //View available rooms
    public void viewAvailableRooms(RoomListCallback callback) {
        FirebaseManager.getRootRef()
                .child("rooms")
                .orderByChild("available")
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Room> rooms = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Room room = ds.getValue(Room.class);
                            if (room != null) {
                                room.setRoomId(ds.getKey()); // IMPORTANT: Set the roomId
                                room.setAvailable(room.getCurrentOccupancy() < room.getCapacity());
                                rooms.add(room);
                            }
                        }
                        callback.onResult(rooms);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onResult(new ArrayList<>());
                    }
                });
    }
    //Get Room Counts
    public void getAllRoomsCount(CountCallback callback) {
        firebaseManager.getAllRoomsCount(callback);
    }

    public void viewStudentApplications(
            String studentId,
            ApplicationListCallback callback
    ) {
        FirebaseManager.getRootRef()
                .child("applications")
                .orderByChild("studentId")
                .equalTo(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<HostelApplication> list = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            HostelApplication app = ds.getValue(HostelApplication.class);
                            list.add(app);
                        }
                        callback.onResult(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(new ArrayList<>());
                    }
                });
    }

    public void addSampleRooms(OperationCallback callback) {
        String apiUrl = "https://jsonplaceholder.typicode.com/posts?_limit=2";
        new AddSampleRoomsAsyncTask(callback).execute(apiUrl);
    }

    // AsyncTask class for consuming JSON web service
    private class AddSampleRoomsAsyncTask extends AsyncTask<String, Void, String> {

        private OperationCallback callback;

        public AddSampleRoomsAsyncTask(OperationCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... urls) {
            return requestWebService(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                parseAndSaveRooms(result);
            } else {
                callback.onComplete(false);
            }
        }
    }

    // Method to request data from web service using HttpURLConnection
    private static String requestWebService(String serviceUrl) {
        HttpURLConnection urlConnection = null;
        try {
            // Create connection
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();

            // Set timeouts
            urlConnection.setConnectTimeout(15000);  // 15 seconds
            urlConnection.setReadTimeout(10000);     // 10 seconds

            // Get JSON data
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            // Convert InputStream into String
            Scanner scanner = new Scanner(in);
            String strJSON = scanner.useDelimiter("\\A").next();
            scanner.close();

            return strJSON;

        } catch (MalformedURLException e) {
            Log.e("HMSController", "URL is invalid: " + e.getMessage());
        } catch (SocketTimeoutException e) {
            Log.e("HMSController", "Data retrieval timed out: " + e.getMessage());
        } catch (IOException e) {
            Log.e("HMSController", "Could not read response body: " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    // Method to parse JSON and save rooms to Firebase
    private void parseAndSaveRooms(String jsonResponse) {
        try {
            JSONArray rootArray = new JSONArray(jsonResponse);
            List<Room> rooms = new ArrayList<>();

            // Parse JSON array and create Room objects
            for (int i = 0; i < rootArray.length(); i++) {
                JSONObject jsonObject = rootArray.getJSONObject(i);

                // Extract data from API response
                int id = jsonObject.optInt("id", i + 1);
                int userId = jsonObject.optInt("userId", 1);

                // Map API data to Room object
                String roomNumber = "Room-" + id;
                String hostelBlock = "Block-" + ((userId % 3) + 1);
                int floor = (id % 4) + 1;
                int capacity = (userId % 2) + 2;  // 2 or 3
                RoomType roomType = capacity == 2 ? RoomType.SINGLE : RoomType.SHARED;
                double hostelRent = 5000.0 + (id * 500);

                // Create facilities list
                List<Facility> facilities = new ArrayList<>();
                facilities.add(Facility.AC);
                facilities.add(Facility.WIFI);
                if (capacity == 2) {
                    facilities.add(Facility.ATTACHED_BATH);
                }
                facilities.add(Facility.HEATER);
                facilities.add(Facility.STUDY_TABLE);

                // Create Room object
                Room room = new Room(
                        roomNumber,
                        hostelBlock,
                        floor,
                        capacity,
                        roomType,
                        hostelRent,
                        facilities
                );

                rooms.add(room);
                Log.d("HMSController", "Room created: " + room.toString());
            }

            // Save all rooms to Firebase
            saveMultipleRooms(rooms);

        } catch (JSONException e) {
            Log.e("HMSController", "JSON parsing failed: " + e.getMessage());
        }
    }

    // Method to save multiple rooms to Firebase database
    private void saveMultipleRooms(List<Room> rooms) {
        final int[] successCount = {0};
        final int totalRooms = rooms.size();

        if (totalRooms == 0) {
            return;
        }

        for (Room room : rooms) {
            DatabaseReference ref = FirebaseManager.getRootRef().child("rooms").push();
            String roomId = ref.getKey();
            room.setRoomId(roomId);

            ref.setValue(room)
                    .addOnSuccessListener(aVoid -> {
                        successCount[0]++;
                        Log.d("HMSController", "Room saved successfully. Count: " + successCount[0]);

                        if (successCount[0] == totalRooms) {
                            Log.d("HMSController", "All rooms saved successfully!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("HMSController", "Failed to save room: " + e.getMessage());
                    });
        }
    }


    // Callback interfaces

    public interface ApplicationListCallback {
        void onResult(List<HostelApplication> applications);
    }
    public interface OperationCallback {
        void onComplete(boolean success);
    }

    public interface RoomListCallback {
        void onResult(List<Room> rooms);
    }

    public interface StudentCallback {
        void onResult(Student student);
    }

    public interface RoomCallback {
        void onResult(Room room);
    }
}
