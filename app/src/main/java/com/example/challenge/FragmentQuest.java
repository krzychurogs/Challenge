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
import androidx.fragment.app.FragmentTransaction;

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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FragmentQuest extends Fragment {
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location flocation;
    private FragmentQuestListener listener;
    Button next,back;
    TextView textdistance,textspeed;
    private LatLng lastKnownLatLng;
    List<String>listofplace=new ArrayList<String>();
    List<String>distancelist=new ArrayList<String>();
    private Polyline gpsTrack;
    ArrayList<Integer> newList = new ArrayList<Integer>();//lista tygodni bez duplikatow
    List<String>speedlist=new ArrayList<String>();
    List<String>listofeachtrainingwaypoint=new ArrayList<>();
    public static int counttrain;
    List<LatLng> points ;
    ArrayList<String> listofdates = new ArrayList<String>();
    List<Integer>counter=new ArrayList<>();
    List<Integer>licznik=new ArrayList<>();
    List<Integer>counterofweek=new ArrayList<>();
    List<String>listofsameweek=new ArrayList<>();
    double sumaofdistance=0.0;

    public interface FragmentQuestListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        final View root = inflater.inflate(R.layout.fragment_fragment_quest, container, false);
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("historia");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> newlist = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String dystans = String.valueOf(ds.child("dystans").getValue());
                    String predkosc = String.valueOf(ds.child("predkosc").getValue());
                    String highspeed = String.valueOf(ds.child("highspeed").getValue());
                    String kalorie = String.valueOf(ds.child("kalorie").getValue());
                    String day = String.valueOf(ds.child("data").child("date").getValue());
                    String month = String.valueOf(ds.child("data").child("month").getValue());
                    String years = String.valueOf(ds.child("data").child("year").getValue());
                    String minutes = String.valueOf(ds.child("data").child("minutes").getValue());
                    String hours = String.valueOf(ds.child("data").child("hours").getValue());
                    String seconds = String.valueOf(ds.child("data").child("seconds").getValue());

                    int yearinint = Integer.parseInt(years);
                    int yean = yearinint + 120;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac

                    int monthinint = Integer.parseInt(month);
                    int mon = monthinint + 1;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac
                    String fulldate = day + "/" + mon + "/" + yean;
                    String elementofday = hours + "/" + minutes + "/" + seconds + "/" + day + "/" + month + "/" + years;

                    listofdates.add(fulldate);
                    distancelist.add(dystans);
                    counter.add(calendar(fulldate));

                }

                Set<Integer> unique = new HashSet<Integer>(counter);
                for (Integer key : unique) {
                    licznik.add(key);
                    counterofweek.add(Collections.frequency(counter, key));
                }
                for (int i = 0; i < distancelist.size(); i++) {

                    if(calendar(listofdates.get(i))== 43)
                    {
                        {
                            sumaofdistance+=Double.valueOf(distancelist.get(i).replace(",","."));
                            System.out.println("43 "+sumaofdistance);
                        }
                    }
                    if(calendar(listofdates.get(i))== 42)
                    {
                        {
                            sumaofdistance+=Double.valueOf(distancelist.get(i).replace(",","."));
                            System.out.println("42 "+sumaofdistance);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);


        return root;

    }



    public int calendar(String fulldate)
    {
        String format = "yyyyMMdd";
        SimpleDateFormat df = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.setTime( convertStringToDate(fulldate));
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return  week;
    }



    public Date convertStringToDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dateInString = null;

        try {

            Date date = formatter.parse(dateString);
            dateInString = date;
            //System.out.println(date);
            //System.out.println(formatter.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateInString;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentQuestListener){
            listener= (FragmentQuestListener) context;

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
