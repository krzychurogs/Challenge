package com.example.challenge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragmentHistory extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener{
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location flocation;
    private FragmentHistoryListener listener;
    Button next,back;
    TextView textdistance,textspeed;
    private LatLng lastKnownLatLng;
    List<String>listofplace=new ArrayList<String>();
    List<String>distancelist=new ArrayList<String>();
    private Polyline gpsTrack;
    List<String>speedlist=new ArrayList<String>();
    List<String>listofeachtrainingwaypoint=new ArrayList<>();
     public static int counttrain;


    public interface FragmentHistoryListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();


        final View root = inflater.inflate(R.layout.fragment_history, container, false);

        ImageView imageView1 = (ImageView) root.findViewById(R.id.imageView4);
        textdistance= (TextView)  root.findViewById(R.id.textdist);
        textspeed= (TextView)  root.findViewById(R.id.textspeed);
        next=(Button)root.findViewById(R.id.nextTraining);
        back=(Button)root.findViewById(R.id.backTraining);
        final CustomerMapActivity count=(CustomerMapActivity)getActivity();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("historia");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String dystans = String.valueOf(ds.child("dystans").getValue(Double.class));
                    String predkosc = ds.child("predkosc").getValue(String.class);
                    distancelist.add(dystans);
                    speedlist.add(predkosc);

                    textdistance.setText("Dystans to: "+distancelist.get(0).toString() +" metrów");
                    textspeed.setText("Predkosc to: "+speedlist.get(0).toString());

                    String data=ds.child("waypointy").getValue().toString();

                    listofeachtrainingwaypoint.add(data);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(counttrain==distancelist.size()-1)
                {
                    int countdest=0;
                    counttrain=countdest;
                    textdistance.setText("Dystans to: "+distancelist.get(counttrain).toString() +" metrów");
                    textspeed.setText("Predkosc to: "+speedlist.get(counttrain).toString());
                    pointsfromtrain(counttrain);
                }
                else {
                    counttrain+=1;
                    textdistance.setText("Dystans to: "+distancelist.get(counttrain).toString() +" metrów");
                    textspeed.setText("Predkosc to: "+speedlist.get(counttrain).toString());
                    pointsfromtrain(counttrain);
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(counttrain>0) {
                    counttrain -= 1;
                    textdistance.setText("Dystans to: " + distancelist.get(counttrain).toString() + " metrów");
                    textspeed.setText("Predkosc to: " + speedlist.get(counttrain).toString());
                    pointsfromtrain(counttrain);
                }
                else {
                    int countdest=distancelist.size()-1;
                    counttrain=countdest;
                    textdistance.setText("Dystans to: "+distancelist.get(counttrain).toString() +" metrów");
                    textspeed.setText("Predkosc to: "+speedlist.get(counttrain).toString());
                    pointsfromtrain(counttrain);
                }


            }
        });


        return root;

    }
    public void pointsfromtrain(int count)
        {
            List<String>newlist=new ArrayList<String>();
            String word[]=listofeachtrainingwaypoint.get(count).split(" ");
            for(String w:word)
            {
                newlist.add(w);
            }


            for(int i=0;i<newlist.size();i++)
            {
            if(i%2==1)
            {
                String words[]=newlist.get(i).split(",");
                for(String w:words)
                {
                    String str1 = w.replace("(", "");
                    String strnew=str1.replace(")","");

                    listofplace.add(strnew);
                }
            }
            }

    }


    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.CYAN);
        polylineOptions.width(4);
        gpsTrack = mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        List<LatLng> coordList = gpsTrack.getPoints();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        for(int i=0;i<listofplace.size();i++)
        {
            Double latide=21.9927641;
            Double longtide=  50.0226329;

            if(i%2==0)//longtiude
            {
                longtide=Double.valueOf(listofplace.get(i));
            }
            if(i%2 == 1){

                String lat=listofplace.get(i).toString().replace("]","");
                latide=Double.valueOf(lat);
            }
            lastKnownLatLng= new LatLng(longtide, latide);
            coordList.add(lastKnownLatLng);
        }


        gpsTrack.setPoints(coordList);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentHistoryListener){
            listener= (FragmentHistoryListener) context;

        }
        else {
            throw new RuntimeException(context.toString()+" trzeba implementowac Listnera");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

}
