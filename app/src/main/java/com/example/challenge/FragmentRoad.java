package com.example.challenge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
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
    TextView textdistance,textspeed,kalorie;
    TextView labeldistance,labelspeed,labelkalorie,labelczas;
    TextView nicksrednia,dystanscheckpoint,nickfriend;
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
    private Chronometer chronometer;
    TextView MDistance,MSpeed;
    TextToSpeech textToSpeech;
    ImageButton MEnd;
    ImageButton mStopTime;
    ImageButton MStart,MRefreshButton,MSort;
    static boolean rusz=false;
    TableLayout tl;
    private boolean running;
    private long pauseOffset;
    List<String>listofname=new ArrayList<String>();
    List<String>listoffriendname=new ArrayList<String>();
    TableLayout t1;
    boolean dodaneDoBazy=false;
    boolean dodaneDoBazy1=false;






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
       System.out.println("bund"+bundle.getString("number"));


        final View root = inflater.inflate(R.layout.fragment_road, container, false);
        MStart=(ImageButton)root.findViewById(R.id.start);
        MDistance=(TextView)root.findViewById(R.id.distance);
        mStopTime=(ImageButton)root.findViewById(R.id.stoptime) ;
        MSort=(ImageButton)root.findViewById(R.id.refsortbutton) ;
        MSpeed=(TextView)root.findViewById(R.id.speed);
        MRefreshButton=(ImageButton)root.findViewById(R.id.refreshbutton);
        kalorie=(TextView)root.findViewById(R.id.kalorie);
        tl = (TableLayout) root.findViewById(R.id.main_table);
        MEnd=(ImageButton)root.findViewById(R.id.end);
        nicksrednia=(TextView)root.findViewById(R.id.twojnick);
        nickfriend=(TextView)root.findViewById(R.id.twojfriend);
        labeldistance=(TextView)root.findViewById(R.id.dystanslabel);
        labelspeed=(TextView)root.findViewById(R.id.speedlabel);
        labelkalorie=(TextView)root.findViewById(R.id.kalorielabel);
        dystanscheckpoint=(TextView)root.findViewById(R.id.twojdystans);
        labelczas=(TextView)root.findViewById(R.id.czaslabel);
        chronometer = root.findViewById(R.id.czas);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        mStopTime.setVisibility(View.GONE);
        MEnd.setVisibility(View.GONE);
        nicksrednia.setVisibility(View.INVISIBLE);
        nickfriend.setVisibility(View.INVISIBLE);
        dystanscheckpoint.setVisibility(View.INVISIBLE);
        MSort.setVisibility(View.INVISIBLE);
        tl.setVisibility(View.INVISIBLE );
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
                showFriend();

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
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(50.019432, 21.994719), new LatLng(50.023823, 21.995047), new LatLng(50.024486, 21.988947), new LatLng(50.021395, 21.986917))
                .strokeColor(Color.RED)
                .fillColor(Color.GRAY));
        if(rusz==true) {


            lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            ArrayList<LatLng> coordList = new ArrayList<LatLng>();
            for (int count = 0; count < listofeachtrainingwaypoint.size(); count++) {


                List<String> newlist = new ArrayList<String>();
                String word[] = listofeachtrainingwaypoint.get(count).split(" ");
                for (String w : word) {
                    newlist.add(w);
                }


                for (int i = 0; i < newlist.size(); i++) {
                    if (i % 2 == 1) {
                        String words[] = newlist.get(i).split(",");
                        for (String w : words) {
                            String str1 = w.replace("(", "");
                            String strnew = str1.replace(")", "");

                            listofplace.add(strnew);
                        }
                    }
                }
            }
            for (int i = 0; i < listofplace.size(); i++) {
                Double latide = 21.9927641;
                Double longtide = 50.0226329;

                if (i % 2 == 0)//longtiude
                {
                    longtide = Double.valueOf(listofplace.get(i));
                }
                if (i % 2 == 1) {

                    String lat = listofplace.get(i).toString().replace("]", "");
                    latide = Double.valueOf(lat);
                }
                coordList.add(new LatLng(longtide, latide));

            }

            System.out.println("w srodku" + isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) coordList));

            if (isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) coordList) == false) {
                updateTrack();
            } else if (isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) coordList) == true) {
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

            String user_id = mAuth.getCurrentUser().getUid();
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reff.addListenerForSingleValueEvent(valueEventListener);
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



        if(suma>=100 && suma<200 && dodaneDoBazy==false)
        {

                MSort.setVisibility(View.VISIBLE);
                DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                        ("Customers").child("Road").child("pierwsza trasa").child("pierwszycheck").push();
                //  road.child("srednia").child("name").child(listofname.get(0)).setValue(avgspeed);
                road.child("name").setValue(listofname.get(0));
                road.child("dystans").setValue(100);
                DecimalFormat dfs = new DecimalFormat("#.##");

                road.child("srednia").setValue(dfs.format(avgspeed));
                String pierwszycheck="pierwszycheck";
                dodaneDoBazy=true;
                 showTable(pierwszycheck,dfs.format(avgspeed));

        }
         if(suma>=200 && dodaneDoBazy1==false)
        {
            //  showTable();
            MSort.setVisibility(View.VISIBLE);
            DatabaseReference road = FirebaseDatabase.getInstance().getReference().child("Users").child
                    ("Customers").child("Road").child("pierwsza trasa").child("drugicheck").push();
            road.child("name").setValue(listofname.get(0));
            //road.child("srednia").child("name").child(listofname.get(0)).setValue(avgspeed);
            DecimalFormat dfs = new DecimalFormat("#.##");
            road.child("srednia").setValue(dfs.format(avgspeed));
            road.child("dystans").setValue(200);
            String drugicheck="drugicheck";
            dodaneDoBazy1=true;
            showTable(drugicheck,dfs.format(avgspeed));

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
    public void showTable(String check,final String avgcheck)
    {
        tl.refreshDrawableState();
        tl.invalidate();
        tl.removeAllViews();

        String user_id = mAuth.getCurrentUser().getUid();
        reffname= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child("pierwsza trasa")
                .child(check).orderByChild("srednia").limitToFirst(6);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                int count=0;
                TableRow row= new TableRow(getActivity());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row= new TableRow(getActivity());
                lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView tdate = new TextView(getActivity());
                TextView tweight = new TextView(getActivity());
                tdate = new TextView(getActivity());
                tweight = new TextView(getActivity());
                tdate.setText("imie");
                tdate.setPadding(3, 3, 3, 3);
                tweight.setText("średnia");
                tweight.setPadding(3, 3, 3, 3);
                row.addView(tdate);
                row.addView(tweight);
                tl.addView(row,0 );
                count=1;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String name =    String.valueOf(ds.child("name").getValue() );
                    String srednia =  String.valueOf(ds.child("srednia").getValue());
                    String dystans =  String.valueOf(ds.child("dystans").getValue());

                    row= new TableRow(getActivity());
                    lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);
                    tdate = new TextView(getActivity());
                    tweight = new TextView(getActivity());
                    System.out.println("listname"+listofname.get(0));

                    row= new TableRow(getActivity());
                    lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

                    if(listofname.get(0).equals(name))
                    {
                        nicksrednia.setTypeface(null, Typeface.BOLD);
                        nicksrednia.setText("Twoja Srednia"+" "+avgcheck);
                        dystanscheckpoint.setText("Dystans "+dystans);
                    }
                    if(listoffriendname.get(0).equals(name))
                    {
                        nickfriend.setTypeface(null, Typeface.BOLD);
                        nickfriend.setText("Srednia "+listoffriendname.get(0)+" "+srednia);
                    }
                    tdate.setText(name);
                    tdate.setPadding(3, 3, 3, 3);
                    tweight.setText(srednia);
                    tweight.setPadding(3, 3, 3, 3);
                    row.addView(tdate);
                    row.addView(tweight);
                    tl.addView(row,1);

                }
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
        System.out.println("w srodku"+isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) latLngPolygon));
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
        nicksrednia.setVisibility(View.VISIBLE);
        nickfriend.setVisibility(View.VISIBLE);
        labeldistance.setVisibility(View.INVISIBLE);
        dystanscheckpoint.setVisibility(View.VISIBLE);
        kalorie.setVisibility(View.INVISIBLE);
        labelczas.setVisibility(View.INVISIBLE);
        tl.setVisibility(View.VISIBLE);
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
        nicksrednia.setVisibility(View.INVISIBLE);
        nickfriend.setVisibility(View.INVISIBLE);
        dystanscheckpoint.setVisibility(View.INVISIBLE);
        tl.setVisibility(View.INVISIBLE);
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
