package com.neeravtanay.neuratask.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.database.AssignmentDao;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.utils.AIHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssignmentRepository {

    private final AssignmentDao dao;
    private final String userId;
    private final ExecutorService executor;
    private final Application app;

    public AssignmentRepository(Application app) {
        this.app = app;
        AppDatabase db = AppDatabase.getInstance(app);
        dao = db.assignmentDao();
        executor = Executors.newSingleThreadExecutor();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anon";
    }

    // 🔹 Get pending assignments for current user
    public LiveData<List<AssignmentModel>> getPending() {
        return dao.getPending(userId, System.currentTimeMillis());
    }

    // 🔹 Get completed assignments for current user
    public LiveData<List<AssignmentModel>> getCompleted() {
        return dao.getCompleted(userId, true);
    }

    // 🔹 Get overdue assignments for current user
    public LiveData<List<AssignmentModel>> getOverdue() {
        return dao.getOverdue(userId, System.currentTimeMillis());
    }

    // 🔹 Insert assignment with AI score
    public void insert(AssignmentModel assignment) {
        executor.execute(() -> {
            assignment.setOwnerId(userId);
            assignment.setPriorityScore(AIHelper.computePriorityScore(assignment));
            dao.insert(assignment);
        });
    }

    // 🔹 Update assignment
    public void update(AssignmentModel assignment) {
        executor.execute(() -> dao.update(assignment));
    }

    // 🔹 Delete assignment (also cancels its notifications)
    public void delete(AssignmentModel assignment) {
        executor.execute(() -> {
            WorkManager.getInstance(app).cancelAllWorkByTag(assignment.getId());
            dao.delete(assignment);
        });
    }

    // 🔹 Optional Firestore sync logic (kept safe)
    public void attemptSyncUnsynced() {
        executor.execute(() -> {
            List<AssignmentModel> unsynced = dao.getUnsynced(userId);
            for (AssignmentModel a : unsynced) {
                // Firestore sync logic could go here...
                // Example:
                // FirestoreHelper.syncAssignment(a);
                a.setSynced(true);
                dao.update(a);
            }
        });
    }
}
