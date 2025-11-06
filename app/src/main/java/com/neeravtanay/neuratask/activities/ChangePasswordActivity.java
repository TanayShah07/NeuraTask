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
    private ImageView ivTogglePassword, ivToggleCurrentPassword;
    private Button btnSave, btnCancel;

    private boolean isNewPasswordVisible = false;
    private boolean isCurrentPasswordVisible = false;
    private String lastSavedPassword = ""; // For Save state tracking

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize UI components
        etPassword = findViewById(R.id.etPassword);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleCurrentPassword = findViewById(R.id.ivToggleCurrentPassword);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Restore previous password text if needed
        etPassword.setText(lastSavedPassword);

        // Enable Save button only when new password changes
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(!s.toString().equals(lastSavedPassword) && s.length() >= 6);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 👁️ Toggle for NEW password
        ivTogglePassword.setOnClickListener(v -> {
            if (isNewPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_on);
            }
            isNewPasswordVisible = !isNewPasswordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });

        // 👁️ Toggle for CURRENT password
        ivToggleCurrentPassword.setOnClickListener(v -> {
            if (isCurrentPasswordVisible) {
                etCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleCurrentPassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleCurrentPassword.setImageResource(R.drawable.ic_eye_on);
            }
            isCurrentPasswordVisible = !isCurrentPasswordVisible;
            etCurrentPassword.setSelection(etCurrentPassword.getText().length());
        });

        // ❌ Cancel button: close activity
        btnCancel.setOnClickListener(v -> finish());

        // 💾 Save button: reauthenticate and update password
        btnSave.setOnClickListener(v -> {
            String currentPass = etCurrentPassword.getText().toString().trim();
            String newPass = etPassword.getText().toString().trim();

            if (newPass.equals(currentPass)) {
                Toast.makeText(this, "New password cannot be the same as the current one", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user != null && newPass.length() >= 6) {
                String email = user.getEmail();
                if (email != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, currentPass);
                    user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                    lastSavedPassword = newPass;
                                    etPassword.setText("");
                                    etCurrentPassword.setText("");
                                    btnSave.setEnabled(false);
                                } else {
                                    Toast.makeText(this, "Update failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
