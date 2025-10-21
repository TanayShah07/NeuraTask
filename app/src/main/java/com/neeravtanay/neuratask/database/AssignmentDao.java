package com.neeravtanay.neuratask.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.neeravtanay.neuratask.models.AssignmentModel;
import java.util.List;

@Dao
public interface AssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AssignmentModel a);
    @Update
    void update(AssignmentModel a);
    @Query("SELECT * FROM assignments ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getAll();
    @Query("SELECT * FROM assignments WHERE isCompleted = 0 AND dueTimestamp >= :now ORDER BY priorityScore DESC, dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getPending(long now);
    @Query("SELECT * FROM assignments WHERE isCompleted = 1 ORDER BY dueTimestamp DESC")
    LiveData<List<AssignmentModel>> getCompleted();
    @Query("SELECT * FROM assignments WHERE isCompleted = 0 AND dueTimestamp < :now ORDER BY dueTimestamp ASC")
    LiveData<List<AssignmentModel>> getOverdue(long now);
    @Query("SELECT * FROM assignments WHERE synced = 0")
    List<AssignmentModel> getUnsynced();
}
