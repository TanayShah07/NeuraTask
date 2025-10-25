package com.neeravtanay.neuratask.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.activities.fragments.PendingFragment;
import com.neeravtanay.neuratask.activities.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private ImageButton btnNotifications;
    private ImageView appLogo;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Views
        bottomNavigation = findViewById(R.id.bottom_navigation);
        btnNotifications = findViewById(R.id.btnNotifications);
        appLogo = findViewById(R.id.appLogo);

        // Default Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new PendingFragment())
                .commit();

        // Bottom Navigation Listener using if-else
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new PendingFragment())
                        .commit();
                return true;

            } else if (id == R.id.nav_add) {
                startActivity(new Intent(this, AddAssignmentActivity.class));
                return true;

            } else if (id == R.id.nav_profile) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();
                return true;

            } else {
                return false;
            }
        });

        // Notifications button click
        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class))
        );

        // Logo animation
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        appLogo.startAnimation(anim);
    }
}
