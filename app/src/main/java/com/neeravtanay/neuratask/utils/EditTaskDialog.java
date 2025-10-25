package com.neeravtanay.neuratask.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.viewmodels.AssignmentViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditTaskDialog extends Dialog {

    private final AssignmentModel assignment;
    private final AssignmentViewModel viewModel;

    private TextInputEditText titleField, subjectField, dueField;
    private Button saveButton, cancelButton;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public EditTaskDialog(@NonNull Context context,
                          AssignmentModel assignment,
                          AssignmentViewModel viewModel) {
        super(context, R.style.RoundedDialog); // Custom rounded theme
        this.assignment = assignment;
        this.viewModel = viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_task);
        setCancelable(true);

        // Find views
        titleField = findViewById(R.id.editTitle);
        subjectField = findViewById(R.id.editSubject);
        dueField = findViewById(R.id.editDue);
        saveButton = findViewById(R.id.btnSave);
        cancelButton = findViewById(R.id.btnCancel);

        // Pre-fill existing data
        titleField.setText(assignment.getTitle());
        subjectField.setText(assignment.getSubject());

        // Convert timestamp → readable date
        if (assignment.getDueTimestamp() > 0) {
            String formatted = dateFormat.format(new Date(assignment.getDueTimestamp()));
            dueField.setText(formatted);
        }

        // Save button
        saveButton.setOnClickListener(v -> {
            String newTitle = titleField.getText() != null ? titleField.getText().toString().trim() : "";
            String newSubject = subjectField.getText() != null ? subjectField.getText().toString().trim() : "";
            String newDue = dueField.getText() != null ? dueField.getText().toString().trim() : "";

            if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newSubject) || TextUtils.isEmpty(newDue)) {
                Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            long newDueTimestamp;
            try {
                Date parsedDate = dateFormat.parse(newDue);
                if (parsedDate == null) throw new ParseException("Invalid date", 0);
                newDueTimestamp = parsedDate.getTime();
            } catch (ParseException e) {
                Toast.makeText(getContext(),
                        "Invalid date format. Use dd/MM/yyyy HH:mm",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Update model
            assignment.setTitle(newTitle);
            assignment.setSubject(newSubject);
            assignment.setDueTimestamp(newDueTimestamp);

            // Push update to ViewModel (Room + Firestore)
            viewModel.update(assignment);

            Toast.makeText(getContext(), "Task updated successfully!", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }
}
