package com.neeravtanay.neuratask.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents an in-app notification stored in the local Room database.
 * Also used for displaying notification history and unread counts.
 */
@Entity(tableName = "notifications")
public class NotificationModel {

    @PrimaryKey
    @NonNull
    public String id;

    public String title;
    public String message;
    public long timestamp;
    public boolean read;

    public NotificationModel() {}

    public NotificationModel(String id, String title, String message, long timestamp, boolean read) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }
}
