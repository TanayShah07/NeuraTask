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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView signupRedirect;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        signupRedirect = findViewById(R.id.signupRedirect);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setCancelable(false);

        loginButton.setOnClickListener(v -> loginUser());
        signupRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        db.collection("users").document(uid)
                                .get()
                                .addOnSuccessListener(doc -> {
                                    UserProfile profile;
                                    if (!doc.exists()) {
                                        profile = new UserProfile(email);
                                        db.collection("users").document(uid).set(profile);
                                    } else {
                                        profile = new UserProfile(doc);
                                    }

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("userProfile", profile);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to fetch profile", Toast.LENGTH_SHORT).show());
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Toast.makeText(this, "Too many attempts. Try again later.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Login failed: " + (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class UserProfile implements java.io.Serializable {
        public String email;
        public String name;
        public String age;
        public int avatarIndex;

        public UserProfile() {}

        public UserProfile(String email) {
            this.email = email;
            this.name = "";
            this.age = "";
            this.avatarIndex = -1;
        }

        public UserProfile(DocumentSnapshot doc) {
            this.email = doc.getString("email");
            this.name = doc.getString("name") != null ? doc.getString("name") : "";
            this.age = doc.getString("age") != null ? doc.getString("age") : "";
            Long avatar = doc.getLong("avatarIndex");
            this.avatarIndex = avatar != null ? avatar.intValue() : -1;
        }
    }
}
