package com.neeravtanay.neuratask.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.database.AssignmentDao;
import com.neeravtanay.neuratask.models.AssignmentModel;

import java.util.List;
import java.util.Map;

public class AssignmentReminderWorker extends Worker {

    public AssignmentReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            AssignmentDao dao = AppDatabase.getInstance(getApplicationContext()).assignmentDao();
            List<AssignmentModel> assignments = dao.getAllAssignmentsSync();

            long now = System.currentTimeMillis();
            long oneDay = 24 * 60 * 60 * 1000L;

            for (AssignmentModel a : assignments) {
                long due = a.getDueTimestamp();

                if (due - now <= oneDay && due - now > 0) {
                    sendNotification("Assignment Due Soon!",
                            a.getTitle() + " is due within 24 hours.");
                } else if (due < now) {
                    sendNotification("Assignment Overdue!",
                            a.getTitle() + " has passed its due date!");
                }
            }

            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void sendNotification(String title, String message) {
        FirebaseFirestore.getInstance()
                .collection("notifications")
                .add(Map.of(
                        "userId", FirebaseAuth.getInstance().getUid(),
                        "title", title,
                        "message", message,
                        "timestamp", System.currentTimeMillis()
                ));
    }
}