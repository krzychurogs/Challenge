package com.example.challenge;

import androidx.appcompat.app.AppCompatActivity;

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

    int count=0 ;
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
        updateQuestion(count);

            answer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answer1.getText().toString().equals(mAnswer)) {
                        points+=4;
                        updateQuestion(count+=1);
                        System.out.println(mAnswer);
                        System.out.println(count);


                    }
                    else if (answer1.getText().toString().equals(medAnswer)) {
                        points+=3;
                        updateQuestion(count+=1);
                    }
                    else if (answer1.getText().toString().equals(lowAnswer)) {
                        points+=2;
                        updateQuestion(count+=1);
                    }
                    else if (answer1.getText().toString().equals(verylowAnswer)) {
                        points+=1;
                        updateQuestion(count+=1);
                    }


                }
            });

            answer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answer2.getText().toString() == mAnswer) {
                        points+=4;
                        updateQuestion(count+=1);
                    }
                    if (answer2.getText().toString() == medAnswer) {
                        points+=3;
                        updateQuestion(count+=1);
                    }
                    if (answer2.getText().toString() == lowAnswer) {
                        points+=2;
                        updateQuestion(count+=1);
                    }
                    if (answer2.getText().toString() == verylowAnswer) {
                        points+=1;
                        updateQuestion(count+=1);
                    }
                }
            });
            answer3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answer3.getText().toString() == mAnswer) {
                        points+=4;
                        updateQuestion(count+=1);
                    }
                    if (answer3.getText().toString() == medAnswer) {
                        points+=3;
                        updateQuestion(count+=1);
                    }
                    if (answer3.getText().toString() == lowAnswer) {
                        points+=2;
                        updateQuestion(count+=1);
                    }
                    if (answer3.getText().toString() == verylowAnswer) {
                        points+=1;
                        updateQuestion(count+=1);
                    }
                }
            });
            answer4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answer4.getText().toString() == mAnswer) {
                        points+=4;
                        updateQuestion(count+=1);
                    }
                    if (answer4.getText().toString() == medAnswer) {
                        points+=3;
                        updateQuestion(count+=1);
                    }
                    if (answer4.getText().toString() == lowAnswer) {
                        points+=2;
                        updateQuestion(count+=1);
                    }
                    if (answer4.getText().toString() == verylowAnswer) {
                        points+=1;
                        updateQuestion(count+=1);
                    }
                }
            });

                System.out.println(points);
                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child
                        ("Customers").child("Historia").child(user_id).child("level");
                ref.setValue("wysoki lvl");

    }

    public void updateQuestion(int num)
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
    }
}
