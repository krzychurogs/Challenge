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

public class CustomerRegisterActivity extends AppCompatActivity implements FragmentHistory.FragmentHistoryListener {

    private EditText mEmail, mPassword,mrPassword,mName;

    private Button mRegistration,MLogout;
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FragmentHistory fragmenta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register2);
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
                    reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id);
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("level").exists())
                            {
                                Intent intent = new Intent(CustomerRegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(CustomerRegisterActivity.this, QuizActivity.class);
                    startActivity(intent);
                    finish();
                    return;

                }
            }
        };

        mEmail = (EditText) findViewById(R.id.et_email);
        mPassword = (EditText) findViewById(R.id.et_password);
        mName= (EditText) findViewById(R.id.et_name);
        mrPassword= (EditText) findViewById(R.id.et_repassword);
        mRegistration = (Button) findViewById(R.id.registration);
        mRegistration.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CustomerRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerRegisterActivity.this, "Rejestracja sie nie powiodla", Toast.LENGTH_SHORT).show();
                        }else{
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
                            current_user_db.setValue(mName.getText().toString());
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