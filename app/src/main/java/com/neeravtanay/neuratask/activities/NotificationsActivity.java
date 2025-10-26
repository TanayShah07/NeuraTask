package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.adapters.NotificationsAdapter;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.models.NotificationModel;
import com.neeravtanay.neuratask.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private NotificationsAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        RecyclerView rv = findViewById(R.id.rvNotifications);
        Button clearBtn = findViewById(R.id.btnClearAll);

        rv.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        db = AppDatabase.getInstance(this);

        adapter = new NotificationsAdapter(this, new ArrayList<>(), notification -> {
            db.notificationDao().markAsRead(notification.id);
            notification.read = true;
            adapter.notifyDataSetChanged();
        });
        rv.setAdapter(adapter);

        // Create notification channel for push notifications
        NotificationHelper.createChannel(this);

        // Load Firestore notifications if user is logged in
        if (auth.getCurrentUser() != null) {
            loadFirestoreNotifications();
        } else {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Observe Room DB notifications
        db.notificationDao().getAll().observe(this, new Observer<List<NotificationModel>>() {
            @Override
            public void onChanged(List<NotificationModel> notifications) {
                if (notifications == null) notifications = new ArrayList<>();
                adapter.setNotifications(notifications);  // <-- Use the correct method in your adapter
            }
        });

        // Clear all notifications
        clearBtn.setOnClickListener(v -> db.notificationDao().clearAll());
    }

    private void loadFirestoreNotifications() {
        String uid = auth.getCurrentUser().getUid();
        firestore.collection("notifications")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(this::handleFirestoreNotifications)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show()
                );
    }

    private void handleFirestoreNotifications(QuerySnapshot querySnapshot) {
        List<NotificationModel> notifications = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot) {
            Map<String, Object> data = doc.getData();
            if (data != null && data.containsKey("message")) {
                notifications.add(new NotificationModel(
                        doc.getId(),
                        "Notification",
                        data.get("message").toString(),
                        System.currentTimeMillis(),
                        false
                ));
            }
        }
        if (!notifications.isEmpty()) {
            adapter.setNotifications(notifications);
        }
    }
}
