package com.yuri.yurisflashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.plattysoft.leonids.ParticleSystem;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;
    boolean isShowingAnswers = true;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countDownTimer = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.timer)).setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
            }
        };

        flashcardDatabase = new FlashcardDatabase((getApplicationContext()));
        allFlashcards = flashcardDatabase.getAllCards();

        if(allFlashcards != null && allFlashcards.size() > 0){
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());

            ((TextView) findViewById(R.id.option_one)).setText(allFlashcards.get(0).getWrongAnswer1());
            ((TextView) findViewById(R.id.option_two)).setText(allFlashcards.get(0).getWrongAnswer2());
        }

        findViewById(R.id.flashcard_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.rocket).setVisibility(View.INVISIBLE);
                View answerSideView = findViewById(R.id.flashcard_answer);

                // get the center for the clipping circle
                int cx = answerSideView.getWidth() / 2;
                int cy = answerSideView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius);

                // hide the question and show the answer to prepare for playing the animation!
                findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);

                anim.setDuration(800);
                anim.start();
            }
        });

        findViewById(R.id.flashcard_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());
                allFlashcards = flashcardDatabase.getAllCards();
                if(allFlashcards.size() <= 0){
                    currentCardDisplayedIndex = 0;
                    findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
                    findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);

                    findViewById(R.id.option_one).setBackgroundColor(Color.parseColor("#e8e2d8"));
                    findViewById(R.id.option_two).setBackgroundColor(Color.parseColor("#e8e2d8"));

                    findViewById(R.id.option_one).setVisibility(View.INVISIBLE);
                    findViewById(R.id.option_two).setVisibility(View.INVISIBLE);
                    findViewById(R.id.rocket).setVisibility(View.VISIBLE);
                    findViewById(R.id.next).setVisibility(View.INVISIBLE);

                    findViewById(R.id.timer).setVisibility(View.INVISIBLE);
                } else{
                    Flashcard flashcard = allFlashcards.get(currentCardDisplayedIndex);
                    findViewById(R.id.rocket).setVisibility(View.INVISIBLE);

                    ((TextView) findViewById(R.id.flashcard_question)).setText(flashcard.getQuestion());
                    ((TextView) findViewById(R.id.flashcard_answer)).setText(flashcard.getAnswer());
                    ((TextView) findViewById(R.id.option_one)).setText(flashcard.getWrongAnswer1());
                    ((TextView) findViewById(R.id.option_two)).setText(flashcard.getWrongAnswer2());

                    countDownTimer.cancel();
                    countDownTimer.start();
                }
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // if no cards, return
                if(allFlashcards.size() == 0){
                    currentCardDisplayedIndex = 0;
                    return;
                }

                findViewById(R.id.rocket).setVisibility(View.INVISIBLE);

                // if only one card, return
                if(allFlashcards.size() == 1) {
                    return;
                }

                int newRandIndex = currentCardDisplayedIndex;
                while(newRandIndex == currentCardDisplayedIndex){
                    newRandIndex = getRandomNumber(0, allFlashcards.size()-1);
                }
                currentCardDisplayedIndex = newRandIndex;

                allFlashcards = flashcardDatabase.getAllCards();
                Flashcard flashcard = allFlashcards.get(currentCardDisplayedIndex);

                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);

                final Animation leftOutOption = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out);
                final Animation rightInOption = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);

                if(findViewById(R.id.flashcard_question).getVisibility() == View.INVISIBLE){
                    findViewById(R.id.flashcard_answer).startAnimation(leftOutAnim);
                } else{
                    findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);
                }

                leftOutOption.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // if card available, show data in TextViews
                        findViewById(R.id.option_one).setBackgroundColor(Color.parseColor("#e8e2d8"));
                        findViewById(R.id.option_two).setBackgroundColor(Color.parseColor("#e8e2d8"));

                        ((TextView) findViewById(R.id.option_one)).setText(flashcard.getWrongAnswer1());
                        ((TextView) findViewById(R.id.option_two)).setText(flashcard.getWrongAnswer2());

                        findViewById(R.id.option_one).startAnimation(rightInOption);
                        findViewById(R.id.option_two).startAnimation(rightInOption);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });


                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                        findViewById(R.id.option_one).startAnimation(leftOutOption);
                        findViewById(R.id.option_two).startAnimation(leftOutOption);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // if card available, show data in TextViews

                        ((TextView) findViewById(R.id.flashcard_question)).setText(flashcard.getQuestion());
                        ((TextView) findViewById(R.id.flashcard_answer)).setText(flashcard.getAnswer());

                        findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                        findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);

                        findViewById(R.id.flashcard_question).startAnimation(rightInAnim);

                        countDownTimer.cancel();
                        countDownTimer.start();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });
            }
        });

        findViewById(R.id.option_one).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                findViewById(R.id.option_one).setBackgroundColor(getResources().getColor(R.color.red, null));
                findViewById(R.id.option_two).setBackgroundColor(getResources().getColor(R.color.green, null));
            }
        });

        findViewById(R.id.option_two).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                findViewById(R.id.option_two).setBackgroundColor(getResources().getColor(R.color.green, null));
                new ParticleSystem(MainActivity.this, 100, R.drawable.confetti, 3000)
                        .setSpeedRange(0.2f, 0.5f)
                        .oneShot(findViewById(R.id.option_two), 100);
            }
        });

        findViewById(R.id.toggle_choices_visibility).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isShowingAnswers == true) {
                    ((ImageView) findViewById(R.id.toggle_choices_visibility)).setImageResource(R.drawable.openeye);
                    isShowingAnswers = false;
                    findViewById(R.id.option_one).setVisibility(View.INVISIBLE);
                    findViewById(R.id.option_two).setVisibility(View.INVISIBLE);
                    findViewById(R.id.option_one).setBackgroundColor(Color.parseColor("#e8e2d8"));
                    findViewById(R.id.option_two).setBackgroundColor(Color.parseColor("#e8e2d8"));
                } else{
                    ((ImageView) findViewById(R.id.toggle_choices_visibility)).setImageResource(R.drawable.closedeye);
                    isShowingAnswers = true;
                    allFlashcards = flashcardDatabase.getAllCards();
                    if(allFlashcards.size() > 0){
                        findViewById(R.id.option_one).setVisibility(View.VISIBLE);
                        findViewById(R.id.option_two).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public int getRandomNumber(int minNumber, int maxNumber){
        Random rand = new Random();
        return rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) { // this 100 needs to match the 100 we used when we called startActivityForResult!
            findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
            findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
            findViewById(R.id.timer).setVisibility(View.VISIBLE);

            findViewById(R.id.option_one).setBackgroundColor(Color.parseColor("#e8e2d8"));
            findViewById(R.id.option_two).setBackgroundColor(Color.parseColor("#e8e2d8"));

            findViewById(R.id.option_one).setVisibility(View.VISIBLE);
            findViewById(R.id.option_two).setVisibility(View.VISIBLE);
            findViewById(R.id.rocket).setVisibility(View.INVISIBLE);
            findViewById(R.id.next).setVisibility(View.VISIBLE);

            String question = data.getExtras().getString("question"); // 'string1' needs to match the key we used when we put the string in the Intent
            String answer = data.getExtras().getString("answer");
            String option1 = data.getExtras().getString("option1");
            String option2 = data.getExtras().getString("option2");

            ((TextView)findViewById(R.id.flashcard_question)).setText(question);
            ((TextView)findViewById(R.id.flashcard_answer)).setText(answer);
            ((TextView)findViewById(R.id.option_one)).setText(option1);
            ((TextView)findViewById(R.id.option_two)).setText(option2);



            Snackbar.make(findViewById(R.id.flashcard_question),
                    "Card successfully created",
                    Snackbar.LENGTH_SHORT)
                    .show();

            flashcardDatabase.insertCard(new Flashcard(question, answer, option1, option2));
            allFlashcards = flashcardDatabase.getAllCards();
        }
    }
}
