package com.neeravtanay.neuratask.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.neeravtanay.neuratask.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_splash);
        ImageView logo = findViewById(R.id.splashLogo);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.splash_logo_anim);
        logo.startAnimation(a);
        new Handler().postDelayed(() -> {
            if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1600);
    }
}
