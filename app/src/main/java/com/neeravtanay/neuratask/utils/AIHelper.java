package com.neeravtanay.neuratask.utils;

import com.neeravtanay.neuratask.models.AssignmentModel;

public class AIHelper {

    public static float computePriorityScore(AssignmentModel a) {
        long now = System.currentTimeMillis();

        // Use getters instead of direct field access
        float timeLeft = Math.max(0, a.getDueTimestamp() - now);
        int priorityManual = a.getPriorityManual();
        int estimated = a.getEstimatedMinutes();

        // Example formula for priority
        return priorityManual * 10 + (estimated > 0 ? 1000f / estimated : 0) + (timeLeft > 0 ? 100000f / timeLeft : 0);
    }
}
