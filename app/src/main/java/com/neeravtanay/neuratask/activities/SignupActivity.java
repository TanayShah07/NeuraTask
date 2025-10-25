package com.neeravtanay.neuratask.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.R;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText nameField, ageField, emailField, passwordField;
    private Button signupButton;
    private TextView loginRedirect;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameField = findViewById(R.id.nameField);
        ageField = findViewById(R.id.ageField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signupButton = findViewById(R.id.signupButton);
        loginRedirect = findViewById(R.id.loginRedirect);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);

        signupButton.setOnClickListener(v -> registerUser());

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = nameField.getText().toString().trim();
        String age = ageField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(email) || password.length() < 6) {
            Toast.makeText(this, "Please fill all fields. Password must be >=6 chars", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdates);

                    String uid = user.getUid();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("age", age);
                    userData.put("email", email);

                    db.collection("users").document(uid)
                            .set(userData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                }
            } else {
                Exception e = task.getException();
                if (e instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Signup failed: " + (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
