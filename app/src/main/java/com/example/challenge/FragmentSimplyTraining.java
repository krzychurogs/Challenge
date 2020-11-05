package com.example.challenge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class FragmentSimplyTraining extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener{
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location flocation;
    private FragmentSimplyTrainingListener listener;
    Button next,back;
    TextView textdistance,textspeed;
    private LatLng lastKnownLatLng;
    List<String>listofplace=new ArrayList<String>();
    List<String>distancelist=new ArrayList<String>();
    private Polyline gpsTrack;
    List<String>speedlist=new ArrayList<String>();
    List<String>listofeachtrainingwaypoint=new ArrayList<>();
    public static int counttrain;
    List<LatLng> points ;
    int secs;
    public float suma=0;
    long maxid=0;
    private Chronometer chronometer;
    TextView MDistance,MSpeed;
    TextView MCalory;
    TextToSpeech textToSpeech;
    ImageButton MEnd;
    ImageButton mStopTime;
    ImageButton MStart;
    static boolean rusz=false;
    private boolean running;
    private long pauseOffset;
    List<Double>highstedaverage=new ArrayList<Double>();

    public interface FragmentSimplyTrainingListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();


        final View root = inflater.inflate(R.layout.fragment_fragment_simply_training, container, false);
        MStart=(ImageButton)root.findViewById(R.id.start);
        MDistance=(TextView)root.findViewById(R.id.distance);
        mStopTime=(ImageButton)root.findViewById(R.id.stoptime) ;
        MCalory=(TextView)root.findViewById(R.id.kalorie);
        MSpeed=(TextView)root.findViewById(R.id.speed);
        MEnd=(ImageButton)root.findViewById(R.id.end);
        chronometer = root.findViewById(R.id.czas);
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

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child("pierwsza trasa");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot snapshot : dataSnapshot.child("waypointy").getChildren()) {
                    String data = snapshot.getValue(String.class);
                    listofeachtrainingwaypoint.add(data);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reff.addListenerForSingleValueEvent(valueEventListener);



        return root;

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
        polylineOptions.color(Color.RED);
        polylineOptions.width(4);
        gpsTrack = mMap.addPolyline(polylineOptions);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
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

        if(rusz==true)
        {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(17 ));
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
        DecimalFormat dfsumam = new DecimalFormat("#.#");
        MDistance.setText(""+dfsumam.format(suma)+"m");
        double km= 3.6;

        int elapsedMillis = (int) (SystemClock.elapsedRealtime() - chronometer.getBase());
        secs= elapsedMillis/1000;
        final double avgspeed= (suma/secs)* km ;
        final double calory= (suma*0.70)/1000;
        highstedaverage.add(avgspeed);
        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormat dfcalory = new DecimalFormat("#.#");
        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(df.format(avgspeed));
        text.append(Html.fromHtml("<sup>km</sup>/<sub>h</sub>"));
        MSpeed.setText(text);
        System.out.println(calory);
        MCalory.setText(dfcalory.format(calory));
        if(suma>100)
        {
            textToSpeech=new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status!= TextToSpeech.ERROR)
                    {
                        textToSpeech.setLanguage(new Locale("pl", "PL"));
                        DecimalFormat df = new DecimalFormat("0") ;
                        String text=String.valueOf(df.format(avgspeed)+"kilometrów na godzine");
                        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);

                    }
                }
            });
        }





        final double finalSuma = suma;
        MEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String user_id = mAuth.getCurrentUser().getUid();
                DecimalFormat df = new DecimalFormat("#.##");

                String szybkosc= df.format(avgspeed).toString();
                String kalorie = df.format(calory).toString() ;
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
                historia.child("kalorie").setValue(kalorie);
                DecimalFormat dfsuma = new DecimalFormat("#.##");
                historia.child("dystans").setValue(dfsuma.format(suma));
                    historia.child("highspeed").setValue( dfsuma.format(Collections.max(highstedaverage)));
                Toast.makeText(getActivity().getApplicationContext(),"Dobry trening",Toast.LENGTH_SHORT).show();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentSimplyTrainingListener){
            listener= (FragmentSimplyTrainingListener) context;

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
