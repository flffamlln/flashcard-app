package com.yuri.yurisflashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCardActivity.this, MainActivity.class);
                AddCardActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = ((EditText) findViewById(R.id.enterquestion)).getText().toString();
                String answer = ((EditText) findViewById(R.id.enteranswer)).getText().toString();
                String option1 = ((EditText) findViewById(R.id.option_one)).getText().toString();
                String option2 =  ((EditText) findViewById(R.id.option_two)).getText().toString();
                if(question.length() == 0 || answer.length() == 0){
                    Toast.makeText(getApplicationContext(), "Must Enter Question and Answer", Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent();
                    data.putExtra("question", question);
                    data.putExtra("answer", answer);
                    data.putExtra("option1", option1);
                    data.putExtra("option2", option2);
                    //data.putExtra("option3", option3);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
    }
}