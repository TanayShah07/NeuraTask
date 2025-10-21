package com.neeravtanay.neuratask.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.models.AssignmentModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

public class AssignmentRepository {
    private final AppDatabase db;
    private final FirebaseFirestore fs;
    public AssignmentRepository(Context ctx) {
        db = AppDatabase.getInstance(ctx);
        fs = FirebaseFirestore.getInstance();
    }
    public LiveData<List<AssignmentModel>> getPending() {
        return db.assignmentDao().getPending(System.currentTimeMillis());
    }
    public LiveData<List<AssignmentModel>> getCompleted() {
        return db.assignmentDao().getCompleted();
    }
    public LiveData<List<AssignmentModel>> getOverdue() {
        return db.assignmentDao().getOverdue(System.currentTimeMillis());
    }
    public void insert(AssignmentModel a) {
        if (a.id == null) a.id = UUID.randomUUID().toString();
        a.synced = false;
        a.ownerId = a.ownerId == null ? "anon" : a.ownerId;
        Executors.newSingleThreadExecutor().execute(() -> db.assignmentDao().insert(a));
        syncToFirestore(a);
    }
    public void update(AssignmentModel a) {
        a.synced = false;
        Executors.newSingleThreadExecutor().execute(() -> db.assignmentDao().update(a));
        syncToFirestore(a);
    }
    private void syncToFirestore(AssignmentModel a) {
        Map<String,Object> map = new HashMap<>();
        map.put("id", a.id);
        map.put("title", a.title);
        map.put("description", a.description);
        map.put("dueTimestamp", a.dueTimestamp);
        map.put("isCompleted", a.isCompleted);
        map.put("priorityManual", a.priorityManual);
        map.put("estimatedMinutes", a.estimatedMinutes);
        map.put("ownerId", a.ownerId);
        fs.collection("assignments").document(a.id).set(map, SetOptions.merge()).addOnSuccessListener(unused -> {
            a.synced = true;
            Executors.newSingleThreadExecutor().execute(() -> db.assignmentDao().update(a));
        });
    }
    public void attemptSyncUnsynced() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AssignmentModel> list = db.assignmentDao().getUnsynced();
            for (AssignmentModel a : list) syncToFirestore(a);
        });
    }
}
