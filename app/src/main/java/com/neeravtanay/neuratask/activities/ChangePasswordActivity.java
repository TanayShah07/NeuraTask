package com.neeravtanay.neuratask.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neeravtanay.neuratask.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etPassword;
    private ImageView ivTogglePassword;
    private Button btnSave, btnCancel;

    private boolean isPasswordVisible = false;
    private String lastSavedPassword = ""; // used for cancel/restore functionality

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Set initial password value (empty or previously saved)
        etPassword.setText(lastSavedPassword);

        // Enable Save button only when password is modified
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(!s.toString().equals(lastSavedPassword) && s.length() >= 6);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Toggle password visibility
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_on);
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });

        // Cancel button restores last saved password
        btnCancel.setOnClickListener(v -> etPassword.setText(lastSavedPassword));

        // Save button updates password
        btnSave.setOnClickListener(v -> {
            String newPassword = etPassword.getText().toString().trim();
            if (user != null && newPassword.length() >= 6) {
                user.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show();
                        lastSavedPassword = newPassword;
                        btnSave.setEnabled(false);
                    } else {
                        Toast.makeText(this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
