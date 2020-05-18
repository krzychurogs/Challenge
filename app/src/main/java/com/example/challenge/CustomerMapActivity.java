package com.example.challenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location mFirstLocation;
    LocationRequest mLocationRequest;
    private Polyline gpsTrack;
    private LatLng lastKnownLatLng,endKnownLatLng,firstKnownLatLng;
    Button MLogout,MStart,MEnd;
    EditText MDistance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MLogout=(Button)findViewById(R.id.logout);
        MStart=(Button)findViewById(R.id.start);
        MEnd=(Button)findViewById(R.id.end);
        MDistance=(EditText)findViewById(R.id.distance);


        MLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(CustomerMapActivity.this,CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
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


        final Location first=new Location("");
        final Location second=new Location("");


        MStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                    for(int i=0;i<1;i++)
                    mFirstLocation=location;


                firstKnownLatLng=new LatLng(mFirstLocation.getLatitude(), mFirstLocation.getLongitude());

                first.setLatitude(firstKnownLatLng.latitude);
                first.setLongitude(firstKnownLatLng.longitude);
                System.out.println("poczatek"+ first.getLatitude());
                System.out.println("poczatek"+ first.getLongitude());




        MEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                endKnownLatLng=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                second.setLatitude(endKnownLatLng.latitude);
                second.setLongitude(endKnownLatLng.longitude);
                System.out.println("koniec"+ second.getLatitude());

                System.out.println("koniec"+ second.getLongitude());

                float[] distance = new float[3];

                Location.distanceBetween( first.getLatitude(), first.getLongitude(),
                        second.getLatitude(), second.getLongitude(), distance);



                MDistance.setText("Dystans to:"+ String.valueOf(distance[0]));
                String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference distance_history_db = FirebaseDatabase.getInstance().getReference().child("Users").child("History").child(userid);
                distance_history_db.setValue(distance[0]);
            }
        });


    }
});


        // Set listeners for click events.
        updateTrack();
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
        String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("userAvailable");
        GeoFire geoFire=new GeoFire(ref);
        geoFire.removeLocation(userid);
    }


    private void updateTrack() {
        List<LatLng> points = gpsTrack.getPoints();
        points.add(lastKnownLatLng);
        gpsTrack.setPoints(points);
    }
}
