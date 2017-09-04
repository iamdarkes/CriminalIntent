package com.example.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    //added for Challenge 1 to save state when rotating screen
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_GRADE = "grade";
    private static final String KEY_SCORE = "score";
    private boolean mIsCheater;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;

    //Added for Challenge 1 to check if question is answered
    private int mAnswered = 0;
    //Added for Challenge 2 to keep track of score
    private int mGrade = 0;
    private float mScore = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            //added for Challenge 1 to save state when rotating screen
            mAnswered = savedInstanceState.getInt(KEY_ANSWERED, 0);
            //added for Challnege 2 to save grade when rotating screen
            mGrade = savedInstanceState.getInt(KEY_GRADE, 0);
            mScore = savedInstanceState.getFloat(KEY_SCORE, 0f);
        }

        //set what button is
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        updateQuestion();

        // sets what the button is
        mTrueButton = (Button) findViewById(R.id.true_button);
        //defines button's actions
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //must implement OnClickListener interface's sole method
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               checkAnswer(false);
           }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                //set to zero for new questions, part of challenge 1
                mAnswered = 0;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

                //Intent intent = new Intent(QuizActivity.this, CheatActivity.class);

                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if(data == null) {
                return;
            }
        }

        mIsCheater = CheatActivity.wasAnswerShown(data);
    }

   @Override
   // saves index when screen is rotated to start where left off
   public void onSaveInstanceState(Bundle savedInstanceState) {
       super.onSaveInstanceState(savedInstanceState);
       Log.i(TAG, "onSaveInstanceState");
       savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
       //added for Challenge 1 to save state when rotating screen
       savedInstanceState.putInt(KEY_ANSWERED, mAnswered);
       //added for challenge 2 to save grade when rotating screen
       savedInstanceState.putInt(KEY_GRADE, mGrade);

   }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        //Log.d(TAG, "Updating question text", new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if(mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (mAnswered == 0) {
                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.correct_toast;
                    mAnswered++;
                    mGrade++;
                } else {
                    messageResId = R.string.incorrect_toast;
                    mAnswered++;
                }
            } else {
                messageResId = R.string.answered_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
            displayGrade();
    }

    //Added for challenge 2 to display grade on final question
    private void displayGrade() {
        if(mCurrentIndex  == mQuestionBank.length - 1) {
            mScore = ((float) mGrade /  mQuestionBank.length) * 100;
            String format = String.format("%.02f", mScore);
            Toast.makeText(this, "Score: " + format + "%", Toast.LENGTH_SHORT).show();
            mScore = 0;
            mGrade = 0;
        }
    }
}
