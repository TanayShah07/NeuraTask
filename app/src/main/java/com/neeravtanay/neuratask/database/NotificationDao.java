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

    @Query("UPDATE notifications SET read = 1 WHERE id = :id")
    void markAsRead(String id);

    @Query("DELETE FROM notifications")
    void clearAll();
}
