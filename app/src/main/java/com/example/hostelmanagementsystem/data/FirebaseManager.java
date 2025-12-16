package com.example.hostelmanagementsystem.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class FirebaseManager {
    private static FirebaseDatabase database;
    private static DatabaseReference rootRef;

    public static void init() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            rootRef = database.getReference("HMS");
        }
    }

    public static DatabaseReference getRootRef() {
        if (rootRef == null) {
            init(); // safety fallback
        }
        return rootRef;
    }
}
