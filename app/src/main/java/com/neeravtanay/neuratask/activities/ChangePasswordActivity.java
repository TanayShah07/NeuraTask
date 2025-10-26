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

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neeravtanay.neuratask.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etPassword, etCurrentPassword;
    private ImageView ivTogglePassword;
    private Button btnSave, btnCancel;

    private boolean isPasswordVisible = false;
    private String lastSavedPassword = ""; // used for Save functionality

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etPassword = findViewById(R.id.etPassword);
        etCurrentPassword = findViewById(R.id.etCurrentPassword); // current password field
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

        // Cancel button: close activity and go back to previous/home page
        btnCancel.setOnClickListener(v -> finish());

        // Save button updates password
        btnSave.setOnClickListener(v -> {
            String currentPass = etCurrentPassword.getText().toString().trim();
            String newPass = etPassword.getText().toString().trim();

            if (newPass.equals(currentPass)) {
                Toast.makeText(this, "New password cannot be the same as the existing password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user != null && newPass.length() >= 6) {
                String email = user.getEmail();
                if (email != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, currentPass);
                    user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            // Update password
                            user.updatePassword(newPass).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show();
                                    lastSavedPassword = newPass;
                                    etPassword.setText("");
                                    etCurrentPassword.setText("");
                                    btnSave.setEnabled(false);
                                } else {
                                    Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
