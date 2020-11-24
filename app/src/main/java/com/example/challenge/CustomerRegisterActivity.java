package com.example.challenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerRegisterActivity extends AppCompatActivity implements FragmentHistory.FragmentHistoryListener {

    private EditText mEmail, mPassword, mrPassword, mName;

    private Button mRegistration, MLogout;
    DatabaseReference reff, reffs, reffsrequest;
    private FirebaseAuth mAuth;
    List<String> listofname = new ArrayList<>();
    List<String> listofkey = new ArrayList<>();
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FragmentHistory fragmenta;
   private ImageView imageswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = database.getReference("sim");

        mAuth = FirebaseAuth.getInstance();
        fragmenta = new FragmentHistory();
        showNameAfterId();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String user_id = mAuth.getCurrentUser().getUid();
                    reff = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id);
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("level").exists()) {
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
        mName = (EditText) findViewById(R.id.et_name);
        mrPassword = (EditText) findViewById(R.id.et_repassword);
        imageswitch= (ImageView) findViewById(R.id.imageswitch);
        mRegistration = (Button) findViewById(R.id.registration);

        imageswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                showNameAfterId();
                System.out.println(checkName());
                if(checkName()==false)
                {
                    try {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CustomerRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(CustomerRegisterActivity.this, "Rejestracja sie nie powiodla,email jest już w bazie", Toast.LENGTH_SHORT).show();
                                } else {

                                    String user_id = mAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
                                    current_user_db.setValue(mName.getText().toString());
                                    DatabaseReference nr_dzienne = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrDziennego");
                                    nr_dzienne.setValue(0);
                                    DatabaseReference nr_dzienne_avg = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrDziennegoAvg");
                                    nr_dzienne_avg.setValue(0);
                                    DatabaseReference nr_tygodnie_avg = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowegoAvg");
                                    nr_tygodnie_avg.setValue(0);
                                    DatabaseReference nr_tygodnie_dist = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowegoDist");
                                    nr_tygodnie_dist.setValue(0);
                                    DatabaseReference nr_tygodnie_count = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowyCount");
                                    nr_tygodnie_count.setValue(0);
                                }
                            }
                        });
                    }
                    catch (Exception e)
                    {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomerRegisterActivity.this);
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Uzupelnij formularz");
                        alertDialog.show();
                    }


                }
                else {
                   System.out.println("false");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomerRegisterActivity.this);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Jest juz taki nick w bazie");
                    alertDialog.show();
                }


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

    public void showNameAfterId() {

        reff = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = reff.orderByChild("name").equalTo("Krzysztof");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = String.valueOf(ds.getKey());
                    String name = String.valueOf(ds.getValue());

                    listofkey.add(key);
                   // System.out.println(key);

                }
                for(int i=1;i<listofkey.size();i++)
                {
                    showRequest(listofkey.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);
    }

    public void showRequest(String key) {

        reffsrequest = FirebaseDatabase.getInstance().getReference().child("Users").child(key);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    System.out.println("test");
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = String.valueOf(ds.getKey());
                    String name = String.valueOf(ds.getValue());
                    listofname.add(name);

                //  System.out.println("n"+name);
                //   System.out.println("k"+key);
                }
             checkName();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reffsrequest.addListenerForSingleValueEvent(valueEventListener);

    }
    public boolean checkName()
    {

        if(listofname.contains(mName.getText().toString()))
        {
            return true;
        }
        return false;
    }




}