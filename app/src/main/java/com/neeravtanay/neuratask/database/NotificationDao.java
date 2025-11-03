package com.neeravtanay.neuratask.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.neeravtanay.neuratask.models.NotificationModel;
import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    void insert(NotificationModel notification);

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    LiveData<List<NotificationModel>> getAll();

    // NEW: get notifications for a specific user
    @Query("SELECT * FROM notifications WHERE userId = :ownerId ORDER BY timestamp DESC")
    LiveData<List<NotificationModel>> getAllForUser(String ownerId);

    @Query("UPDATE notifications SET read = 1 WHERE id = :id")
    void markAsRead(String id);

    @Query("DELETE FROM notifications")
    void clearAll();

    // NEW: delete notifications tied to an assignment owner or id if desired
    @Query("DELETE FROM notifications WHERE userId = :ownerId")
    void deleteAllForUser(String ownerId);
}
