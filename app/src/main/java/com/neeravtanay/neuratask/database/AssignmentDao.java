package com.neeravtanay.neuratask.dao;

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

    // Get all pending assignments (not completed and due >= now)
    @Query("SELECT * FROM assignments WHERE isCompleted = 0 AND dueTimestamp >= :currentTime ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getPending(long currentTime);

    // Get all completed assignments
    @Query("SELECT * FROM assignments WHERE isCompleted = :completed ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getCompleted(boolean completed);

    // Get all overdue assignments (not completed and due < now)
    @Query("SELECT * FROM assignments WHERE isCompleted = 0 AND dueTimestamp < :currentTime ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getOverdue(long currentTime);

    // Get all unsynced assignments
    @Query("SELECT * FROM assignments WHERE synced = 0")
    List<AssignmentModel> getUnsynced();

    @Insert
    void insert(AssignmentModel assignment);

    @Update
    void update(AssignmentModel assignment);

    @Delete
    void delete(AssignmentModel assignment);
}
