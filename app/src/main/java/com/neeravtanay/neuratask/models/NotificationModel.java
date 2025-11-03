package com.neeravtanay.neuratask.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationModel {

    @PrimaryKey
    @NonNull
    public String id;

    public String title;
    public String message;
    public long timestamp;
    public boolean read;
    public String userId;  // 🔹 Add this to separate per-user notifications

    // 🔹 Empty constructor required by Room
    public NotificationModel() {}

    // 🔹 Constructor used in NotificationsActivity
    public NotificationModel(String id, String title, String message, long timestamp, boolean read) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    // 🔹 Optional constructor (with userId, for per-user storage)
    public NotificationModel(String id, String title, String message, long timestamp, boolean read, String userId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
        this.userId = userId;
    }
}
