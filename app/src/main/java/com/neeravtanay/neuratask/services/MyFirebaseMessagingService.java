package com.neeravtanay.neuratask.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.activities.HomeActivity;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.models.NotificationModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "neuratask_notifications";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = "NeuraTask";
        String body = "You have a new update";

        // 🔹 Handle standard notification payload
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null)
                title = remoteMessage.getNotification().getTitle();
            if (remoteMessage.getNotification().getBody() != null)
                body = remoteMessage.getNotification().getBody();
        }

        // 🔹 Handle data payload
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            if (data.get("title") != null) title = data.get("title");
            if (data.get("body") != null) body = data.get("body");
        }

        // 🔹 Display local notification
        showNotification(title, body);

        // 🔹 Save notification to Firestore (if user logged in)
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", uid);
            notification.put("title", title);
            notification.put("message", body);
            notification.put("timestamp", System.currentTimeMillis());

            FirebaseFirestore.getInstance().collection("notifications")
                    .add(notification)
                    .addOnSuccessListener(docRef -> Log.d(TAG, "✅ Saved notification to Firestore"))
                    .addOnFailureListener(e -> Log.e(TAG, "❌ Failed to save notification to Firestore", e));
        }

        // 🔹 Save to local Room database
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        NotificationModel nm = new NotificationModel(
                UUID.randomUUID().toString(),
                title,
                body,
                System.currentTimeMillis(),
                false,
                uid
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                db.notificationDao().insert(nm);
            } catch (Exception e) {
                Log.e(TAG, "❌ Room insert failed", e);
            }
        });
    }

    private void showNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 🔹 Create notification channel (for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NeuraTask Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("NeuraTask reminders and updates");
            if (manager != null) manager.createNotificationChannel(channel);
        }

        // 🔹 Intent to open HomeActivity when clicked
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // 🔹 Build and show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title != null ? title : "NeuraTask")
                .setContentText(message != null ? message : "You have a new update")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(this)
                .notify((int) (System.currentTimeMillis() % Integer.MAX_VALUE), builder.build());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "🔥 New FCM Token: " + token);

        // 🔹 Save FCM token to Firestore (if user is signed in)
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("fcmToken", token)
                    .addOnSuccessListener(a -> Log.d(TAG, "✅ Token saved to user doc"))
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "⚠️ User doc update failed, merging instead.");
                        Map<String, Object> map = new HashMap<>();
                        map.put("fcmToken", token);
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .set(map, SetOptions.merge());
                    });
        }
    }
}