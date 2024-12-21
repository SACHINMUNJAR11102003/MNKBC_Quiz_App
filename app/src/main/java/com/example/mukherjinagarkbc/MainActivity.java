package com.example.mukherjinagarkbc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CountDownTimer countDownTimer;
    RadioGroup optionsGroup;
    int time;
    private final long startTimeInMillis = 30 * 1000; // 30 seconds in milliseconds
    private long timeLeftInMillis = startTimeInMillis;
    Button skip_button, next_button;
    int skipped = 0;
    int score = 0; // Variable to store the score

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    TextView question_textview, timer_textview, skipped_textview;
    RadioButton option1_textview, option2_textview, option3_textview, option4_textview;
    ArrayList<QuestionModel> questionModelArrayList;
    int currentQuestionIndex = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        skip_button = findViewById(R.id.skip_question_button);
        next_button = findViewById(R.id.next_question_button);
        question_textview = findViewById(R.id.question_textveiw);
        option1_textview = findViewById(R.id.option1);
        option2_textview = findViewById(R.id.option2);
        option3_textview = findViewById(R.id.option3);
        option4_textview = findViewById(R.id.option4);
        skipped_textview = findViewById(R.id.skipped_textview);
        timer_textview = findViewById(R.id.timer_textview);
        optionsGroup = findViewById(R.id.options_group);

        questionModelArrayList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "http://10.0.2.2:8080/mnkbc/questions"; // Added protocol

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String question = jsonObject.getString("question");
                        String option_1 = jsonObject.getString("option1");
                        String option_2 = jsonObject.getString("option2");
                        String option_3 = jsonObject.getString("option3");
                        String option_4 = jsonObject.getString("option4");
                        int right_ans = jsonObject.getInt("right_ans");
                        int ques_time = jsonObject.getInt("time");

                        questionModelArrayList.add(new QuestionModel(question, option_1, option_2, option_3, option_4, right_ans, ques_time));
                    }
                    if (!questionModelArrayList.isEmpty()) {
                        displayQuestion(currentQuestionIndex); // Display the first question
                    } else {
                        Toast.makeText(MainActivity.this, "No Questions available", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);

        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipped++;
                setSkipped(skipped);
                currentQuestionIndex++;
                displayQuestion(currentQuestionIndex);
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(); // Check the selected answer
                currentQuestionIndex++;
                if (currentQuestionIndex < questionModelArrayList.size()) {
                    displayQuestion(currentQuestionIndex);
                } else {
                    finishQuiz(); // Finish the quiz if it's the last question
                }
            }
        });
    }

    private void startTimer(long startTimeInMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(startTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountdownText();
                checkAnswer();
                if (currentQuestionIndex + 1 < questionModelArrayList.size()) {
                    currentQuestionIndex++;
                    displayQuestion(currentQuestionIndex);
                } else {
                    finishQuiz();
                }
            }
        }.start();
    }

    private void displayQuestion(int index) {
        if (index < questionModelArrayList.size()) {
            QuestionModel currentQuestion = questionModelArrayList.get(index);

            optionsGroup.clearCheck();
            String question = currentQuestion.getQuestion();
            String option1 = currentQuestion.getOption1();
            String option2 = currentQuestion.getOption2();
            String option3 = currentQuestion.getOption3();
            String option4 = currentQuestion.getOption4();
            int get_time = currentQuestion.getTime();

            setTime(get_time);
            skipped_textview.setText(String.valueOf(getSkipped()));

            question_textview.setText(question);
            option1_textview.setText(option1);
            option2_textview.setText(option2);
            option3_textview.setText(option3);
            option4_textview.setText(option4);

            startTimer(get_time * 1000);
        }
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        QuestionModel currentQuestion = questionModelArrayList.get(currentQuestionIndex);

        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            int selectedOption = optionsGroup.indexOfChild(selectedRadioButton) + 1; // Get the selected option index (1-based)

            if (selectedOption == currentQuestion.getRight_ans()) {
                score++; // Increment the score for the correct answer
            }
        }
    }

    private void finishQuiz() {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("skipped", skipped);
        startActivity(intent);
        finish();
    }

    private void updateCountdownText() {
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d", seconds);
        timer_textview.setText(timeLeftFormatted);
    }
}
