package com.neeravtanay.neuratask.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.utils.AIHelper;
import com.neeravtanay.neuratask.viewmodels.AssignmentViewModel;

import java.util.Calendar;

public class AddAssignmentActivity extends AppCompatActivity {
    EditText etTitle, etDescription, etSubject, etEstimated;
    Button btnPickDate, btnPickTime, btnAIHelp, btnSave;
    Spinner spinnerPriority;
    long pickedDateTime = 0;
    AssignmentViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etSubject = findViewById(R.id.etSubject);
        etEstimated = findViewById(R.id.etEstimated);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnAIHelp = findViewById(R.id.btnAIHelp);
        btnSave = findViewById(R.id.btnSave);
        spinnerPriority = findViewById(R.id.spinnerPriority);

        spinnerPriority.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new Integer[]{1,2,3,4,5}));

        vm = new ViewModelProvider(this).get(AssignmentViewModel.class);

        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar cc = Calendar.getInstance();
                cc.set(year, month, dayOfMonth);
                pickedDateTime = cc.getTimeInMillis();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnPickTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                Calendar cc = Calendar.getInstance();
                cc.setTimeInMillis(pickedDateTime == 0 ? System.currentTimeMillis() : pickedDateTime);
                cc.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cc.set(Calendar.MINUTE, minute);
                pickedDateTime = cc.getTimeInMillis();
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        btnAIHelp.setOnClickListener(v -> {
            String desc = etDescription.getText().toString().trim();
            if (desc.isEmpty()) {
                etTitle.setText("New Task");
                etEstimated.setText("60");
                spinnerPriority.setSelection(2);
                return;
            }
            String genTitle = desc.length() > 20 ? desc.substring(0, 20) + "..." : desc;
            etTitle.setText(genTitle);
            etEstimated.setText("60");
            spinnerPriority.setSelection(3);
        });

        btnSave.setOnClickListener(v -> {
            AssignmentModel a = new AssignmentModel();
            a.setId(java.util.UUID.randomUUID().toString()); // use setter
            a.setTitle(etTitle.getText().toString().trim());
            a.setDescription(etDescription.getText().toString().trim());
            a.setSubject(etSubject.getText().toString().trim());
            a.setEstimatedMinutes(Integer.parseInt(etEstimated.getText().toString().isEmpty() ? "60" : etEstimated.getText().toString()));
            a.setPriorityManual((Integer) spinnerPriority.getSelectedItem());
            a.setDueTimestamp(pickedDateTime == 0 ? System.currentTimeMillis() : pickedDateTime);
            a.setCompleted(false);
            a.setOwnerId(FirebaseAuth.getInstance().getCurrentUser() == null ? "anon" : FirebaseAuth.getInstance().getCurrentUser().getUid());
            a.setPriorityScore(AIHelper.computePriorityScore(a));

            vm.insert(a);
            finish();
        });
    }
}
