package com.neeravtanay.neuratask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.neeravtanay.neuratask.R;

public class AIFillActivity extends AppCompatActivity {

    private EditText inputDescription;
    private Button btnGenerate, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_fill);

        inputDescription = findViewById(R.id.inputDescription);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnGenerate.setOnClickListener(v -> {
            String desc = inputDescription.getText().toString().trim();

            if (desc.isEmpty()) {
                Toast.makeText(this, "Please describe your assignment!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🧠 Simplified AI-style parsing (Title, Subject, Description only)
            String title = generateTitle(desc);
            String subject = generateSubject(desc);
            String description = "Auto-filled: " + desc;

            // Send only relevant data (no date/time/priority AI parsing)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("description", description);
            resultIntent.putExtra("subject", subject);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // --- Simplified AI logic ---
    private String generateTitle(String input) {
        input = input.toLowerCase();
        if (input.contains("math")) return "Math Assignment";
        if (input.contains("science")) return "Science Homework";
        if (input.contains("english")) return "English Essay";
        if (input.contains("history")) return "History Report";
        if (input.contains("project")) return "Project Work";
        return "General Assignment";
    }

    private String generateSubject(String input) {
        input = input.toLowerCase();
        if (input.contains("math")) return "Mathematics";
        if (input.contains("science")) return "Science";
        if (input.contains("english")) return "English";
        if (input.contains("history")) return "History";
        if (input.contains("computer")) return "Computer Science";
        return "General Studies";
    }
}
