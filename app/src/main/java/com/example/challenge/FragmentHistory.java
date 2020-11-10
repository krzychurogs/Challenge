package com.example.challenge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    Button backtoall;
    TextView textdistance,textspeed,texthighspeed,textkalorie,datahisttext;
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
        Bundle bundle=getArguments();

        final View root = inflater.inflate(R.layout.fragment_history, container, false);
        String fullhour=bundle.getString("hour");
        System.out.println("datatren"+fullhour);
        final String[] partshour = fullhour.split("/");
        ImageView imageView1 = (ImageView) root.findViewById(R.id.imageView4);
        textdistance= (TextView)  root.findViewById(R.id.textDist);
        textspeed= (TextView)  root.findViewById(R.id.textSpeed);
        textkalorie= (TextView)  root.findViewById(R.id.textKalorie);
        texthighspeed= (TextView)  root.findViewById(R.id.textHighSpeed);
        datahisttext= (TextView)  root.findViewById(R.id.datatraintext);
        backtoall=(Button)root.findViewById(R.id.backtoall);


        final MainActivity count=(MainActivity)getActivity();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("historia");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String dystans =  String.valueOf(ds.child("dystans").getValue());
                    String predkosc = String.valueOf(ds.child("predkosc").getValue());
                    String highspeed =String.valueOf(ds.child("highspeed").getValue());
                    String kalorie = String.valueOf(ds.child("kalorie").getValue());
                    String day = String.valueOf(ds.child("data").child("date").getValue());
                    String month = String.valueOf(ds.child("data").child("month").getValue());
                    String years = String.valueOf(ds.child("data").child("year").getValue());
                    String minutes= String.valueOf(ds.child("data").child("minutes").getValue());
                    String hours= String.valueOf(ds.child("data").child("hours").getValue());
                    String seconds= String.valueOf(ds.child("data").child("seconds").getValue());
                    int yearinint=Integer.parseInt(years);
                    int yean=yearinint+1900;
                    if(partshour[0].equals(hours)&&partshour[1].equals(minutes)&&partshour[2].equals(seconds)&&
                            partshour[3].equals(day)&&partshour[4].equals(month)&&partshour[5].equals(years))
                    {
                        DecimalFormat dfsuma = new DecimalFormat("#.##");
                        textdistance.setText(dystans+"m");
                        textspeed.setText(predkosc+ "km/h") ;
                        texthighspeed.setText(highspeed+"km/h");
                        textkalorie.setText(kalorie);
                        datahisttext.setText(day+"/"+month+"/"+yean+" "+hours+":"+minutes);
                        String data=ds.child("waypointy").getValue().toString();

                        listofeachtrainingwaypoint.add(data);
                    }
                }

                pointsfromtrain();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);


        return root;

    }
    public void pointsfromtrain()
    {


        List<String>newlist=new ArrayList<String>();
        String word[]=listofeachtrainingwaypoint.get(0).split(" ");
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
        List<LatLng> coordList = new ArrayList<LatLng>();

        Double latide= Double.valueOf(listofplace.get(1));
        Double longtide=  Double.valueOf(listofplace.get(0));

        for(int i=0;i<listofplace.size();i++)
        {
            longtide=Double.valueOf(listofplace.get(i));

            if(listofplace.size() > i + 1){
                latide = Double.valueOf(listofplace.get(++i).toString().replace("]","")); //Change here
            }
            lastKnownLatLng= new LatLng(longtide, latide);

            coordList.add(lastKnownLatLng);
        }


        PolylineOptions options = new PolylineOptions()
                .color(Color.BLUE)
                .geodesic(true)
                .width(5)
                .addAll(coordList);

        Polyline pfad = mMap.addPolyline(options);


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



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000000);
        mLocationRequest.setFastestInterval(100000000);
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

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));




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