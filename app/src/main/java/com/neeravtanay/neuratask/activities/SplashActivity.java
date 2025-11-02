package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.neeravtanay.neuratask.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 4000;
    private TextView tvAuthors;
    private Button btnGetStarted, btnAboutApp;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        // 🔹 If user is already logged in, skip splash and go directly to Home
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
            return;
        }

        // 🔹 Else, show splash screen
        setContentView(R.layout.activity_splash);

        tvAuthors = findViewById(R.id.tvAuthors);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnAboutApp = findViewById(R.id.btnAboutApp);

        // Initially hide buttons
        btnGetStarted.setVisibility(View.INVISIBLE);
        btnAboutApp.setVisibility(View.INVISIBLE);

        // Fade animations for author text and buttons
        new Handler().postDelayed(() -> {
            fadeOut(tvAuthors);
            fadeIn(btnGetStarted);
            fadeIn(btnAboutApp);
        }, SPLASH_DELAY);

        // 🔹 “Get Started” → open Login page
        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        });

        // 🔹 “About App” → open AboutAppActivity
        btnAboutApp.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, AboutAppActivity.class));
        });
    }

    private void fadeIn(View view) {
        view.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(1000);
        view.startAnimation(animation);
    }

    private void fadeOut(View view) {
        AlphaAnimation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }
}
