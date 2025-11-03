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
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.utils.AIHelper;
import com.neeravtanay.neuratask.utils.NotificationWorker;
import com.neeravtanay.neuratask.viewmodels.AssignmentViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AddAssignmentActivity extends AppCompatActivity {

    EditText etTitle, etDescription, etSubject, etEstimated;
    Button btnPickDate, btnPickTime, btnAIHelp, btnSave;
    Spinner spinnerPriority;
    TextView tvPickedDateTime;
    long pickedDateTime = 0;
    AssignmentViewModel vm;
    final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault());
    private static final int AI_FILL_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        // 🔹 Initialize UI
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

        // 🔹 Priority spinner
        spinnerPriority.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new Integer[]{1, 2, 3, 4, 5}));

        // 🔹 ViewModel init
        vm = new ViewModelProvider(this).get(AssignmentViewModel.class);

        // 🔹 Pick Date
        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar cc = Calendar.getInstance();
                cc.set(year, month, dayOfMonth);
                pickedDateTime = cc.getTimeInMillis();
                tvPickedDateTime.setText("📅 " + sdf.format(pickedDateTime));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 🔹 Pick Time
        btnPickTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                Calendar cc = Calendar.getInstance();
                cc.setTimeInMillis(pickedDateTime == 0 ? System.currentTimeMillis() : pickedDateTime);
                cc.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cc.set(Calendar.MINUTE, minute);
                pickedDateTime = cc.getTimeInMillis();
                tvPickedDateTime.setText("⏰ " + sdf.format(pickedDateTime));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        // 🔹 AI Fill
        btnAIHelp.setOnClickListener(v -> {
            Intent i = new Intent(this, AIFillActivity.class);
            startActivityForResult(i, AI_FILL_REQUEST_CODE);
        });

        // 🔹 Save Assignment
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String subj = etSubject.getText().toString().trim();
            String estText = etEstimated.getText().toString().trim();

            if (title.isEmpty() || desc.isEmpty() || subj.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int est = estText.isEmpty() ? 60 : Integer.parseInt(estText);

            AssignmentModel a = new AssignmentModel();
            a.setId(UUID.randomUUID().toString());
            a.setTitle(title);
            a.setDescription(desc);
            a.setSubject(subj);
            a.setEstimatedMinutes(est);
            a.setPriorityManual((Integer) spinnerPriority.getSelectedItem());
            a.setDueTimestamp(pickedDateTime == 0 ? System.currentTimeMillis() : pickedDateTime);
            a.setCompleted(false);

            String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "anon";

            a.setOwnerId(uid);
            a.setPriorityScore(AIHelper.computePriorityScore(a));

            vm.insert(a);

            scheduleAssignmentNotification(a, uid);

            Toast.makeText(this, "✅ Assignment added!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // 🔹 Receive AI autofill
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AI_FILL_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String desc = data.getStringExtra("description");
            String subj = data.getStringExtra("subject");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String priority = data.getStringExtra("priority");

            etTitle.setText(title);
            etDescription.setText(desc);
            etSubject.setText(subj);
            tvPickedDateTime.setText("📅 " + date + " ⏰ " + time);

            try {
                spinnerPriority.setSelection(Integer.parseInt(priority) - 1);
            } catch (Exception ignored) {}

            try {
                pickedDateTime = sdf.parse(date + " " + time).getTime();
            } catch (Exception e) {
                pickedDateTime = System.currentTimeMillis();
            }
        }
    }

    // 🔹 Notification Scheduling with user isolation + near-time safety
    public void scheduleAssignmentNotification(AssignmentModel assignment, String uid) {
        long now = System.currentTimeMillis();
        long due = assignment.getDueTimestamp();
        long timeUntilDue = Math.max(0, due - now);

        long oneHour = TimeUnit.HOURS.toMillis(1);
        long sixHours = TimeUnit.HOURS.toMillis(6);
        long oneDay = TimeUnit.DAYS.toMillis(1);

        java.util.function.Consumer<Long> scheduleIfPositive = (delayMs) -> {
            if (delayMs <= 0) return;
            Data data = new Data.Builder()
                    .putString("assignmentId", assignment.getId())
                    .putString("userId", uid)
                    .putString("title", assignment.getTitle())
                    .putLong("dueTime", due)
                    .build();

            OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag(uid + "_" + assignment.getId()) // unique per user
                    .build();

            WorkManager.getInstance(this).enqueueUniqueWork(
                    uid + "_" + assignment.getId() + "_" + delayMs,
                    ExistingWorkPolicy.REPLACE,
                    work
            );
        };

        // ⏱ For testing or very near deadlines (<= 1h)
        if (timeUntilDue <= oneHour) {
            scheduleIfPositive.accept(timeUntilDue);
            return;
        }

        // 24h, 6h, 1h before and exact time
        if (timeUntilDue > oneDay) scheduleIfPositive.accept(timeUntilDue - oneDay);
        if (timeUntilDue > sixHours) scheduleIfPositive.accept(timeUntilDue - sixHours);
        if (timeUntilDue > oneHour) scheduleIfPositive.accept(timeUntilDue - oneHour);
        scheduleIfPositive.accept(timeUntilDue);
    }
}
