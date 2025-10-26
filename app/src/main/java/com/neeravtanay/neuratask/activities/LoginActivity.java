package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;   // TextView for Sign Up redirect
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etEmail = findViewById(R.id.emailField);
        etPassword = findViewById(R.id.passwordField);
        btnLogin = findViewById(R.id.loginButton);
        tvSignUp = findViewById(R.id.signupRedirect); // Sign Up TextView

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Login button click
        btnLogin.setOnClickListener(v -> loginUser());

        // Sign Up redirect click
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in with Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Go to OTP verification page
                    Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
