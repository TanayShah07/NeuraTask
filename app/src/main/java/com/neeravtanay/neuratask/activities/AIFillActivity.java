package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.neeravtanay.neuratask.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AIFillActivity extends AppCompatActivity {

    private EditText inputDescription;
    private Button btnGenerate, btnBack;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_fill);

        inputDescription = findViewById(R.id.inputDescription);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnBack = findViewById(R.id.btnBack);
        calendar = Calendar.getInstance();

        btnBack.setOnClickListener(v -> finish());

        btnGenerate.setOnClickListener(v -> {
            String desc = inputDescription.getText().toString().trim();

            if (desc.isEmpty()) {
                Toast.makeText(this, "Please describe your assignment!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🧠 Simulated AI parsing (you can replace this with OpenAI API later)
            String title = generateTitle(desc);
            String subject = generateSubject(desc);
            String description = "Auto-filled: " + desc;
            String priority = generatePriority(desc);

            // Generate random or current date/time
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());

            // Send back to AddAssignmentActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("description", description);
            resultIntent.putExtra("subject", subject);
            resultIntent.putExtra("date", date);
            resultIntent.putExtra("time", time);
            resultIntent.putExtra("priority", priority);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // Dummy logic for now (you can replace later with AI)
    private String generateTitle(String input) {
        if (input.toLowerCase().contains("math")) return "Math Assignment";
        if (input.toLowerCase().contains("science")) return "Science Homework";
        if (input.toLowerCase().contains("english")) return "English Essay";
        return "General Assignment";
    }

    private String generateSubject(String input) {
        if (input.toLowerCase().contains("math")) return "Mathematics";
        if (input.toLowerCase().contains("science")) return "Science";
        if (input.toLowerCase().contains("english")) return "English";
        return "General Studies";
    }

    private String generatePriority(String input) {
        if (input.toLowerCase().contains("urgent")) return "5";
        if (input.toLowerCase().contains("important")) return "4";
        if (input.toLowerCase().contains("tomorrow")) return "3";
        return "2";
    }
}
