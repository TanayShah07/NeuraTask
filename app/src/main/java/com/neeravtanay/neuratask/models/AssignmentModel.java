package com.neeravtanay.neuratask.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "assignments")
public class AssignmentModel {

    @PrimaryKey
    @NonNull
    private String id;

    private String title;
    private String description;
    private String subject;
    private long dueTimestamp;
    private int priorityManual;
    private int estimatedMinutes;
    private boolean isCompleted;
    private boolean synced;
    private String ownerId;
    private float priorityScore;

    // ✅ Default constructor (for Room + Firestore)
    public AssignmentModel() {
        this.id = UUID.randomUUID().toString();
    }

    // ✅ Full constructor
    public AssignmentModel(@NonNull String id, String title, String description, String subject,
                           long dueTimestamp, int priorityManual, int estimatedMinutes,
                           boolean isCompleted, boolean synced, String ownerId, float priorityScore) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.dueTimestamp = dueTimestamp;
        this.priorityManual = priorityManual;
        this.estimatedMinutes = estimatedMinutes;
        this.isCompleted = isCompleted;
        this.synced = synced;
        this.ownerId = ownerId;
        this.priorityScore = priorityScore;
    }

    // 🔹 Getters & Setters

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getDueTimestamp() {
        return dueTimestamp;
    }

    public void setDueTimestamp(long dueTimestamp) {
        this.dueTimestamp = dueTimestamp;
    }

    public int getPriorityManual() {
        return priorityManual;
    }

    public void setPriorityManual(int priorityManual) {
        this.priorityManual = priorityManual;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public float getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(float priorityScore) {
        this.priorityScore = priorityScore;
    }
}
