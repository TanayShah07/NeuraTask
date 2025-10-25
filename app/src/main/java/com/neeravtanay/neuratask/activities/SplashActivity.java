package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.neeravtanay.neuratask.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 4000;
    private TextView tvAuthors;
    private Button btnGetStarted, btnAboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvAuthors = findViewById(R.id.tvAuthors);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnAboutApp = findViewById(R.id.btnAboutApp);

        new Handler().postDelayed(() -> {
            fadeOut(tvAuthors);
            fadeIn(btnGetStarted);
            fadeIn(btnAboutApp);
        }, SPLASH_DELAY);

        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        });

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
