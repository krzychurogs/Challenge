package com.example.challenge;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.maps.android.PolyUtil;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FragmentRoad extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener{
    DatabaseReference reff,reffs,reffcheck,reffchecktwo,reffcheckend,reffcheckstart,reffpkt;
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
    TextView kalorie,yourScore;
    TextView labeldistance,labelspeed,labelkalorie,labelczas;
    private LatLng lastKnownLatLng;
    private LatLng lastKnownLatLngFirst;
    List<String>listofplace=new ArrayList<String>();
    List<String>listofplacefirstcheck=new ArrayList<String>();
    List<String>markerfirst=new ArrayList<String>();
    List<String>markersecond=new ArrayList<String>();
    List<String>markerfinal=new ArrayList<String>();
    List<String>markerstart=new ArrayList<String>();
    List<String>listofplacesecondcheck=new ArrayList<String>();
    List<String>listofplacemetaheck=new ArrayList<String>();
    List<String>listofplacestartheck=new ArrayList<String>();
    private Polyline gpsTrack;
    List<String>listofnamedup=new ArrayList<String>();
    List<String>listofeachtrainingwaypoint=new ArrayList<>();
    List<String>listofeachfirstwaypoint=new ArrayList<>();
    List<String>listofeachsecondwaypoint=new ArrayList<>();
    List<String>listofeachlastpoint=new ArrayList<>();
    List<String>listofeachstartpoint=new ArrayList<>();
    List<String>listofnamefrienddup=new ArrayList<String>();
    List<String>listofavgefrienddup=new ArrayList<String>();
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
    List<Boolean>listofCheckLocks=new ArrayList<Boolean>();
    List<String>listoffriendname=new ArrayList<String>();
    List<String>listoffriendkey=new ArrayList<String>();
    ConstraintLayout tl,tfriend;
    boolean dodaneDoBazy=false;
    boolean dodaneDoBazy1=false;
    boolean dodaneDoBazy2=false;
    boolean startcheck=false;
    ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    ArrayList<LatLng> coordListFirst = new ArrayList<LatLng>();
    ArrayList<LatLng> coordListSecond = new ArrayList<LatLng>();
    ArrayList<LatLng> coordListLast = new ArrayList<LatLng>();
    ArrayList<LatLng> coordListStart = new ArrayList<LatLng>();
    private RecyclerView mRecyclerView,mRecyclerViewFriend;
    private TableAdapter mAdapter,friendAdapter;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutFriendManager;
    ArrayList<TableItem>exampleList=new ArrayList<>();
    ArrayList<TableItem>friendList=new ArrayList<>();
    ArrayList<TableItem>reverseList=new ArrayList<>();
    ArrayList<TableItem>reverseListFriend=new ArrayList<>();
    List<String>reverselistofnamedup=new ArrayList<String>();
    List<String>reverselistofavgdup=new ArrayList<String>();
    List<String>reverselistofFriendnamedup=new ArrayList<String>();
    List<String>reverselistofFriendavgdup=new ArrayList<String>();
    List<String>listofavgDuplicates=new ArrayList<String>();
    List<String>myname=new ArrayList<String>();
    List<String>listofavgdup=new ArrayList<String>();
    List<String>listofnameDuplicates=new ArrayList<String>();
    List<String>listofName=new ArrayList<String>();
    List<String>listofFriendnameDuplicates=new ArrayList<String>();
    List<Boolean>checkWin=new ArrayList<Boolean>();
    boolean secondchecklock=false;
    boolean firstchecklock=false;
    boolean lastchecklock=false;
    int counter=1;
    int counterfriend=1;
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
       //dodawanietrasy();
        setWinRoad();
        final View root = inflater.inflate(R.layout.fragment_road, container, false);
        MStart=(ImageButton)root.findViewById(R.id.start);
        MDistance=(TextView)root.findViewById(R.id.distance);
        mStopTime=(ImageButton)root.findViewById(R.id.stoptime) ;
        MSort=(ImageButton)root.findViewById(R.id.refsortbutton) ;
        MSpeed=(TextView)root.findViewById(R.id.speed);
        MRefreshButton=(ImageButton)root.findViewById(R.id.refreshbutton);

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
                showNameAfterKey();
                setWinRoad();
                setStartdCheckPoint();
                setFirstCheckPoint();
                setSecondCheckPoint();
                setFinaldCheckPoint();
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
                MDistCheck.setVisibility(View.VISIBLE);
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
                MDistCheck.setVisibility(View.VISIBLE);
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17 ));
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            LatLng loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17 ));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        boolean isInside = PolyUtil.containsLocation(lastKnownLatLng, coordListFirst, true);
       // System.out.println("inside"+isInside);


        if(rusz==true ) {
            if (PolyUtil.containsLocation(lastKnownLatLng, coordListStart, true)==true && startcheck==false)
            {
                startcheck=true;
            }
            else if(PolyUtil.containsLocation(lastKnownLatLng, coordListStart, true) == false && startcheck ==false)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Musisz wystartować z pozycji startowej");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

                if (PolyUtil.containsLocation(lastKnownLatLng, coordList, true)==true && startcheck==true)  {
                    updateTrack();
                } else if (PolyUtil.containsLocation(lastKnownLatLng, coordList, true)== false) {
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




           // System.out.println("w srodku" + isPointInPolygon(lastKnownLatLngLoc, (ArrayList<LatLng>) coordList));


        }

    }
    public void takeWaypoints()
    {
        Bundle bundle=getArguments();

        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child(bundle.getString("name"));
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

              /*  PolygonOptions options = new PolygonOptions()
                        .strokeColor(Color.RED)
                        .fillColor(Color.WHITE)
                        .geodesic(true)
                        .addAll(coordList).addHole(coordListFirst).fillColor(Color.CYAN)
                        .addHole(coordListSecond).fillColor(Color.CYAN   )
                        .addHole(coordListLast).fillColor(Color.CYAN).addHole(coordListStart).fillColor(Color.CYAN);
                        */
                PolygonOptions options = new PolygonOptions()
                        .strokeColor(Color.RED)
                        .fillColor(Color.WHITE)
                        .geodesic(true)
                        .addAll(coordList).addHole(coordListFirst).fillColor(Color.CYAN).addHole(coordListStart).fillColor(Color.CYAN).
                                addHole(coordListLast).fillColor(Color.CYAN).addHole(coordListSecond).fillColor(Color.CYAN   );

                mMap.addPolygon(options);
                LatLng latLng = new LatLng(coordList.get(0).latitude,coordList.get(0).longitude);
                System.out.println(coordList.get(0).latitude);
                System.out.println(coordList.get(0).longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));



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
        final double calory= (suma*65)/1000;
        DecimalFormat df = new DecimalFormat("#.##");
        MSpeed.setText(""+df.format(avgspeed)+"km/h");
        String user_id = mAuth.getCurrentUser().getUid();
        Bundle bundle=getArguments();

     //   if(PolyUtil.containsLocation(lastKnownLatLng, coordListFirst, true)&& dodaneDoBazy==false)
        if(PolyUtil.containsLocation(lastKnownLatLng, coordListFirst, true)&& dodaneDoBazy==false)
        {


            MSort.setVisibility(View.VISIBLE);
            DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                    ("Customers").child("Road").child(bundle.getString("name")).child("pierwszycheck").push();
            //  road.child("srednia").child("name").child(listofname.get(0)).setValue(avgspeed);
            road.child("name").setValue(listofname.get(0));
            road.child("dystans").setValue(100);
            DecimalFormat dfs = new DecimalFormat("#.##");
            Double twodecil=Double.valueOf(df.format(avgspeed));
            road.child("srednia").setValue(twodecil);
            firstchecklock=true;
            road.child("checkLock").setValue(firstchecklock);

            String pierwszycheck="pierwszycheck";
            MDistCheck.setText("Pierwszy CheckPoint ");
            dodaneDoBazy=true;
            showTable(pierwszycheck,dfs.format(avgspeed));

        }
        DecimalFormat dfs = new DecimalFormat("#.##");
        if(PolyUtil.containsLocation(lastKnownLatLng, coordListSecond, true) && dodaneDoBazy1==false)
        {
            //  showTable();

            MSort.setVisibility(View.VISIBLE);
            DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                    ("Customers").child("Road").child(bundle.getString("name")).child("drugicheck").push();
            road.child("name").setValue(listofname.get(0));
            //road.child("srednia").child("name").child(listofname.get(0)).setValue(avgspeed);
            Double twodecil=Double.valueOf(df.format(avgspeed));
            road.child("srednia").setValue(twodecil);
            road.child("dystans").setValue(200);
            String drugicheck="drugicheck";
            secondchecklock=true;
            road.child("secondcheckLock").setValue(secondchecklock);
            MDistCheck.setText("Drugi CheckPoint ");
            dodaneDoBazy1=true;
            showTable(drugicheck,dfs.format(avgspeed));

        }
        if(PolyUtil.containsLocation(lastKnownLatLng, coordListLast, true) && dodaneDoBazy2==false)
        {
            //  showTable();
            if(firstchecklock == true && secondchecklock==true)
            {

                MSort.setVisibility(View.VISIBLE);
                DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                        ("Customers").child("Road").child(bundle.getString("name")).child("meta").push();
                road.child("name").setValue(listofname.get(0));
                //road.child("srednia").child("name").child(listofname.get(0)).setValue(avgspeed);
                Double twodecil=Double.valueOf(df.format(avgspeed));
                road.child("srednia").setValue(twodecil);
                road.child("dystans").setValue(500);
                lastchecklock=true;
                road.child("lastchecklock").setValue(lastchecklock);
                String meta="meta";
                MDistCheck.setText("Meta ");
                dodaneDoBazy2=true;
                stopChronometer();
                rusz=false;
                MStart.setVisibility(View.INVISIBLE);
                mStopTime.setVisibility(View.INVISIBLE);
                MEnd.setVisibility(View.VISIBLE);
                showTable(meta,dfs.format(avgspeed));
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Koniec Treningu, aby zakonczyć i minąć statystyki nacisnij przycisk poniżej");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MEnd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent myIntent = new Intent(getContext(), MainActivity.class);
                                        startActivity(myIntent);
                                    }
                                });

                            }
                        });

                alertDialog.show();
            }

            else {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Którys z checkpointów pominięty");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();


                            }
                        });

                alertDialog.show();
            }
        }

        final double finalSuma = suma;


        MRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSort.setVisibility(View.VISIBLE);
                MRefreshButton.setVisibility(View.INVISIBLE);
                tl.setVisibility(View.VISIBLE);
                showvisibledata();

            }
        });
        MSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showvisbilityscore();
                MRefreshButton.setVisibility(View.VISIBLE);
                MSort.setVisibility(View.INVISIBLE);
                MRefreshButton.setVisibility(View.VISIBLE);
            }
        });

        gpsTrack.setPoints(points);
    }
    public void showTable(final String check, final String avgcheck)
    {
        counter=1;
        counterfriend=1;
        exampleList.clear();
        listofavgefrienddup.clear();
        listofFriendnameDuplicates.clear();
        listofnamefrienddup.clear();
        listofnameDuplicates.clear();

        reverselistofnamedup.clear(); reverselistofavgdup.clear();
        reverseList.clear();
        listofavgdup.clear();
        listofnamedup.clear();
        friendList.clear();
        exampleList.clear();
        yourScore.setText("Twoja aktualna średnia "+avgcheck+" km/h");
        String user_id = mAuth.getCurrentUser().getUid();
        final int limit=20;
        final Bundle bundle=getArguments();
        System.out.println("checkwin"+checkWin.get(0));
        reffname= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child(bundle.getString("name"))
                .child(check).orderByChild("srednia").limitToLast(limit);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                int count=10;
                int max=20;
                int maxfr=5;
                int size=0;
                int sizefr=0;
                int licz=2;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(max>size)
                    { String name =    String.valueOf(ds.child("name").getValue() );

                        String srednia =  String.valueOf(ds.child("srednia").getValue());
                        String dystans =  String.valueOf(ds.child("dystans").getValue());
                        String checkLock=  String.valueOf(ds.child("checkLock").getValue());
                        String secondcheckLock=  String.valueOf(ds.child("checkLock").getValue());
                        String lastcheckLock=  String.valueOf(ds.child("checkLock").getValue());
                        listofCheckLocks.add(Boolean.valueOf(checkLock));
                        listofCheckLocks.add(Boolean.valueOf(secondcheckLock));
                        listofCheckLocks.add(Boolean.valueOf(lastcheckLock ));

                        if(name!=null && srednia!=null)
                        {
                            listofnamedup.add(name);
                            listofavgdup.add(srednia);
                        }

                        for(int i=0;i<listoffriendname.size();i++) {
                            if(name.contains(listoffriendname.get(i)))
                            {
                                listofnamefrienddup.add(name);
                                listofavgefrienddup.add(srednia);
                            }

                        }



                    }
                    size++;
                    count--;


                }


                reverselistofnamedup=reverseList(listofnamedup);
                reverselistofavgdup=reverseList(listofavgdup);
                for(int i=0;i<reverselistofnamedup.size();i++)
                {

                    if(!listofnameDuplicates.contains(reverselistofnamedup.get(i)))
                    {

                        listofnameDuplicates.add(reverselistofnamedup.get(i));

                        if(!reverselistofnamedup.get(i).equals("null"))
                        {
                            exampleList.add(new TableItem(String.valueOf(counter),reverselistofnamedup.get(i),reverselistofavgdup.get(i)+" km/h"));
                            counter++;
                        }

                    }

                }
                if(check.equals("meta")&& counter == 1 && reverselistofnamedup.get(0).equals(myname.get(0)) && checkWin.get(0)!=true)
                {
                    DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                            ("Customers").child("Road").child(bundle.getString("name"));
                    road.child("avgmax").setValue(reverselistofavgdup.get(0));
                    showPoints();
                    System.out.println(check);
                }



                Set<String> hashSet = new LinkedHashSet(listofnamefrienddup);
                ArrayList<String> removedDuplicatesName = new ArrayList(hashSet);
                Set<String> hashSet1 = new LinkedHashSet(listofavgefrienddup);
                ArrayList<String> removedDuplicatesAvg= new ArrayList(hashSet1);

                reverselistofFriendnamedup=reverseList(removedDuplicatesName);
                reverselistofFriendavgdup=reverseList(removedDuplicatesAvg);
                Set<String> hashSet2 = new LinkedHashSet(reverselistofFriendnamedup);
                ArrayList<String> finalremovedDuplicatesName= new ArrayList(hashSet2);

                Set<String> hashSet3 = new LinkedHashSet(reverselistofFriendavgdup);
                ArrayList<String> finalremovedDuplicatesAvg= new ArrayList(hashSet3);




                for(int i=0;i<finalremovedDuplicatesName.size();i++)
                {


                    if(!listofFriendnameDuplicates.contains(finalremovedDuplicatesName.get(i)))
                    {

                        listofFriendnameDuplicates.add(finalremovedDuplicatesName.get(i));

                        if(!finalremovedDuplicatesName.get(i).equals("null"))
                        {

                            friendList.add(new TableItem(String.valueOf(counterfriend),finalremovedDuplicatesName.get(i),finalremovedDuplicatesAvg.get(i)+" km/h"));
                            counterfriend++;
                        }

                    }

                }

                mRecyclerViewFriend.setHasFixedSize(true);
                reverseListFriend = reverseList(friendList);
                mLayoutFriendManager = new LinearLayoutManager(getActivity());
                friendAdapter= new TableAdapter(friendList);
                mRecyclerViewFriend.setLayoutManager(mLayoutFriendManager);
                mRecyclerViewFriend.setAdapter(friendAdapter);

                reverseList = reverseList(exampleList);
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mAdapter = new TableAdapter(exampleList);
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
          latLngPolygon.add(new LatLng(50.025486, 21.998958));

            /*latLngPolygon.add(new LatLng(50.030518,  22.004406));//delhi
            latLngPolygon.add(new LatLng(50.027030,  22.001616));//gujarat
            latLngPolygon.add(new LatLng(50.025762,  21.998054));//pune
            latLngPolygon.add(new LatLng(50.023763,  22.000575));
            latLngPolygon.add(new LatLng(50.026093,  22.002335));
            latLngPolygon.add(new LatLng(50.029078,  22.003568));
            latLngPolygon.add(new LatLng(50.029588,  22.004019));
            latLngPolygon.add(new LatLng(50.030132,  22.004470));



            latLngPolygon.add(new LatLng(50.025508, 21.998928));//delhi
            latLngPolygon.add(new LatLng(50.025516, 21.999046));//gujarat
            latLngPolygon.add(new LatLng(50.025455, 21.999045));//pune
            latLngPolygon.add(new LatLng(50.025457, 21.998916));
*/


        }
        // System.out.println("w srodku"+isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) latLngPolygon));
        DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Road").child("druga trasa").child("drugicheck");
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
      // road.child("checkwaypointy").setValue(nameList);
        road.child("marker").setValue(nameList);
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

        labelspeed.setVisibility(View.INVISIBLE);
        labeldistance.setVisibility(View.INVISIBLE);

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
        labelspeed.setVisibility(View.VISIBLE);
        labeldistance.setVisibility(View.VISIBLE);
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
    public void setFirstCheckPoint()
    {
        Bundle bundle=getArguments();
        reffcheck= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child(bundle.getString("name")).child("pierwszycheck");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                List<String>markerlist=new ArrayList<String>();
                for(DataSnapshot snapshot : dataSnapshot.child("checkwaypointy").getChildren()) {
                    String data = snapshot.getValue(String.class);
                    listofeachfirstwaypoint.add(data);

                }
                String marker="";
                for(DataSnapshot snapshot : dataSnapshot.child("marker").getChildren()) {
                    marker = snapshot.getValue(String.class);
                }
                String markerSplit[]=marker.split(" ");
                for (String w : markerSplit) {
                    markerlist.add(w);
                }
                String marketSplitdot[]=markerlist.get(1).split(",");
                for (String w : marketSplitdot) {
                    String str1 = w.replace("(", "");
                    String strnew = str1.replace(")", "");
                    markerfirst.add(strnew);
                }

                Double latMark= Double.valueOf(markerfirst.get(1));
                Double longtideMark=  Double.valueOf(markerfirst.get(0));
                LatLng first = new LatLng(longtideMark, latMark);
                mMap.addMarker(new MarkerOptions()
                        .position(first)
                        .title("Pierwszy Check"));

                for(int i=0;i<listofeachfirstwaypoint.size();i++)
                {
                    String word[] = listofeachfirstwaypoint.get(i).split(" ");
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

                            listofplacefirstcheck.add(strnew);
                        }
                    }

                }
                Double latide= Double.valueOf(listofplacefirstcheck.get(1));
                Double longtide=  Double.valueOf(listofplacefirstcheck.get(0));

                for(int i=0;i<listofplacefirstcheck.size();i++) {


                    longtide=Double.valueOf(listofplacefirstcheck.get(i));

                    if(listofplacefirstcheck.size() > i + 1){
                        latide = Double.valueOf(listofplacefirstcheck.get(++i).toString().replace("]","")); //Change here
                    }
                    lastKnownLatLngFirst= new LatLng(longtide, latide);

                    coordListFirst.add(lastKnownLatLngFirst);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reffcheck.addListenerForSingleValueEvent(valueEventListener);
    }
    public void setSecondCheckPoint()
    {
        Bundle bundle=getArguments();
        reffchecktwo= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child(bundle.getString("name")).child("drugicheck");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                List<String>markerlist=new ArrayList<String>();
                for(DataSnapshot snapshot : dataSnapshot.child("checkwaypointy").getChildren()) {
                    String data = snapshot.getValue(String.class);
                    listofeachsecondwaypoint.add(data);

                }
                String marker="";
                for(DataSnapshot snapshot : dataSnapshot.child("marker").getChildren()) {
                    marker = snapshot.getValue(String.class);
                }
                String markerSplit[]=marker.split(" ");
                for (String w : markerSplit) {
                    markerlist.add(w);
                }
                String marketSplitdot[]=markerlist.get(1).split(",");
                for (String w : marketSplitdot) {
                    String str1 = w.replace("(", "");
                    String strnew = str1.replace(")", "");
                        markersecond.add(strnew);
                }
                Double latMark= Double.valueOf(markersecond.get(1));
                Double longtideMark=  Double.valueOf(markersecond.get(0));
                LatLng first = new LatLng(longtideMark, latMark);
                mMap.addMarker(new MarkerOptions()
                        .position(first)
                        .title("Drugi Check"));



                for(int i=0;i<listofeachsecondwaypoint.size();i++)
                {
                    String word[] = listofeachsecondwaypoint.get(i).split(" ");
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

                            listofplacesecondcheck.add(strnew);
                        }
                    }

                }
                Double latide= Double.valueOf(listofplacesecondcheck.get(1));
                Double longtide=  Double.valueOf(listofplacesecondcheck.get(0));

                for(int i=0;i<listofplacesecondcheck.size();i++) {


                    longtide=Double.valueOf(listofplacesecondcheck.get(i));

                    if(listofplacesecondcheck.size() > i + 1){
                        latide = Double.valueOf(listofplacesecondcheck.get(++i).toString().replace("]","")); //Change here
                    }
                    lastKnownLatLngFirst= new LatLng(longtide, latide);

                    coordListSecond.add(lastKnownLatLngFirst);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reffchecktwo.addListenerForSingleValueEvent(valueEventListener);
    }
    public void setFinaldCheckPoint()
    {
        Bundle bundle=getArguments();
        reffchecktwo= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child(bundle.getString("name")).child("meta");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                List<String>markerlist=new ArrayList<String>();
                for(DataSnapshot snapshot : dataSnapshot.child("checkwaypointy").getChildren()) {
                    String data = snapshot.getValue(String.class);
                    listofeachlastpoint.add(data);
                }
                String marker="";
                for(DataSnapshot snapshot : dataSnapshot.child("marker").getChildren()) {
                    marker = snapshot.getValue(String.class);
                }
                String markerSplit[]=marker.split(" ");
                for (String w : markerSplit) {
                    markerlist.add(w);
                }
                String marketSplitdot[]=markerlist.get(1).split(",");
                for (String w : marketSplitdot) {
                    String str1 = w.replace("(", "");
                    String strnew = str1.replace(")", "");
                    markerstart.add(strnew);
                }
                Double latMark= Double.valueOf(markerstart.get(1));
                Double longtideMark=  Double.valueOf(markerstart.get(0));
                LatLng first = new LatLng(longtideMark, latMark);
                mMap.addMarker(new MarkerOptions()
                        .position(first)
                        .title("Meta"));



                for(int i=0;i<listofeachlastpoint.size();i++)
                {
                    String word[] = listofeachlastpoint.get(i).split(" ");
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

                            listofplacemetaheck.add(strnew);
                        }
                    }

                }
                Double latide= Double.valueOf(listofplacemetaheck.get(1));
                Double longtide=  Double.valueOf(listofplacemetaheck.get(0));

                for(int i=0;i<listofplacemetaheck.size();i++) {


                    longtide=Double.valueOf(listofplacemetaheck.get(i));

                    if(listofplacemetaheck.size() > i + 1){
                        latide = Double.valueOf(listofplacemetaheck.get(++i).toString().replace("]","")); //Change here
                    }
                    lastKnownLatLngFirst= new LatLng(longtide, latide);

                    coordListLast.add(lastKnownLatLngFirst);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reffchecktwo.addListenerForSingleValueEvent(valueEventListener);
    }
    public void setStartdCheckPoint()
    {
        Bundle bundle=getArguments();
        reffcheckstart= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child(bundle.getString("name")).child("start");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                List<String>markerlist=new ArrayList<String>();
                for(DataSnapshot snapshot : dataSnapshot.child("checkwaypointy").getChildren()) {
                    String data = snapshot.getValue(String.class);
                    listofeachstartpoint.add(data);

                }
                String marker="";
                for(DataSnapshot snapshot : dataSnapshot.child("marker").getChildren()) {
                    marker = snapshot.getValue(String.class);
                }
                String markerSplit[]=marker.split(" ");
                for (String w : markerSplit) {
                    markerlist.add(w);
                }
                String marketSplitdot[]=markerlist.get(1).split(",");
                for (String w : marketSplitdot) {
                    String str1 = w.replace("(", "");
                    String strnew = str1.replace(")", "");
                    markerfinal.add(strnew);
                }
                Double latMark= Double.valueOf(markerfinal.get(1));
                Double longtideMark=  Double.valueOf(markerfinal.get(0));
                LatLng first = new LatLng(longtideMark, latMark);
                mMap.addMarker(new MarkerOptions()
                        .position(first)
                        .title("Start"));



                for(int i=0;i<listofeachstartpoint.size();i++)
                {
                    String word[] = listofeachstartpoint.get(i).split(" ");
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

                            listofplacestartheck.add(strnew);
                        }
                    }

                }
                Double latide= Double.valueOf(listofplacestartheck.get(1));
                Double longtide=  Double.valueOf(listofplacestartheck.get(0));

                for(int i=0;i<listofplacestartheck.size();i++) {


                    longtide=Double.valueOf(listofplacestartheck.get(i));

                    if(listofplacestartheck.size() > i + 1){
                        latide = Double.valueOf(listofplacestartheck.get(++i).toString().replace("]","")); //Change here
                    }
                    lastKnownLatLngFirst= new LatLng(longtide, latide);

                    coordListStart.add(lastKnownLatLngFirst);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reffcheckstart.addListenerForSingleValueEvent(valueEventListener);
    }
    public void showPoints()
    {
        final String user_id = mAuth.getCurrentUser().getUid();
        reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

        reffpkt.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    int pktinfo = dataSnapshot.getValue(Integer.class);
                    String pktint=String.valueOf(pktinfo);
                    // System.out.println("poin"+pointstolvl);
                    //  System.out.println(pktint);
                    int max=600;
                    int diff=max+pktinfo;
                    DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                            ("Customers").child("Historia").child(user_id).child("punkty");
                    pkt.setValue(diff);
                    Bundle bundle=getArguments();
                    DatabaseReference infoadd = FirebaseDatabase.getInstance().getReference().child("Users").child
                            ("Customers").child("Historia").child(user_id).child(bundle.getString("name"));
                    infoadd.setValue(true);
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Zdobyłes punkty za najlepszy czas na trasie");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });

    }
    public void showNameAfterKey()
    {
        String user_id = mAuth.getCurrentUser().getUid();
        reffs= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        reffs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                String namefriend =  String.valueOf(dataSnapshot.child("name").getValue());
                System.out.println("nf"+namefriend);
                myname.add(namefriend);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setWinRoad()
    {
        final Bundle bundle=getArguments();
        String user_id = mAuth.getCurrentUser().getUid();

        reffs= FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child(bundle.getString("name"));
        reffs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                String checkwin =  String.valueOf(dataSnapshot.getValue());
                System.out.println("check"+checkwin);
                checkWin.add(Boolean.valueOf(checkwin));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}