package com.example.challenge;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,FragmentAllTraining.FragmentAllTrainingListener,FragmentRoad.FragmentRoadListener, FragmentQuest.FragmentQuestListener,FragmentHistory.FragmentHistoryListener,FragmentChoiceRoad.FragmentChoiceRoadListener,FragmentSimplyTraining.FragmentSimplyTrainingListener {
    private DrawerLayout drawer;
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    List<String>nameUser=new ArrayList<String>();
    String name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        drawer = findViewById(R.id.drawer_layout);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nameofuser);
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> newlist = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    name= String.valueOf(ds.getValue());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);
        navUsername.setText("Krzysztof");


        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentSimplyTraining()).commit();
            navigationView.setCheckedItem(R.id.nav_simply_training);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_all_training:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentAllTraining()).commit();
                break;
            case R.id.nav_choice_road:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentChoiceRoad()).commit();
                break;
            case R.id.nav_simply_training:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentSimplyTraining()).commit();
                break;
            case R.id.nav_Quest:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentQuest()).commit();
                break;
            case R.id.nav_Logout:
                FirebaseAuth.getInstance().signOut();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onInputSent(CharSequence input) {

    }
}