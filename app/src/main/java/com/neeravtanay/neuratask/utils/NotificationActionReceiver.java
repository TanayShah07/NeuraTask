package com.neeravtanay.neuratask.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", -1);

        if ("ACTION_YES".equals(intent.getAction())) {
            // TODO: Delete task from database here
            Toast.makeText(context, "Task marked completed ✅", Toast.LENGTH_SHORT).show();
        } else if ("ACTION_NO".equals(intent.getAction())) {
            // TODO: Mark task title red in app here
            Toast.makeText(context, "Task not completed ❌", Toast.LENGTH_SHORT).show();
        }

        // Cancel the notification after action
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(notificationId);
        }
    }
}
