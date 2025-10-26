package com.neeravtanay.neuratask.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.models.NotificationModel;

import java.util.List;
import java.util.UUID;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        String assignmentId = getInputData().getString("assignmentId");

        long now = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000L;

        // 🟢 Handle specific assignment (if triggered for one)
        if (assignmentId != null) {
            AssignmentModel assignment = db.assignmentDao().getAssignmentByIdSync(assignmentId);
            if (assignment == null || assignment.isCompleted()) return Result.success();

            sendAndSaveNotification(db, assignment, now, oneDay);
            return Result.success();
        }

        // 🟡 Otherwise → check all assignments
        List<AssignmentModel> allAssignments = db.assignmentDao().getAllAssignmentsSync();
        if (allAssignments == null || allAssignments.isEmpty()) return Result.success();

        for (AssignmentModel a : allAssignments) {
            if (!a.isCompleted()) {
                sendAndSaveNotification(db, a, now, oneDay);
            }
        }

        return Result.success();
    }

    private void sendAndSaveNotification(AppDatabase db, AssignmentModel a, long now, long oneDay) {
        Context ctx = getApplicationContext();
        long due = a.getDueTimestamp();

        String title = null;
        String message = null;

        if (due - now > oneDay) {
            title = "Assignment Reminder";
            message = "Your assignment \"" + a.getTitle() + "\" is due soon.";
            NotificationHelper.show(ctx, a.getId().hashCode(), title, message);

        } else if (due - now <= oneDay && due - now > 0) {
            title = "Assignment Reminder";
            message = "Your assignment \"" + a.getTitle() + "\" is due within 24 hours!";
            NotificationHelper.show(ctx, a.getId().hashCode(), title, message);

        } else if (Math.abs(due - now) < oneDay / 2) {
            title = "Assignment Due Today";
            message = "Your assignment \"" + a.getTitle() + "\" is due today!";
            NotificationHelper.show(ctx, a.getId().hashCode(), title, message);

        } else if (now > due) {
            title = "Assignment Past Due";
            message = "Have you completed \"" + a.getTitle() + "\"?";
            NotificationHelper.showWithActions(ctx, a.getId().hashCode(), title, message);
        }

        // 🟢 If a notification was actually sent, save it to DB
        if (title != null && message != null) {
            db.notificationDao().insert(new NotificationModel(
                    UUID.randomUUID().toString(),
                    title,
                    message,
                    System.currentTimeMillis(),
                    false
            ));
        }
    }
}
