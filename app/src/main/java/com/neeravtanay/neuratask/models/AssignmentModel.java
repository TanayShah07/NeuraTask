package com.neeravtanay.neuratask.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "assignments")
public class AssignmentModel implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;

    private String title;
    private String description;
    private String subject;

    private long dueTimestamp;

    private String priority;
    private int priorityManual;
    private int estimatedMinutes;

    @ColumnInfo(name = "isCompleted")  // ensures Room uses correct column name
    private boolean isCompleted;

    private boolean beyondDeadline;
    private boolean synced;
    private String ownerId;
    private float priorityScore;
    private String firestoreId;

    public AssignmentModel() {
        this.id = UUID.randomUUID().toString();
    }

    public AssignmentModel(@NonNull String id, String title, String description, String subject,
                           long dueTimestamp, String priority, int priorityManual, int estimatedMinutes,
                           boolean isCompleted, boolean beyondDeadline, boolean synced,
                           String ownerId, float priorityScore, String firestoreId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.dueTimestamp = dueTimestamp;
        this.priority = priority;
        this.priorityManual = priorityManual;
        this.estimatedMinutes = estimatedMinutes;
        this.isCompleted = isCompleted;
        this.beyondDeadline = beyondDeadline;
        this.synced = synced;
        this.ownerId = ownerId;
        this.priorityScore = priorityScore;
        this.firestoreId = firestoreId;
    }

    // 🔹 Getters & Setters
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public long getDueTimestamp() { return dueTimestamp; }
    public void setDueTimestamp(long dueTimestamp) { this.dueTimestamp = dueTimestamp; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getPriorityManual() { return priorityManual; }
    public void setPriorityManual(int priorityManual) { this.priorityManual = priorityManual; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public boolean isBeyondDeadline() { return beyondDeadline; }
    public void setBeyondDeadline(boolean beyondDeadline) { this.beyondDeadline = beyondDeadline; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public float getPriorityScore() { return priorityScore; }
    public void setPriorityScore(float priorityScore) { this.priorityScore = priorityScore; }

    public String getFirestoreId() { return firestoreId; }
    public void setFirestoreId(String firestoreId) { this.firestoreId = firestoreId; }
}
