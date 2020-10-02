package com.example.challenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FragmentMap extends Fragment {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location mFirstLocation;
    LocationRequest mLocationRequest;
    private Polyline gpsTrack;
    private FirebaseAuth mAuth;
    private LatLng lastKnownLatLng,endKnownLatLng,firstKnownLatLng;
    Button MStart,MEnd;
    Button mStopTime,mResetTime;
    EditText MDistance,MSpeed;
    TextView MCzas;
    int secs;
    DrawerLayout drawerLayout;


    Handler customHandler=new Handler();
    Runnable updateTimerThread=new Runnable() {
        @Override
        public void run() {
            timeinMiliseconds=SystemClock.uptimeMillis()-startTime;
            updateTime=timeinMiliseconds+timeSwapBuff;
            secs=(int)(updateTime/1000);
            int mins=secs/60;
            secs%=60;
            int milliseconds=(int)(updateTime%1000);
            MCzas.setText(""+mins+":"
                    +String.format("%2d",secs)+":"
                    +String.format("%3d",milliseconds));
            customHandler.postDelayed(this,0);
        }
    };
    long startTime=0L,timeinMiliseconds=0L,timeSwapBuff=0L,updateTime=0L;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return  inflater.inflate(R.layout.fragment_map,container,false);

    }
    @Override
    public void onStart() {
        super.onStart();






    }









    private void updateTrack() {
        List<LatLng> points = gpsTrack.getPoints();
        final Location first=new Location("");
        final Location second=new Location("");
        points.add(lastKnownLatLng);
        float[] distance = new float[3];
        float suma=0;

        Location.distanceBetween( first.getLatitude(), first.getLongitude(),
                second.getLatitude(), second.getLongitude(), distance);

        for(int i=0;i<points.size()-1;i++){


            first.setLatitude(points.get(i).latitude);
            first.setLongitude(points.get(i).longitude);
            second.setLatitude(points.get(i+1).latitude);
            second.setLongitude(points.get(i+1).longitude);
            Location.distanceBetween( first.getLatitude(), first.getLongitude(),
                    second.getLatitude(), second.getLongitude(), distance);

            suma+=distance[0];
            System.out.println(i+"dystans to"+distance[0]);
            System.out.println(i+"suma to"+suma);

            MDistance.setText("Dystans to:"+ suma);
            double km= 3.6;
            final double avgspeed= (suma/secs)* km ;
            DecimalFormat df = new DecimalFormat("#.##");
            MSpeed.setText("Predkosc to:"+df.format(avgspeed)+"km/h");
            final double finalSuma = suma;
            MEnd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    String user_id = mAuth.getCurrentUser().getUid();
                    DecimalFormat df = new DecimalFormat("#.##");

                    String szybkosc= df.format(avgspeed).toString();
                    Toast.makeText(getActivity().getApplicationContext(),"Dobry trening",Toast.LENGTH_SHORT).show();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child
                            ("Customers").child("Historia").child(user_id).child("predkosc");
                    current_user_db.setValue(szybkosc);
                    DatabaseReference distancer = FirebaseDatabase.getInstance().getReference().child("Users").child
                            ("Customers").child("Historia").child(user_id).child("dystans");
                    distancer.setValue(finalSuma);


                }
            });
        }


        gpsTrack.setPoints(points);


    }

}