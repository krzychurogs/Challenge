package com.example.challenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomerMapActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener,FragmentHistory.FragmentHistoryListener,FragmentAllTraining.FragmentAllTrainingListener,FragmentChoiceRoad.FragmentChoiceRoadListener,FragmentRoad.FragmentRoadListener,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location mFirstLocation;
    LocationRequest mLocationRequest;
    private Polyline gpsTrack;
    private FirebaseAuth mAuth;
    private LatLng lastKnownLatLng,endKnownLatLng,firstKnownLatLng;
    ImageButton MStart;
    ImageButton MEnd;
    ImageButton mStopTime;
    Button mResetTime;
    TextView MDistance,MSpeed;
    TextView MCzas;
    int secs;
    public float suma=0;
    float distancer=0;
    DrawerLayout drawerLayout;
    TextToSpeech textToSpeech;
    private Object [] table;
    private int nElems;
    long maxid=0;
    static boolean rusz=false;
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        mAuth = FirebaseAuth.getInstance();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        MStart=(ImageButton)findViewById(R.id.start);
        chronometer = findViewById(R.id.czas);
        MEnd=(ImageButton)findViewById(R.id.end);
        MDistance=(TextView)findViewById(R.id.distance);
        MSpeed=(TextView)findViewById(R.id.speed);
        drawerLayout=(DrawerLayout)findViewById((R.id.drawer));
        NavigationView navigationView=findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        //MStart.setVisibility(View.GONE);

        mStopTime=(ImageButton)findViewById(R.id.stoptime) ;
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        mStopTime.setVisibility(View.GONE);
        MEnd.setVisibility(View.GONE);


        mStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rusz=false;
                stopChronometer();
                mStopTime.setVisibility(View.GONE);
                MEnd.setVisibility(View.VISIBLE);
                MStart.setVisibility(View.VISIBLE);
                if(mStopTime.getVisibility() == View.VISIBLE) {
                    MStart.setVisibility(View.INVISIBLE);
                    mStopTime.setVisibility(View.GONE);
                    MEnd.setVisibility(View.VISIBLE);
                }
                else if(mStopTime.getVisibility() == View.GONE)
                {
                    mStopTime.setVisibility(View.GONE);
                    MStart.setVisibility(View.VISIBLE);
                    MEnd.setVisibility(View.VISIBLE);
                }

            }
        });
        MStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startChronometer();
                rusz=true;
                mStopTime.setVisibility(View.INVISIBLE);
                MStart.setVisibility(View.GONE);

                if(MStart.getVisibility() == View.VISIBLE) {
                    MStart.setVisibility(View.INVISIBLE);
                    mStopTime.setVisibility(View.VISIBLE);
                    MEnd.setVisibility(View.INVISIBLE);
                }
                else if(MStart.getVisibility() == View.GONE)
                {
                    mStopTime.setVisibility(View.VISIBLE);
                    MStart.setVisibility(View.INVISIBLE);
                    MEnd.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.CYAN);
        polylineOptions.width(4);
        gpsTrack = mMap.addPolyline(polylineOptions);

        // Store a data object with the polyline, used here to indicate an arbitrary type.

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(final Location location) {

       if(rusz==true)
       {
           mLastLocation = location;
           LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
           mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
           mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
           String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
           DatabaseReference ref= FirebaseDatabase.getInstance().getReference("userAvailable");
           GeoFire geoFire=new GeoFire(ref);

           geoFire.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()), new
                   GeoFire.CompletionListener() {
                       @Override
                       public void onComplete(String key, DatabaseError error) {
                           //Do some stuff if you want to
                       }
                   });
           lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());

           updateTrack();
           //System.out.println("dyst"+location.getSpeed());

       }

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    private void updateTrack() {
        final List<LatLng> points = gpsTrack.getPoints();

        final Location first=new Location("");
        float[] distance = new float[3];
        final Location second=new Location("");
        points.add(lastKnownLatLng);

        int h=points.size();
        if(h>2)
            {
                first.setLatitude(lastKnownLatLng.latitude);
                first.setLongitude(lastKnownLatLng.longitude);
                second.setLatitude(points.get(h-2).latitude);
                second.setLongitude(points.get(h-2).longitude);
                Location.distanceBetween( first.getLatitude(), first.getLongitude(),
                        second.getLatitude(), second.getLongitude(), distance);
                suma+=distance[0];

            }
            else if (h<2)
            {
                suma+=0;

            }

            //   System.out.println(i+"dystans to"+distance[0]);
            // System.out.println(i+"suma to"+suma);
            DecimalFormat dfsuma = new DecimalFormat("#.##");
            MDistance.setText(""+dfsuma.format(suma)+"m");
            double km= 3.6;

            int elapsedMillis = (int) (SystemClock.elapsedRealtime() - chronometer.getBase());
            secs= elapsedMillis/1000;
            final double avgspeed= (suma/secs)* km ;
            DecimalFormat df = new DecimalFormat("#.##");
            MSpeed.setText(""+df.format(avgspeed)+"km/h");

            textToSpeech=new TextToSpeech(CustomerMapActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status!= TextToSpeech.ERROR)
                    {
                        textToSpeech.setLanguage(new Locale("pl", "PL"));

                         DecimalFormat df = new DecimalFormat("0") ;
                        String text=String.valueOf(df.format(suma)+"metrów");
                        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);



                    }
                }
            });

            final double finalSuma = suma;
            MEnd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    String user_id = mAuth.getCurrentUser().getUid();
                    DecimalFormat df = new DecimalFormat("#.##");

                    String szybkosc= df.format(avgspeed).toString();
                    DatabaseReference historia = FirebaseDatabase.getInstance().getReference().child("Users").child
                            ("Customers").child("Historia").child(user_id).child("historia").push();
                    historia.setValue("test");


                    historia.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                maxid=(dataSnapshot.getChildrenCount());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Date nowDate = new Date();

                    historia.child("data").setValue(nowDate);
                    historia.child("predkosc").setValue(szybkosc);
                    historia.child("dystans").setValue(finalSuma);
                    Toast.makeText(getApplicationContext(),"Dobry trening",Toast.LENGTH_SHORT).show();
                    String [] table;  //Referencja do tablicy
                   int nElems=0;
                   table=new String[5];
                   for(int j=0;j<points.size();j++) {


                       if (nElems >= table.length) {
                           String[] locTable = new String[table.length * 2];
                           for (int i = 0; i < table.length; i++) locTable[i] = table[i];
                           table = locTable;
                       }

                       table[nElems] = String.valueOf(points.get(j));        // Wstawiamy element
                       nElems++;
                   }
                   List nameList = new ArrayList<String>(Arrays.asList(table));

                   historia.child("waypointy").setValue(nameList);
                }
            });


        gpsTrack.setPoints(points);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.wylogujdraw: {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nevhistory:
                {

                getSupportFragmentManager().beginTransaction().replace(R.id.drawer,new FragmentHistory()).commit();
                break;
            }
            case R.id.nevRoads:
            {

                getSupportFragmentManager().beginTransaction().replace(R.id.drawer,new FragmentRoad()).commit();
                break;
            }
            case R.id.nevAllTrainings:
            {

                getSupportFragmentManager().beginTransaction().replace(R.id.drawer,new FragmentAllTraining()).commit();
                break;
            }
            case R.id.nevChoiceRoad:
            {

                getSupportFragmentManager().beginTransaction().replace(R.id.drawer,new FragmentChoiceRoad()).commit();
                break;
            }

        }
        return true;
    }

    @Override
    protected void onPause() {
        if(textToSpeech!=null)
        {
            textToSpeech.stop();
            textToSpeech.shutdown();

        }

        super.onPause();
    }

    @Override
    public void onInputSent(CharSequence input) {

    }
    public void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }
    public void stopChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
}