package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.R;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etAge, etEmail, etPassword;
    private Button btnSignup;
    private TextView tvLoginRedirect;
    private ImageView ivTogglePassword;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etName = findViewById(R.id.nameField);
        etAge = findViewById(R.id.ageField);
        etEmail = findViewById(R.id.emailField);
        etPassword = findViewById(R.id.passwordField);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnSignup = findViewById(R.id.signupButton);
        tvLoginRedirect = findViewById(R.id.loginRedirect);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 👁️ Toggle password visibility
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Sign-up button action
        btnSignup.setOnClickListener(v -> signupUser());

        // Redirect to login page
        tvLoginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
        } else {
            // Show password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_on);
        }
        etPassword.setSelection(etPassword.getText().length()); // Keep cursor at end
        isPasswordVisible = !isPasswordVisible;
    }

    private void signupUser() {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate all fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Firebase Auth user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();

                    // Create user data
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("age", age);
                    userMap.put("email", email);
                    userMap.put("avatarIndex", 0);

                    // Store in Firestore
                    db.collection("users").document(uid)
                            .set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();

                                // Redirect to OTP Verification
                                Intent intent = new Intent(SignupActivity.this, OtpVerificationActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}