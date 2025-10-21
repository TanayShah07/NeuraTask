package com.neeravtanay.neuratask.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public static CollectionReference getAssignmentsCollection() {
        return db.collection("users").document(getCurrentUserId()).collection("assignments");
    }

    public static CollectionReference getNotificationsCollection() {
        return db.collection("users").document(getCurrentUserId()).collection("notifications");
    }

    public static FirebaseFirestore getDatabase() {
        return db;
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
