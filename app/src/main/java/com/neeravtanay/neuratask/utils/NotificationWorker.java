package com.neeravtanay.neuratask.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
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
        Context ctx = getApplicationContext();
        NotificationHelper.createChannel(ctx); // ensure channel exists

        AppDatabase db = AppDatabase.getInstance(ctx);
        String assignmentId = getInputData().getString("assignmentId");

        // Determine current signed-in user UID (if any)
        String currentUid = null;
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        } catch (Exception ignored) {}

        long now = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000L;

        // If a specific assignment was provided -> only process it
        if (assignmentId != null) {
            AssignmentModel assignment = db.assignmentDao().getAssignmentByIdSync(assignmentId);
            if (assignment == null) return Result.success();

            // If we have a logged-in user, ensure assignment belongs to them
            if (currentUid != null && !currentUid.equals(assignment.getOwnerId())) {
                return Result.success();
            }

            if (assignment.isCompleted()) return Result.success();

            sendAndSaveNotification(db, assignment, now, oneDay);
            return Result.success();
        }

        // No assignmentId: only process for currently logged-in user (do nothing if no user)
        if (currentUid == null) return Result.success();

        List<AssignmentModel> userAssignments = db.assignmentDao().getAllAssignmentsForUserSync(currentUid);
        if (userAssignments == null || userAssignments.isEmpty()) return Result.success();

        for (AssignmentModel a : userAssignments) {
            if (a == null) continue;
            if (a.isCompleted()) continue;
            sendAndSaveNotification(db, a, now, oneDay);
        }

        return Result.success();
    }

    private void sendAndSaveNotification(AppDatabase db, AssignmentModel a, long now, long oneDay) {
        Context ctx = getApplicationContext();
        long due = a.getDueTimestamp();
        String title = null;
        String message = null;
        long timeLeft = due - now;

        if (timeLeft > oneDay) {
            title = "Assignment Reminder";
            message = "Your assignment \"" + a.getTitle() + "\" is due on " + android.text.format.DateFormat.format("EEE, dd MMM HH:mm", due);
            NotificationHelper.show(ctx, a.getId().hashCode(), title, message);

        } else if (timeLeft <= oneDay && timeLeft > 60 * 60 * 1000L) {
            title = "Due Soon";
            message = "Your assignment \"" + a.getTitle() + "\" is due within 24 hours.";
            NotificationHelper.show(ctx, a.getId().hashCode(), title, message);

        } else if (timeLeft <= 60 * 60 * 1000L && timeLeft > 0) {
            title = "Due in less than 1 hour";
            message = "Hurry — \"" + a.getTitle() + "\" is due soon.";
            NotificationHelper.show(ctx, a.getId().hashCode(), title, message);

        } else if (timeLeft <= 0 && Math.abs(timeLeft) < 60 * 60 * 1000L) {
            title = "Due Now / Overdue";
            message = "Your assignment \"" + a.getTitle() + "\" is due or just passed. Mark complete if finished.";
            NotificationHelper.showWithActions(ctx, a.getId().hashCode(), title, message);

        } else if (timeLeft <= 0 && Math.abs(timeLeft) >= 60 * 60 * 1000L) {
            title = "Assignment Overdue";
            message = "The assignment \"" + a.getTitle() + "\" is overdue. Please update status.";
            NotificationHelper.show(ctx, a.getId().hashCode(), title, message);
        }

        // Save into local DB with ownerId so notifications are per-user
        if (title != null && message != null) {
            String owner = a.getOwnerId();
            NotificationModel nm = new NotificationModel(
                    UUID.randomUUID().toString(),
                    title,
                    message,
                    System.currentTimeMillis(),
                    false,
                    owner
            );
            db.notificationDao().insert(nm);
        }
    }
}
