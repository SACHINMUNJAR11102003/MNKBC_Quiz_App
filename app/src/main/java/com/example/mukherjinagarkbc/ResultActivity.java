package com.example.mukherjinagarkbc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView scoreTextView, skippedTextView;
    Button restartButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreTextView = findViewById(R.id.score_textview);
        skippedTextView = findViewById(R.id.skipped_textview);
        restartButton = findViewById(R.id.restart_button);

        // Get data from Intent
        Intent intent = getIntent();
        int score = intent.getIntExtra("score", 0);
        int skipped = intent.getIntExtra("skipped", 0);

        // Set the score and skipped values
        scoreTextView.setText("Your Score: " + score);
        skippedTextView.setText("Questions Skipped: " + skipped);

        // Restart quiz button functionality
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent restartIntent = new Intent(ResultActivity.this, MainActivity.class);
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(restartIntent);
                finish();
            }
        });
    }
}
