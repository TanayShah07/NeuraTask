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

import com.google.firebase.auth.FirebaseAuth;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.utils.AIHelper;
import com.neeravtanay.neuratask.viewmodels.AssignmentViewModel;
import com.neeravtanay.neuratask.utils.NotificationWorker;

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

        // 🔹 Initialize UI elements
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

        // 🔹 Priority dropdown
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

        // 🔹 Open AI Fill Page
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
            a.setOwnerId(FirebaseAuth.getInstance().getCurrentUser() == null ? "anon" : FirebaseAuth.getInstance().getCurrentUser().getUid());
            a.setPriorityScore(AIHelper.computePriorityScore(a));

            // 🔹 Insert into DB
            vm.insert(a);

            // 🔹 Schedule notification
            scheduleAssignmentNotification(a);

            Toast.makeText(this, "✅ Assignment added!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // 🔹 Receive AI data and auto-fill
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

            // 🔹 Combine date/time into one display
            tvPickedDateTime.setText("📅 " + date + " ⏰ " + time);

            // 🔹 Set priority spinner (convert string to index)
            try {
                spinnerPriority.setSelection(Integer.parseInt(priority) - 1);
            } catch (Exception ignored) {
            }

            // 🔹 Convert date+time to timestamp
            try {
                pickedDateTime = sdf.parse(date + " " + time).getTime();
            } catch (Exception e) {
                pickedDateTime = System.currentTimeMillis();
            }
        }
    }

    // 🔹 Schedule notification using WorkManager
    public void scheduleAssignmentNotification(AssignmentModel assignment) {
        long delay = assignment.getDueTimestamp() - System.currentTimeMillis();
        if (delay < 0) delay = 0;

        Data data = new Data.Builder()
                .putString("assignmentId", assignment.getId())
                .build();

        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueue(work);
    }
}
