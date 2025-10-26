package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.utils.EmailSender;

import java.util.HashMap;
import java.util.Map;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerify;
    private TextView tvResend;
    private String email;
    private FirebaseFirestore db;
    private CountDownTimer timer;
    private String currentOtp;

    // Sender Gmail credentials (only one account for app)
    private final String FROM_EMAIL = "natkat0709@gmail.com";       // your sender Gmail
    private final String FROM_PASSWORD = "xvku hvsx ndbe krkl";               // App Password for sender Gmail

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etOtp = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerifyOtp);
        tvResend = findViewById(R.id.tvResendOtp);
        db = FirebaseFirestore.getInstance();

        // Email comes from Login/Signup page
        email = getIntent().getStringExtra("email");

        sendOtp();        // send OTP immediately
        startOtpTimer();  // start 5-minute timer

        btnVerify.setOnClickListener(v -> verifyOtp());
        tvResend.setOnClickListener(v -> {
            sendOtp();
            startOtpTimer();
            tvResend.setVisibility(TextView.GONE);
        });
    }

    private void sendOtp() {
        // Generate 4-digit OTP
        currentOtp = String.valueOf((int)(Math.random() * 9000) + 1000);
        long expiry = System.currentTimeMillis() + 5 * 60 * 1000; // 5 min expiry

        // Save OTP & expiry in Firestore
        Map<String, Object> otpData = new HashMap<>();
        otpData.put("otp", currentOtp);
        otpData.put("expiry", expiry);

        db.collection("otp_verification").document(email)
                .set(otpData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "OTP sent to your email", Toast.LENGTH_SHORT).show();

                    // Send OTP email
                    String subject = "Your OTP for NeuraTask";
                    String body = "Your OTP is: " + currentOtp + "\nIt will expire in 5 minutes.";
                    EmailSender.sendEmail(email, subject, body, FROM_EMAIL, FROM_PASSWORD);
                });
    }

    private void startOtpTimer() {
        timer = new CountDownTimer(5 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                tvResend.setVisibility(TextView.VISIBLE);
            }
        }.start();
    }

    private void verifyOtp() {
        String enteredOtp = etOtp.getText().toString().trim();
        if (TextUtils.isEmpty(enteredOtp)) {
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("otp_verification").document(email)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String otp = doc.getString("otp");
                        Long expiryLong = doc.getLong("expiry");
                        long expiry = expiryLong != null ? expiryLong : 0;

                        if (System.currentTimeMillis() > expiry) {
                            Toast.makeText(this, "OTP expired. Please resend.", Toast.LENGTH_SHORT).show();
                        } else if (otp.equals(enteredOtp)) {
                            Toast.makeText(this, "OTP verified!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "OTP not found. Please resend.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
