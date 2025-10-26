package com.neeravtanay.neuratask.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.activities.fragments.PendingFragment;
import com.neeravtanay.neuratask.activities.fragments.ProfileFragment;
import com.neeravtanay.neuratask.database.AppDatabase;
import com.neeravtanay.neuratask.utils.NotificationWorker;

import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private ImageButton btnNotifications;
    private TextView tvNotificationBadge;
    private ImageView appLogo;
    private AppDatabase db;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Views
        bottomNavigation = findViewById(R.id.bottom_navigation);
        btnNotifications = findViewById(R.id.btnNotifications);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        appLogo = findViewById(R.id.appLogo);

        // Initialize database
        db = AppDatabase.getInstance(this);

        // ✅ Schedule background notification worker (once every 24 hours)
        PeriodicWorkRequest dailyWork = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                24, TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "AssignmentNotifier",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
        );

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

        // 🔹 Observe unread notifications count and update badge
        db.notificationDao().getAll().observe(this, notifications -> {
            long unread = notifications.stream().filter(n -> !n.read).count();
            updateNotificationBadge(unread);
        });
    }

    private void updateNotificationBadge(long count) {
        if (count > 0) {
            tvNotificationBadge.setText(String.valueOf(count));
            tvNotificationBadge.setVisibility(View.VISIBLE);
        } else {
            tvNotificationBadge.setVisibility(View.GONE);
        }
    }
}
