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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FragmentRoad extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener{
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location flocation;
    private FragmentRoadListener listener;
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


    public interface FragmentRoadListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();


        final View root = inflater.inflate(R.layout.fragment_road, container, false);

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
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(50.03,21.9987751), new LatLng(50.0227154,21.9997975), new LatLng(50.002724,21.9927758), new LatLng(50.022724,21.9527758))
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        ArrayList<LatLng> coordList = new ArrayList<LatLng>();
        for(int count=0;count<listofeachtrainingwaypoint.size();count++) {


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
            coordList.add(new LatLng(longtide, latide));

        }

        System.out.println("w srodku"+isPointInPolygon(lastKnownLatLng, (ArrayList<LatLng>) coordList));

        String user_id = mAuth.getCurrentUser().getUid();
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


}
