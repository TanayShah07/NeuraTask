package com.neeravtanay.neuratask.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.dao.AssignmentDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssignmentRepository {

    private final AssignmentDao dao;
    private final ExecutorService executor;
    private LiveData<List<AssignmentModel>> pending;
    private LiveData<List<AssignmentModel>> completed;
    private LiveData<List<AssignmentModel>> overdue;

    public AssignmentRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        dao = db.assignmentDao();
        executor = Executors.newSingleThreadExecutor();
    }

    // Get all pending assignments (due >= now and not completed)
    public LiveData<List<AssignmentModel>> getPending() {
        long now = System.currentTimeMillis();
        pending = dao.getPending(now);
        return pending;
    }

    // Get all completed assignments
    public LiveData<List<AssignmentModel>> getCompleted() {
        completed = dao.getCompleted(true);
        return completed;
    }

    // Get all overdue assignments (due < now and not completed)
    public LiveData<List<AssignmentModel>> getOverdue() {
        long now = System.currentTimeMillis();
        overdue = dao.getOverdue(now);
        return overdue;
    }

    // Insert assignment asynchronously
    public void insert(AssignmentModel assignment) {
        executor.execute(() -> dao.insert(assignment));
    }

    // Update assignment asynchronously
    public void update(AssignmentModel assignment) {
        executor.execute(() -> dao.update(assignment));
    }

    // Delete assignment asynchronously
    public void delete(AssignmentModel assignment) {
        executor.execute(() -> dao.delete(assignment));
    }

    // Attempt to sync unsynced assignments (placeholder)
    public void attemptSyncUnsynced() {
        executor.execute(() -> {
            List<AssignmentModel> unsynced = dao.getUnsynced();
            for (AssignmentModel a : unsynced) {
                // Sync logic with server/Firestore goes here
                // After successful sync:
                a.setSynced(true);
                dao.update(a);
            }
        });
    }
}
