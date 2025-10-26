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

    // 🔹 Insert a new assignment
    @Insert
    long insert(AssignmentModel assignment);

    // 🔹 Update an existing assignment
    @Update
    void update(AssignmentModel assignment);

    // 🔹 Delete an assignment
    @Delete
    void delete(AssignmentModel assignment);

    // 🔹 Delete assignment by ID
    @Query("DELETE FROM assignments WHERE id = :id")
    void deleteById(String id);

    // 🔹 Get all pending assignments for a user (not completed and due >= now)
    @Query("SELECT * FROM assignments WHERE ownerId = :ownerId AND isCompleted = 0 AND dueTimestamp >= :currentTime ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getPending(String ownerId, long currentTime);

    // 🔹 Get all completed assignments for a user
    @Query("SELECT * FROM assignments WHERE ownerId = :ownerId AND isCompleted = :completed ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getCompleted(String ownerId, boolean completed);

    // 🔹 Get all overdue assignments for a user (not completed and due < now)
    @Query("SELECT * FROM assignments WHERE ownerId = :ownerId AND isCompleted = 0 AND dueTimestamp < :currentTime ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getOverdue(String ownerId, long currentTime);

    // 🔹 Get all unsynced assignments for a user
    @Query("SELECT * FROM assignments WHERE ownerId = :ownerId AND synced = 0")
    List<AssignmentModel> getUnsynced(String ownerId);

    // 🔹 Get all pending assignments (non-LiveData)
    @Query("SELECT * FROM assignments WHERE isCompleted = 0 ORDER BY dueTimestamp ASC")
    List<AssignmentModel> getPendingAssignments();

    // 🔹 Get single assignment synchronously (used by NotificationWorker)
    @Query("SELECT * FROM assignments WHERE id = :id LIMIT 1")
    AssignmentModel getAssignmentByIdSync(String id);

    // 🔹 Get all assignments (used for global auto-notifications)
    @Query("SELECT * FROM assignments")
    List<AssignmentModel> getAllAssignmentsSync();
}
