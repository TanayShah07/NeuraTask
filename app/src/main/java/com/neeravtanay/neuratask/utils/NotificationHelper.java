package com.neeravtanay.neuratask.utils;

import android.Manifest;
import android.annotation.SuppressLint; // ✅ Correct import
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.neeravtanay.neuratask.R;

public class NotificationHelper {
    public static final String CHANNEL_ID = "neuratask_channel";

    /** Create notification channel (required for Android 8+) */
    public static void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel ch = new android.app.NotificationChannel(
                    CHANNEL_ID,
                    "NeuraTask",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            ch.setDescription("NeuraTask reminders");
            android.app.NotificationManager nm = ctx.getSystemService(android.app.NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    /** Simple notification without actions */
    @SuppressLint("MissingPermission")
    public static void show(Context ctx, int id, String title, String text) {
        if (!hasNotificationPermission(ctx)) return;

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true);

        NotificationManagerCompat.from(ctx).notify(id, b.build());
    }

    /** Actionable notification with Yes / No buttons */
    @SuppressLint("MissingPermission")
    public static void showWithActions(Context ctx, int id, String title, String text) {
        if (!hasNotificationPermission(ctx)) return;

        // YES action
        Intent yesIntent = new Intent(ctx, NotificationActionReceiver.class);
        yesIntent.setAction("ACTION_YES");
        yesIntent.putExtra("notificationId", id);
        android.app.PendingIntent yesPending = android.app.PendingIntent.getBroadcast(
                ctx,
                id,
                yesIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_MUTABLE
        );

        // NO action
        Intent noIntent = new Intent(ctx, NotificationActionReceiver.class);
        noIntent.setAction("ACTION_NO");
        noIntent.putExtra("notificationId", id);
        android.app.PendingIntent noPending = android.app.PendingIntent.getBroadcast(
                ctx,
                id + 1000,
                noIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_MUTABLE
        );

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.ic_yes, "Yes", yesPending)
                .addAction(R.drawable.ic_no, "No", noPending)
                .setAutoCancel(true);

        NotificationManagerCompat.from(ctx).notify(id, b.build());
    }

    /** Check notification permission (Android 13+) */
    private static boolean hasNotificationPermission(Context ctx) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED;
    }
}
