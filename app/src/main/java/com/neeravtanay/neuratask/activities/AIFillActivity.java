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
        btnGenerate.setOnClickListener(v -> generateAutoFill());
    }

    private void generateAutoFill() {
        String desc = inputDescription.getText().toString().trim();
        if (desc.isEmpty()) {
            Toast.makeText(this, "Please describe your assignment!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent result = new Intent();
        result.putExtra("title", generateTitle(desc));
        result.putExtra("description", "Auto-filled: " + desc);
        result.putExtra("subject", generateSubject(desc));
        setResult(RESULT_OK, result);
        finish();
    }

    private String generateTitle(String input) {
        input = input.toLowerCase();
        if (input.contains("math")) return "Math Assignment";
        if (input.contains("science")) return "Science Homework";
        if (input.contains("english")) return "English Essay";
        if (input.contains("history")) return "History Report";
        if (input.contains("physics")) return "Physics Assignment";
        if (input.contains("MAD")) return "Mobile Application Development";
        if (input.contains("project")) return "Project Work";
        return "General Assignment";
    }

    private String generateSubject(String input) {
        input = input.toLowerCase();
        if (input.contains("math")) return "Mathematics";
        if (input.contains("science")) return "Science";
        if (input.contains("english")) return "English";
        if (input.contains("history")) return "History";
        if (input.contains("physics")) return "Physics Assignment";
        if (input.contains("mad")) return "Mobile Application Development";
        if (input.contains("computer")) return "Computer Science";
        return "General Studies";
    }
}