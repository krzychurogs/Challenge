package com.example.challenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerLoginActivity extends AppCompatActivity implements FragmentHistory.FragmentHistoryListener {

    private EditText mEmail, mPassword;

    private Button mLogin, mRegistration,MLogout;
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FragmentHistory fragmenta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference mRef=database.getReference("sim");

        mAuth = FirebaseAuth.getInstance();
        fragmenta= new FragmentHistory();


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    String user_id = mAuth.getCurrentUser().getUid();
                    reff= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("lvl").exists())
                            {
                                Intent intent = new Intent(CustomerLoginActivity.this, MainActivity .class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(CustomerLoginActivity.this, QuizActivity.class);
                    startActivity(intent);
                    finish();
                    return;

                }
            }
        };

        mEmail = (EditText) findViewById(R.id.et_email);
        mPassword = (EditText) findViewById(R.id.et_password);

        mLogin = (Button) findViewById(R.id.btn_login);
        mRegistration = (Button) findViewById(R.id.btn_register);
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerLoginActivity.this, CustomerRegisterActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerLoginActivity.this, "Logowanie sie nie powiodło", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onInputSent(CharSequence input) {

    }
}