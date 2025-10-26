package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.R;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSignup;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.emailField);
        etPassword = findViewById(R.id.passwordField);
        btnSignup = findViewById(R.id.signupButton);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignup.setOnClickListener(v -> signupUser());
    }

    private void signupUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user in Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Optional: store user info in Firestore
                    db.collection("users").document(email).set(new User(email));

                    // Go to OTP page
                    Intent intent = new Intent(SignupActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Simple User model for Firestore
    private static class User {
        String email;
        User(String email) { this.email = email; }
    }
}
