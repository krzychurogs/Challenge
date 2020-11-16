
package com.example.challenge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class FragmentRoad extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener{
    DatabaseReference reff,reffs;
    Query reffname;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location flocation;
    private FragmentRoadListener listener;
    Button next,back;
    ImageButton changetable;
    TextView textdistance,textspeed,kalorie,yourScore;
    TextView labeldistance,labelspeed,labelkalorie,labelczas;
    private LatLng lastKnownLatLng;
    List<String>listofplace=new ArrayList<String>();
    List<String>avglist=new ArrayList<String>();
    private Polyline gpsTrack;
    List<String>listofnamedup=new ArrayList<String>();
    List<String>listofeachtrainingwaypoint=new ArrayList<>();
    public static int counttrain;
    List<LatLng> points ;
    int secs;
    public float suma=0;
    private Chronometer chronometer;
    TextView MDistance,MSpeed,MDistCheck;
    TextView MFriendInfo;
    TextToSpeech textToSpeech;
    ImageButton MEnd;
    ImageButton changeToStats;
    ImageButton mStopTime;
    ImageButton MStart,MRefreshButton,MSort;
    static boolean rusz=false;

    private boolean running;
    private long pauseOffset;
    List<String>listofname=new ArrayList<String>();
    List<String>listoffriendname=new ArrayList<String>();
    List<String>listoffriendkey=new ArrayList<String>();
    ConstraintLayout tl,tfriend;
    boolean dodaneDoBazy=false;
    boolean dodaneDoBazy1=false;
    boolean dodaneDoBazy2=false;
    ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    private RecyclerView mRecyclerView,mRecyclerViewFriend;
    private TableAdapter mAdapter,friendAdapter;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutFriendManager;
    ArrayList<TableItem>exampleList=new ArrayList<>();
    ArrayList<TableItem>friendList=new ArrayList<>();
    ArrayList<TableItem>reverseList=new ArrayList<>();
    ArrayList<TableItem>reverseListFriend=new ArrayList<>();

    int textlength = 0;

    public interface FragmentRoadListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        Bundle bundle=getArguments();
        System.out.println("bund"+bundle.getString("name"));


        final View root = inflater.inflate(R.layout.fragment_road, container, false);
        MStart=(ImageButton)root.findViewById(R.id.start);
        MDistance=(TextView)root.findViewById(R.id.distance);
        mStopTime=(ImageButton)root.findViewById(R.id.stoptime) ;
        MSort=(ImageButton)root.findViewById(R.id.refsortbutton) ;
        MSpeed=(TextView)root.findViewById(R.id.speed);
        MRefreshButton=(ImageButton)root.findViewById(R.id.refreshbutton);
        kalorie=(TextView)root.findViewById(R.id.kalorie);
        tl = (ConstraintLayout) root.findViewById(R.id.main_table);
        tfriend = (ConstraintLayout) root.findViewById(R.id.friend_table);
        mRecyclerView = root.findViewById(R.id.recyclerViewMovieList);
        mRecyclerViewFriend= root.findViewById(R.id.recyclerViewFriend);
        MEnd=(ImageButton)root.findViewById(R.id.end);
        changetable=(ImageButton) root.findViewById(R.id.changetable);
        changeToStats=(ImageButton)root.findViewById(R.id.changetabletostats);
        yourScore=(TextView)root.findViewById(R.id.textViewYourScore);
        labeldistance=(TextView)root.findViewById(R.id.dystanslabel);
        labelspeed=(TextView)root.findViewById(R.id.speedlabel);
        MDistCheck=(TextView)root.findViewById(R.id.dystanscheck);
        MFriendInfo=(TextView)root.findViewById(R.id.friendInfo);
        labelkalorie=(TextView)root.findViewById(R.id.kalorielabel);
        labelczas=(TextView)root.findViewById(R.id.czaslabel);
        chronometer = root.findViewById(R.id.czas);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        mStopTime.setVisibility(View.GONE);
        MEnd.setVisibility(View.GONE);
        MDistCheck.setVisibility(View.INVISIBLE);
        MFriendInfo.setVisibility(View.INVISIBLE);
        MSort.setVisibility(View.INVISIBLE);
        yourScore.setVisibility(View.INVISIBLE);
        tl.setVisibility(View.INVISIBLE );
        changetable.setVisibility(View.INVISIBLE);
        tfriend.setVisibility(View.INVISIBLE);
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
        showFriend();
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
        reffname= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    listofname.add(name);
                    // imie po id

                }


                takeWaypoints();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reffname.addListenerForSingleValueEvent(valueEventListener1);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);


        changetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tl.setVisibility(View.INVISIBLE);
                mRecyclerViewFriend.setVisibility(View.VISIBLE);
                tfriend.setVisibility(View.VISIBLE);
                changetable.setVisibility(View.INVISIBLE);
                changeToStats.setVisibility(View.VISIBLE);
                MFriendInfo.setVisibility(View.VISIBLE);
            }
        });

        changeToStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tl.setVisibility(View.VISIBLE);
                mRecyclerViewFriend.setVisibility(View.INVISIBLE);
                tfriend.setVisibility(View.INVISIBLE);
                MFriendInfo.setVisibility(View.INVISIBLE);
                changetable.setVisibility(View.VISIBLE);
                changeToStats.setVisibility(View.INVISIBLE);
            }
        });


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

        if(rusz==true) {


            lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            System.out.println("w srodku" + isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) coordList));

            if (isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) coordList) == true) {
                updateTrack();
            } else if (isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) coordList) == false) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Jesteś poza obszarem");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }





        }

    }
    public void takeWaypoints()
    {
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child("pierwsza trasa");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot snapshot : dataSnapshot.child("waypointy").getChildren()) {
                    String data = snapshot.getValue(String.class);
                    listofeachtrainingwaypoint.add(data);

                }



                for(int i=0;i<listofeachtrainingwaypoint.size();i++)
                {
                    String word[] = listofeachtrainingwaypoint.get(i).split(" ");
                    for (String w : word) {
                        newlist.add(w);
                    }
                }


                    for (int i = 0; i < newlist.size(); i++) {
                        if (i % 3 == 0) {
                        }
                        else {
                            String words[] = newlist.get(i).split(",");
                            for (String w : words) {
                                String str1 = w.replace("(", "");
                                String strnew = str1.replace(")", "");

                                listofplace.add(strnew);
                            }
                        }

                }
                Double latide= Double.valueOf(listofplace.get(1));
                Double longtide=  Double.valueOf(listofplace.get(0));
                for(int i=0;i<listofplace.size();i++) {


                    longtide=Double.valueOf(listofplace.get(i));

                    if(listofplace.size() > i + 1){
                        latide = Double.valueOf(listofplace.get(++i).toString().replace("]","")); //Change here
                    }
                    lastKnownLatLng= new LatLng(longtide, latide);

                    coordList.add(lastKnownLatLng);
                }

                PolygonOptions options = new PolygonOptions()
                        .strokeColor(Color.RED)
                        .fillColor(Color.GRAY)
                        .geodesic(true)
                        .addAll(coordList);

                Polygon pfad = mMap.addPolygon(options);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reff.addListenerForSingleValueEvent(valueEventListener);
    }
    private void updateTrack() {
        MSort.setVisibility(View.INVISIBLE);
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

        if(suma>100)
        {
            textToSpeech=new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
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
        }


        String user_id = mAuth.getCurrentUser().getUid();


        Bundle bundle=getArguments();

        if(suma>=1 && suma<15 && dodaneDoBazy==false)
        {

            MSort.setVisibility(View.VISIBLE);
            DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                    ("Customers").child("Road").child(bundle.getString("name")).child("pierwszycheck").push();
            //  road.child("srednia").child("name").child(listofname.get(0)).setValue(avgspeed);
            road.child("name").setValue(listofname.get(0));
            road.child("dystans").setValue(100);
            DecimalFormat dfs = new DecimalFormat("#.##");

            road.child("srednia").setValue(dfs.format(avgspeed));
            String pierwszycheck="pierwszycheck";
            MDistCheck.setText("Pierwszy CheckPoint "+100 +"m");
            dodaneDoBazy=true;
            showTable(pierwszycheck,dfs.format(avgspeed));

        }
        if(suma>=15 && dodaneDoBazy1==false)
        {
            //  showTable();
            MSort.setVisibility(View.VISIBLE);
            DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                    ("Customers").child("Road").child(bundle.getString("name")).child("drugicheck").push();
            road.child("name").setValue(listofname.get(0));
            //road.child("srednia").child("name").child(listofname.get(0)).setValue(avgspeed);
            DecimalFormat dfs = new DecimalFormat("#.##");
            road.child("srednia").setValue(dfs.format(avgspeed));
            road.child("dystans").setValue(200);
            String drugicheck="drugicheck";
            MDistCheck.setText("Drugi CheckPoint "+200 +"m");
            dodaneDoBazy1=true;
            showTable(drugicheck,dfs.format(avgspeed));

        }
        if(suma>=20 && dodaneDoBazy2==false)
        {
            //  showTable();
            MSort.setVisibility(View.VISIBLE);
            DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                    ("Customers").child("Road").child(bundle.getString("name")).child("meta").push();
            road.child("name").setValue(listofname.get(0));
            //road.child("srednia").child("name").child(listofname.get(0)).setValue(avgspeed);
            DecimalFormat dfs = new DecimalFormat("#.##");
            road.child("srednia").setValue(dfs.format(avgspeed));
            road.child("dystans").setValue(500);
            String meta="meta";
            MDistCheck.setText("Meta "+500 +"m");
            dodaneDoBazy2=true;
            showTable(meta,dfs.format(avgspeed));

        }

        final double finalSuma = suma;


        MRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSort.setVisibility(View.VISIBLE);
                MRefreshButton.setVisibility(View.INVISIBLE);
                tl.setVisibility(View.VISIBLE);
                showvisibledata();
                if(MSort.getVisibility() == View.VISIBLE) {

                }
                else if(MRefreshButton.getVisibility() == View.INVISIBLE)
                {

                }
            }
        });
        MSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showvisbilityscore();
                MRefreshButton.setVisibility(View.VISIBLE);
                MSort.setVisibility(View.INVISIBLE);
                MRefreshButton.setVisibility(View.VISIBLE);
                if(MRefreshButton.getVisibility() == View.VISIBLE) {

                }
                else if(MSort.getVisibility() == View.INVISIBLE)
                {

                }
            }
        });

        gpsTrack.setPoints(points);


    }
        public void showTable(final String check, final String avgcheck)
    {
        exampleList.clear();
        yourScore.setText("Twoja aktualna średnia "+avgcheck+" km/h");
        String user_id = mAuth.getCurrentUser().getUid();
        final int limit=15;
        final Bundle bundle=getArguments();

        reffname= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child(bundle.getString("name"))
                .child(check).orderByChild("srednia").limitToLast(limit);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                int count=10;
                int max=10;
                int maxfr=5;
                int size=0;
                int sizefr=0;
                int licz=2;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(max>size)
                        {
                            String name =    String.valueOf(ds.child("name").getValue() );
                            String srednia =  String.valueOf(ds.child("srednia").getValue());
                            String dystans =  String.valueOf(ds.child("dystans").getValue());
                            listofnamedup.add(name);
                            for(int i=0;i<listoffriendname.size();i++) {
                                if(name.contains(listoffriendname.get(i)))
                                {
                                    if(maxfr>sizefr)
                                    {
                                        System.out.println("Srednia "+listoffriendname.get(i)+" "+srednia);
                                        friendList.add((new TableItem(String.valueOf(licz),listoffriendname.get(i),srednia+" km/h")));
                                        licz--;
                                        sizefr++;
                                    }

                                }
                            }
                            if(check.equals("meta")&& count==1)
                            {
                                DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                                        ("Customers").child("Road").child(bundle.getString("name"));
                                road.child("avgmax").setValue(srednia);
                            }
                            exampleList.add(new TableItem(String.valueOf(count),name,srednia+" km/h"));
                        }
                    size++;
                        count--;


                }

                mRecyclerViewFriend.setHasFixedSize(true);
                reverseListFriend = reverseList(friendList);
                mLayoutFriendManager = new LinearLayoutManager(getActivity());
                friendAdapter= new TableAdapter(reverseListFriend);
                mRecyclerViewFriend.setLayoutManager(mLayoutFriendManager);
                mRecyclerViewFriend.setAdapter(friendAdapter);




                reverseList = reverseList(exampleList);
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mAdapter = new TableAdapter(reverseList);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reffname.addListenerForSingleValueEvent(valueEventListener);


    }
    private boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

            return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentRoadListener){
            listener= (FragmentRoadListener) context;

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
    public void dodawanietrasy()
    {
        final List<LatLng> latLngPolygon = new ArrayList<>();
        {
            latLngPolygon.add(new LatLng(50.03,21.9987751));//delhi
            latLngPolygon.add(new LatLng(50.0227154,21.9997975));//gujarat
            latLngPolygon.add(new LatLng(50.002724,21.9927758));//pune
            latLngPolygon.add(new LatLng(50.022724,21.9527758));
        }
       // System.out.println("w srodku"+isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) latLngPolygon));
        DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Road").child("pierwsza trasa");
        String [] table;  //Referencja do tablicy
        int nElems=0;
        table=new String[5];
        for(int j=0;j<latLngPolygon.size();j++) {


            if (nElems >= table.length) {
                String[] locTable = new String[table.length * 2];
                for (int i = 0; i < table.length; i++) locTable[i] = table[i];
                table = locTable;
            }

            table[nElems] = String.valueOf(latLngPolygon.get(j));        // Wstawiamy element
            nElems++;
        }
        List nameList = new ArrayList<String>(Arrays.asList(table));

        road.child("waypointy").setValue(nameList);
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
    public void showvisbilityscore()
    {
        MDistance.setVisibility(View.INVISIBLE);
        MSpeed.setVisibility(View.INVISIBLE);
        chronometer.setVisibility(View.INVISIBLE);
        labelkalorie.setVisibility(View.INVISIBLE);
        labelspeed.setVisibility(View.INVISIBLE);
        labeldistance.setVisibility(View.INVISIBLE);
        kalorie.setVisibility(View.INVISIBLE);
        labelczas.setVisibility(View.INVISIBLE);
        tl.setVisibility(View.VISIBLE);
        mRecyclerViewFriend.setVisibility(View.INVISIBLE);
        tfriend.setVisibility(View.VISIBLE);
        changetable.setVisibility(View.VISIBLE);
        MDistCheck.setVisibility(View.VISIBLE);
        MSort.setVisibility(View.VISIBLE);
        yourScore.setVisibility(View.VISIBLE);
    }
    public void showvisibledata()
    {
        MDistance.setVisibility(View.VISIBLE);
        MSpeed.setVisibility(View.VISIBLE);
        chronometer.setVisibility(View.VISIBLE);
        labelkalorie.setVisibility(View.VISIBLE);
        labelspeed.setVisibility(View.VISIBLE);
        labeldistance.setVisibility(View.VISIBLE);
        kalorie.setVisibility(View.VISIBLE);
        labelczas.setVisibility(View.VISIBLE);
        MDistCheck.setVisibility(View.VISIBLE);
        tl.setVisibility(View.INVISIBLE);
        changetable.setVisibility(View.INVISIBLE);
        tfriend.setVisibility(View.INVISIBLE);
        yourScore.setVisibility(View.INVISIBLE);
        mRecyclerViewFriend.setVisibility(View.INVISIBLE);;
        changeToStats.setVisibility(View.INVISIBLE);
        MDistCheck.setVisibility(View.INVISIBLE);
        MFriendInfo.setVisibility(View.INVISIBLE);
    }
    public void showFriend()
    {
        String user_id = mAuth.getCurrentUser().getUid();
        reffs= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("friends");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String namefriend =  String.valueOf(ds.getKey());
                    listoffriendkey.add(namefriend);
                    showNameAfterId(namefriend);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reffs.addListenerForSingleValueEvent(valueEventListener);
    }
    public static<T> ArrayList<T> reverseList(List<T> list)
    {
        ArrayList<T> reverse = new ArrayList<>(list);
        Collections.reverse(reverse);
        return reverse;
    }

public void showNameAfterId(String key)
{
    String user_id = mAuth.getCurrentUser().getUid();
    reffs= FirebaseDatabase.getInstance().getReference().child("Users").child(key);
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<String>newlist=new ArrayList<String>();
            for(DataSnapshot ds : dataSnapshot.getChildren()) {
               // System.out.println(ds.getKey());
               // System.out.println(ds.getValue());
                String namefriend =  String.valueOf(ds.getValue()   );
                listoffriendname.add(namefriend);

            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    reffs.addListenerForSingleValueEvent(valueEventListener);
}


}