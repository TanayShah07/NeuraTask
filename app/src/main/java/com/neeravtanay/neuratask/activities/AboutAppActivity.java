package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.neeravtanay.neuratask.R;

public class AboutAppActivity extends AppCompatActivity {

    private Button btnBack, btnLoginFromAbout;
    private TextView tvAboutTitle, tvAboutContent, tvDevelopers, tvTutorialTitle;
    private ImageView img_ss_homepage, img_ss_addassignment, img_ss_aifill, img_ss_changepass;
    private TextView tv_ss_homepage_caption, tv_ss_add_caption, tv_ss_aifill_caption, tv_ss_changepass_caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        // Initialize all views
        initViews();

        // Back Button → return to previous screen
        btnBack.setOnClickListener(v -> finish());

        // Login Button → navigate to LoginActivity
        btnLoginFromAbout.setOnClickListener(v -> {
            Intent intent = new Intent(AboutAppActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnLoginFromAbout = findViewById(R.id.btnLoginFromAbout);

        tvAboutTitle = findViewById(R.id.tvAboutTitle);
        tvAboutContent = findViewById(R.id.tvAboutContent);
        tvDevelopers = findViewById(R.id.tvDevelopers);
        tvTutorialTitle = findViewById(R.id.tvTutorialTitle);

        img_ss_homepage = findViewById(R.id.img_ss_homepage);
        img_ss_addassignment = findViewById(R.id.img_ss_addassignment);
        img_ss_aifill = findViewById(R.id.img_ss_aifill);
        img_ss_changepass = findViewById(R.id.img_ss_changepass);

        tv_ss_homepage_caption = findViewById(R.id.tv_ss_homepage_caption);
        tv_ss_add_caption = findViewById(R.id.tv_ss_add_caption);
        tv_ss_aifill_caption = findViewById(R.id.tv_ss_aifill_caption);
        tv_ss_changepass_caption = findViewById(R.id.tv_ss_changepass_caption);
    }
}
