package com.neeravtanay.neuratask.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingWorkPolicy;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.models.NotificationModel;
import com.neeravtanay.neuratask.utils.AIHelper;
import com.neeravtanay.neuratask.utils.NotificationWorker;
import com.neeravtanay.neuratask.viewmodels.AssignmentViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

public class AddAssignmentActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etSubject, etEstimated;
    private Button btnPickDate, btnPickTime, btnAIHelp, btnSave;
    private Spinner spinnerPriority;
    private TextView tvPickedDateTime;
    private long pickedDateTime = 0L;
    private AssignmentViewModel vm;
    private static final int AI_FILL_REQUEST_CODE = 101;
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault());
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        firestore = FirebaseFirestore.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etSubject = findViewById(R.id.etSubject);
        etEstimated = findViewById(R.id.etEstimated);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnAIHelp = findViewById(R.id.btnAIHelp);
        btnSave = findViewById(R.id.btnSave);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        tvPickedDateTime = findViewById(R.id.tvPickedDateTime);

        spinnerPriority.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new Integer[]{1, 2, 3, 4, 5}));

        vm = new ViewModelProvider(this).get(AssignmentViewModel.class);

        btnPickDate.setOnClickListener(v -> pickDate());
        btnPickTime.setOnClickListener(v -> pickTime());
        btnAIHelp.setOnClickListener(v -> startActivityForResult(new Intent(this, AIFillActivity.class), AI_FILL_REQUEST_CODE));
        btnSave.setOnClickListener(v -> saveAssignment());
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar cc = Calendar.getInstance();
            cc.set(y, m, d);
            pickedDateTime = cc.getTimeInMillis();
            tvPickedDateTime.setText("📅 " + sdf.format(pickedDateTime));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pickTime() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, h, m) -> {
            Calendar cc = Calendar.getInstance();
            cc.setTimeInMillis(pickedDateTime == 0 ? System.currentTimeMillis() : pickedDateTime);
            cc.set(Calendar.HOUR_OF_DAY, h);
            cc.set(Calendar.MINUTE, m);
            pickedDateTime = cc.getTimeInMillis();
            tvPickedDateTime.setText("⏰ " + sdf.format(pickedDateTime));
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void saveAssignment() {
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String subj = etSubject.getText().toString().trim();
        String estText = etEstimated.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || subj.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int est = estText.isEmpty() ? 60 : Integer.parseInt(estText);
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anon";

        AssignmentModel a = new AssignmentModel();
        a.setId(UUID.randomUUID().toString());
        a.setTitle(title);
        a.setDescription(desc);
        a.setSubject(subj);
        a.setEstimatedMinutes(est);
        a.setPriorityManual((Integer) spinnerPriority.getSelectedItem());
        a.setDueTimestamp(pickedDateTime == 0 ? System.currentTimeMillis() : pickedDateTime);
        a.setCompleted(false);
        a.setOwnerId(uid);
        a.setPriorityScore(AIHelper.computePriorityScore(a));

        // 1️⃣ Insert into local Room DB
        vm.insert(a);

        // 2️⃣ Save to Firestore (assignment data)
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", a.getTitle());
        doc.put("description", a.getDescription());
        doc.put("subject", a.getSubject());
        doc.put("ownerId", a.getOwnerId());
        doc.put("dueTimestamp", a.getDueTimestamp());
        doc.put("createdAt", System.currentTimeMillis());
        doc.put("status", "pending");
        doc.put("priorityManual", a.getPriorityManual());
        doc.put("priorityScore", a.getPriorityScore());

        firestore.collection("assignments").document(a.getId())
                .set(doc)
                .addOnSuccessListener(unused -> {
                    // ✅ Schedule local reminders
                    scheduleAssignmentNotification(a, uid);

                    // ✅ Create a notification entry in Firestore
                    Map<String, Object> notif = new HashMap<>();
                    notif.put("userId", FirebaseAuth.getInstance().getUid());
                    notif.put("title", "New Assignment Created!");
                    notif.put("message", "Assignment \"" + a.getTitle() + "\" has been added successfully.");
                    notif.put("timestamp", System.currentTimeMillis());

                    FirebaseFirestore.getInstance()
                            .collection("notifications")
                            .add(notif)
                            .addOnSuccessListener(ref -> {
                                // ✅ Immediately insert into Room for instant UI update
                                NotificationModel localNotif = new NotificationModel(
                                        ref.getId(),
                                        "New Assignment Created!",
                                        "Assignment \"" + a.getTitle() + "\" has been added successfully.",
                                        System.currentTimeMillis(),
                                        false,
                                        uid
                                );

                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    try {
                                        AppDatabase.getInstance(this).notificationDao().insert(localNotif);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            });
                })
                .addOnFailureListener(e -> {
                    // still schedule local reminders even if Firestore fails
                    scheduleAssignmentNotification(a, uid);
                });

        Toast.makeText(this, "✅ Assignment Added!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AI_FILL_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            etTitle.setText(data.getStringExtra("title"));
            etDescription.setText(data.getStringExtra("description"));
            etSubject.setText(data.getStringExtra("subject"));
        }
    }

    // 🔔 Schedule multiple reminders (24h/6h/1h/exact)
    private void scheduleAssignmentNotification(AssignmentModel a, String uid) {
        long now = System.currentTimeMillis();
        long due = a.getDueTimestamp();
        long timeUntilDue = Math.max(0, due - now);

        long oneHour = TimeUnit.HOURS.toMillis(1);
        long sixHours = TimeUnit.HOURS.toMillis(6);
        long oneDay = TimeUnit.DAYS.toMillis(1);

        java.util.function.Consumer<Long> schedule = delayMs -> {
            if (delayMs <= 0) return;

            Data data = new Data.Builder()
                    .putString("assignmentId", a.getId())
                    .putString("userId", uid)
                    .putString("title", a.getTitle())
                    .putLong("dueTime", due)
                    .build();

            OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag(uid + "_" + a.getId())
                    .build();

            WorkManager.getInstance(this).enqueueUniqueWork(
                    uid + "_" + a.getId() + "_" + delayMs,
                    ExistingWorkPolicy.REPLACE,
                    work
            );
        };

        if (timeUntilDue <= oneHour) {
            schedule.accept(timeUntilDue);
        } else {
            if (timeUntilDue > oneDay) schedule.accept(timeUntilDue - oneDay);
            if (timeUntilDue > sixHours) schedule.accept(timeUntilDue - sixHours);
            if (timeUntilDue > oneHour) schedule.accept(timeUntilDue - oneHour);
            schedule.accept(timeUntilDue);
        }
    }
}