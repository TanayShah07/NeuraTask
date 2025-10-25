package com.neeravtanay.neuratask.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.neeravtanay.neuratask.models.AssignmentModel;
import java.util.List;

@Dao
public interface AssignmentDao {

    // 🔹 Get all pending assignments for a specific user (not completed and due >= now)
    @Query("SELECT * FROM assignments WHERE ownerId = :ownerId AND isCompleted = 0 AND dueTimestamp >= :currentTime ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getPending(String ownerId, long currentTime);

    // 🔹 Get all completed assignments for a specific user
    @Query("SELECT * FROM assignments WHERE ownerId = :ownerId AND isCompleted = :completed ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getCompleted(String ownerId, boolean completed);

    // 🔹 Get all overdue assignments for a specific user (not completed and due < now)
    @Query("SELECT * FROM assignments WHERE ownerId = :ownerId AND isCompleted = 0 AND dueTimestamp < :currentTime ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getOverdue(String ownerId, long currentTime);

    // 🔹 Get all unsynced assignments for a specific user
    @Query("SELECT * FROM assignments WHERE ownerId = :ownerId AND synced = 0")
    List<AssignmentModel> getUnsynced(String ownerId);

    @Insert
    void insert(AssignmentModel assignment);

    @Update
    void update(AssignmentModel assignment);

    @Delete
    void delete(AssignmentModel assignment);
}
