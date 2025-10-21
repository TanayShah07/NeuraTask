package com.neeravtanay.neuratask.models;

public class NotificationModel {
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
