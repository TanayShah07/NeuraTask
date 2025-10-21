package com.neeravtanay.neuratask.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "assignments")
public class Assignment implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String priority;
    private String deadlineDate;
    private String deadlineTime;
    private boolean completed;
    private boolean beyondDeadline;
    private String firestoreId;

    public Assignment(String title, String description, String priority, String deadlineDate, String deadlineTime, boolean completed, boolean beyondDeadline, String firestoreId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadlineDate = deadlineDate;
        this.deadlineTime = deadlineTime;
        this.completed = completed;
        this.beyondDeadline = beyondDeadline;
        this.firestoreId = firestoreId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(String deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isBeyondDeadline() {
        return beyondDeadline;
    }

    public void setBeyondDeadline(boolean beyondDeadline) {
        this.beyondDeadline = beyondDeadline;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }
}
