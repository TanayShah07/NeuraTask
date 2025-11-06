package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.adapters.NotificationsAdapter;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.models.NotificationModel;
import com.neeravtanay.neuratask.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private NotificationsAdapter adapter;
    private AppDatabase db;
    private Button clearBtn;
    private final AtomicBoolean clearingInProgress = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        RecyclerView rv = findViewById(R.id.rvNotifications);
        clearBtn = findViewById(R.id.btnClearAll);
        rv.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        db = AppDatabase.getInstance(this);

        adapter = new NotificationsAdapter(this, new ArrayList<>(), notification -> {
            // mark as read off main thread and update UI
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.notificationDao().markAsRead(notification.id);
                // update local object so UI reflects change immediately (observer will update too)
                notification.read = true;
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            });
        });
        rv.setAdapter(adapter);

        // Create notification channel for push notifications (idempotent)
        NotificationHelper.createChannel(this);

        // require login
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 1) Observe Room DB (single source of truth for UI)
        db.notificationDao().getAll().observe(this, new Observer<List<NotificationModel>>() {
            @Override
            public void onChanged(List<NotificationModel> notifications) {
                if (notifications == null) notifications = new ArrayList<>();
                adapter.setNotifications(notifications); // ensure adapter replaces data
            }
        });

        // 2) Listen to Firestore in realtime and upsert into Room (keeps Room and server synced)
        listenAndSyncFirestoreNotifications();

        // 3) Clear all button: clear both Room and Firestore
        clearBtn.setOnClickListener(v -> clearAllNotifications());
    }

    private void listenAndSyncFirestoreNotifications() {
        String uid = auth.getCurrentUser().getUid();
        CollectionReference col = firestore.collection("notifications");
        Query q = col.whereEqualTo("userId", uid).orderBy("timestamp", Query.Direction.DESCENDING);

        // Realtime listener — converts changes to upserts in Room
        q.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Could show a subtle toast/log
                    return;
                }
                if (snapshots == null || snapshots.isEmpty()) {
                    // nothing on server — we keep Room as-is (might be empty)
                    return;
                }

                // Upsert each changed document into Room on background executor
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        DocumentSnapshot doc = dc.getDocument();
                        Map<String, Object> data = doc.getData();
                        if (data == null) continue;

                        String id = doc.getId();
                        String title = data.containsKey("title") ? String.valueOf(data.get("title")) : "Notification";
                        String message = data.containsKey("message") ? String.valueOf(data.get("message")) : "";
                        long timestamp = data.containsKey("timestamp") ? ((Number) data.get("timestamp")).longValue() : System.currentTimeMillis();
                        boolean read = data.containsKey("read") ? Boolean.parseBoolean(String.valueOf(data.get("read"))) : false;

                        // If your NotificationModel constructor requires a userId, pass uid. Adjust if needed.
                        NotificationModel nm = new NotificationModel(id, title, message, timestamp, read, uid);

                        // Upsert (insert or replace) — ensure DAO insert is @Insert(onConflict = REPLACE)
                        try {
                            db.notificationDao().insert(nm);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void clearAllNotifications() {
        if (clearingInProgress.get()) return; // avoid concurrent clears
        clearingInProgress.set(true);
        clearBtn.setEnabled(false);

        String uid = auth.getCurrentUser().getUid();

        // 1) Clear Room DB off main thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.notificationDao().clearAll();

            // 2) Clear Firestore docs for this user (delete them from server so they don't come back)
            firestore.collection("notifications")
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        // Delete in batch to be efficient
                        List<String> docIds = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            docIds.add(doc.getId());
                        }
                        // perform deletes (do in batches of 500 if many; here assume few)
                        for (String docId : docIds) {
                            firestore.collection("notifications").document(docId)
                                    .delete()
                                    .addOnFailureListener(ex -> {
                                        // log but don't block UI
                                        ex.printStackTrace();
                                    });
                        }
                        runOnUiThread(() -> {
                            Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
                            clearBtn.setEnabled(true);
                            clearingInProgress.set(false);
                        });
                    })
                    .addOnFailureListener(ex -> {
                        ex.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Failed to clear server notifications; local cleared", Toast.LENGTH_SHORT).show();
                            clearBtn.setEnabled(true);
                            clearingInProgress.set(false);
                        });
                    });
        });
    }
}