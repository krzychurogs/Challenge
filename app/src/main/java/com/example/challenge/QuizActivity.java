package com.example.challenge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    Button answer1,answer2,answer3,answer4;
    TextView question;
    private Questions mQuestions=new Questions();
    private String mAnswer;
    private FirebaseAuth mAuth;
    private String medAnswer;
    private String lowAnswer;
    private String verylowAnswer;
    private int mScore;
    int points=0;
    public int count=0 ;
    private int num = 0;
    private int mQuestionaLengtht=mQuestions.mQuestions.length;
    Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        mAuth = FirebaseAuth.getInstance();
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        r=new Random();
        question = findViewById(R.id.question);

        updateQuestion();
            answer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answer1.getText().toString() == mAnswer) {
                        points+=4;
                        updateQuestion();
                        System.out.println(mAnswer);
                        System.out.println("licznik"+points);
                    }
                }
            });

            answer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(medAnswer);
                    if (answer2.getText().toString() == medAnswer) {
                        System.out.println(medAnswer);
                        updateQuestion();
                        points += 3;
                        System.out.println("licznik"+points);

                    }
                }
            });
            answer3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answer3.getText().toString() == lowAnswer) {
                        System.out.println(lowAnswer);
                        points+=2;
                        updateQuestion();
                        System.out.println("licznik"+points);
                    }

                }
            });
            answer4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answer4.getText().toString() == verylowAnswer) {
                        points+=1;

                        updateQuestion();
                        System.out.println("licznik"+points);
                    }

                }
            });
    }

    public void updateQuestion()
    {
        if(num<mQuestionaLengtht)
        {
            question.setText(mQuestions.getQuestion(num));
            answer1.setText(mQuestions.getChoice1(num));
            answer2.setText(mQuestions.getChoice2(num));
            answer3.setText(mQuestions.getChoice3(num));
            answer4.setText(mQuestions.getChoice4(num));
            mAnswer=mQuestions.getCorrectAnswer(num);
            medAnswer=mQuestions.getCorrectAnswersformedium(num);
            lowAnswer=mQuestions.getCorrectAnswersforlow(num);
            verylowAnswer=mQuestions.getCorrectAnswersforverylow(num);
            num++;
        }
        else {
            String lvl="";
            if(points>35)
            {
                lvl="High";
            } else if (points>20) {
                lvl="Medium";
            }
            else {
                lvl="Easy";
            }

            final String finallvl=lvl;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Wynik ankiety");
            builder.setMessage("Plan treningowy dobrany dla ciebie bedzie na poziomie "+lvl);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("lvl");
                    ref.setValue(finallvl);
                    Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            });
            builder.setCancelable(false);
            builder.show();
        }

    }
}
