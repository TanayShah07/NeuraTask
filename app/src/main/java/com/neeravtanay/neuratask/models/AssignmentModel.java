package com.neeravtanay.neuratask.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assignments")
public class AssignmentModel {
    @PrimaryKey
    public String id;
    public String title;
    public String description;
    public String subject;
    public long dueTimestamp;
    public int priorityManual;
    public int estimatedMinutes;
    public boolean isCompleted;
    public boolean synced;
    public String ownerId;
    public float priorityScore;
}
