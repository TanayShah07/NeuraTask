package com.neeravtanay.neuratask.utils;

import android.content.Context;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.neeravtanay.neuratask.workers.AssignmentReminderWorker;
import java.util.concurrent.TimeUnit;

public class WorkerScheduler {

    public static void scheduleAssignmentReminder(Context context) {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                AssignmentReminderWorker.class,
                6, TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(context).enqueue(request);
    }
}