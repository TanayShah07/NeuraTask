package com.neeravtanay.neuratask.utils;

import com.neeravtanay.neuratask.models.AssignmentModel;

public class AIHelper {
    public static float computePriorityScore(AssignmentModel a) {
        long now = System.currentTimeMillis();
        float timeLeft = Math.max(0, a.dueTimestamp - now);
        float normTime = Math.min(1f, timeLeft / (1000f * 60 * 60 * 24 * 7));
        float urgency = 1 - normTime;
        float manual = a.priorityManual / 5f;
        float est = Math.min(1f, a.estimatedMinutes / 240f);
        float w1 = 0.5f;
        float w2 = 0.3f;
        float w3 = 0.2f;
        return w1 * urgency + w2 * manual + w3 * est;
    }
}
