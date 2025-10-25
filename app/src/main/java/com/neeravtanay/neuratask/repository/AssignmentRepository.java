package com.neeravtanay.neuratask.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.database.AssignmentDao;
import com.neeravtanay.neuratask.models.AssignmentModel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssignmentRepository {

    private final AssignmentDao dao;
    private final String userId;
    private final ExecutorService executor;

    public AssignmentRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        dao = db.assignmentDao();
        executor = Executors.newSingleThreadExecutor();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anon";
    }

    // Get all pending assignments for current user
    public LiveData<List<AssignmentModel>> getPending() {
        return dao.getPending(userId, System.currentTimeMillis());
    }

    // Get all completed assignments for current user
    public LiveData<List<AssignmentModel>> getCompleted() {
        return dao.getCompleted(userId, true);
    }

    // Get all overdue assignments for current user
    public LiveData<List<AssignmentModel>> getOverdue() {
        return dao.getOverdue(userId, System.currentTimeMillis());
    }

    // Insert assignment asynchronously
    public void insert(AssignmentModel assignment) {
        assignment.setOwnerId(userId);
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

    // Attempt to sync unsynced assignments for current user
    public void attemptSyncUnsynced() {
        executor.execute(() -> {
            List<AssignmentModel> unsynced = dao.getUnsynced(userId);
            for (AssignmentModel a : unsynced) {
                // Firestore sync logic goes here...
                // After successful sync:
                a.setSynced(true);
                dao.update(a);
            }
        });
    }
}
