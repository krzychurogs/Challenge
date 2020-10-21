package com.example.challenge;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,FragmentAllTraining.FragmentAllTrainingListener,FragmentRoad.FragmentRoadListener, FragmentQuest.FragmentQuestListener,FragmentHistory.FragmentHistoryListener,FragmentChoiceRoad.FragmentChoiceRoadListener,FragmentSimplyTraining.FragmentSimplyTrainingListener {
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
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