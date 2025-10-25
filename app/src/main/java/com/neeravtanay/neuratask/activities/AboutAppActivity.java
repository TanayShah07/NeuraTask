package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.neeravtanay.neuratask.R;

public class AboutAppActivity extends AppCompatActivity {

    private Button btnBack, btnLoginFromAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        btnBack = findViewById(R.id.btnBack);
        btnLoginFromAbout = findViewById(R.id.btnLoginFromAbout);

        btnBack.setOnClickListener(v -> finish()); // Back to splash

        btnLoginFromAbout.setOnClickListener(v -> {
            startActivity(new Intent(AboutAppActivity.this, LoginActivity.class));
            finish();
        });
    }
}
