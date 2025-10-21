package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.activities.fragments.PendingFragment;
import com.neeravtanay.neuratask.activities.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    ImageButton btnNotifications;
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_home);
        btnNotifications = findViewById(R.id.btnNotifications);
        bottomNav = findViewById(R.id.bottomNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragmentContainer, new PendingFragment()).commit();
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.homeFragmentContainer, new PendingFragment()).commit();
                    return true;
                case R.id.nav_add:
                    startActivity(new Intent(this, AddAssignmentActivity.class));
                    return true;
                case R.id.nav_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.homeFragmentContainer, new ProfileFragment()).commit();
                    return true;
            }
            return false;
        });
        btnNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        ImageView logo = findViewById(R.id.appLogo);
        android.view.animation.Animation anim = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(anim);
    }
}
