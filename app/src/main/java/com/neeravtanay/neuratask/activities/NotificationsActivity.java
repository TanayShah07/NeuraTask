package com.neeravtanay.neuratask.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.utils.NotificationHelper;

public class NotificationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_notifications);
        NotificationHelper.createChannel(this);
    }
}
