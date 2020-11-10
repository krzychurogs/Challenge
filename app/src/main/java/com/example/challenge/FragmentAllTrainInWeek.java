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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FragmentAllTrainInWeek extends Fragment {
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location flocation;
    ArrayList<String> listofdates = new ArrayList<String>();
    ArrayList<String> elementofdates = new ArrayList<String>();
    ArrayList<String> elementofdatesinweek = new ArrayList<String>();
    private FragmentAllTrainInWeekListener listener;
    Button next,back;
    TextView textdistance,textspeed;
    private LatLng lastKnownLatLng;
    List<String>listofplace=new ArrayList<String>();
    List<String>distancelist=new ArrayList<String>();
    private Polyline gpsTrack;
    List<String>roadlists=new ArrayList<String>();
    List<String>listofeachtrainingwaypoint=new ArrayList<>();
    public static int counttrain;
    List<LatLng> points ;
    private RecyclerView mRecyclerView;
    List<Integer>counter=new ArrayList<>();
    private AdapterTrainInWeek mAdapter;
    ArrayList<Integer> newList = new ArrayList<Integer>();//lista tygodni bez duplikatow
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<TrainInWeekItem>exampleList=new ArrayList<>();

    double sumaofdistance=0.0;
    public interface FragmentAllTrainInWeekListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();


        final View root = inflater.inflate(R.layout.fragment_fragment_all_train_in_week, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("historia");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String day = String.valueOf(ds.child("data").child("date").getValue());
                    String month = String.valueOf(ds.child("data").child("month").getValue());
                    String years = String.valueOf(ds.child("data").child("year").getValue());
                    int yearinint=Integer.parseInt(years);
                    int yean=yearinint+120;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac

                    int monthinint=Integer.parseInt(month);
                    int mon=monthinint+1;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac
                    String fulldate=day+"/"+mon+"/"+yean;
                    counter.add( calendar(fulldate));

                }
                Bundle bundle=getArguments();
                System.out.println(bundle.getInt("number"));
                fork(bundle.getInt("number"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);




        return root;

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentAllTrainInWeekListener){
            listener= (FragmentAllTrainInWeekListener) context;

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
    public void fork(Integer i)
    {
        for (Integer element : counter) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        int max=newList.size()-1;
        lookfordateindatabase(newList.get(i),i,max);




    }

    public static boolean isDateInCurrentWeek(Date date, int week) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.WEEK_OF_YEAR,week);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        if(week == targetWeek )
        {
            return true;
        }
        return false;


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

    public void lookfordateindatabase(final int nrofweek,int numberoflin,int maksfork)
    {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();

        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("historia");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String day = String.valueOf(ds.child("data").child("date").getValue());
                    String month = String.valueOf(ds.child("data").child("month").getValue());
                    String years = String.valueOf(ds.child("data").child("year").getValue());
                    String minutes= String.valueOf(ds.child("data").child("minutes").getValue());
                    String hours= String.valueOf(ds.child("data").child("hours").getValue());
                    String seconds= String.valueOf(ds.child("data").child("seconds").getValue());
                    String dystans = String.valueOf(ds.child("dystans").getValue());
                    int yearinint=Integer.parseInt(years);
                    int yean=yearinint+120;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac

                    int monthinint=Integer.parseInt(month);
                    int mon=monthinint+1;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac
                    String fulldate=day+"/"+mon+"/"+yean;
                    String elementofday=hours+"/"+minutes+"/"+seconds+"/"+day+"/"+month+"/"+years;
                    listofdates.add(fulldate);
                    elementofdates.add(elementofday);
                    String dystanswithoutdot=dystans.replace(",",".");
                    sumaofdistance+=Double.parseDouble(dystanswithoutdot);


                }
                int ctr=0;
                TextView mTextView;
                for(int i=0;i<listofdates.size();i++)
                {
                    if(isDateInCurrentWeek(convertStringToDate(listofdates.get(i)),nrofweek)==true) {
                        ctr += 1;
                        final String[] partshour = elementofdates.get(i).split("/");

                        System.out.println(partshour[0]+":"+partshour[1]+" "+partshour[3]+"/"+partshour[4]+"/"+partshour[5]);

                      int year=Integer.parseInt(partshour[5])+1900;
                      String nazwa=partshour[0]+":"+partshour[1]+" "+partshour[3]+"/"+partshour[4]+"/"+String.valueOf(year);
                      String test="d";
                      exampleList.add(new TrainInWeekItem(R.drawable.ic_circle_50dp,nazwa,test));
                      elementofdatesinweek.add(elementofdates.get(i));


                    }
                    final int x=i;
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mAdapter = new AdapterTrainInWeek(exampleList);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new AdapterTrainInWeek.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Bundle bundle=new Bundle();
                            bundle.putString("hour",elementofdatesinweek.get(position))  ;
                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                            FragmentHistory fragmentHistory=new FragmentHistory();
                            fragmentHistory.setArguments(bundle);
                            fragmentTransaction.replace(R.id.drawer_layout,fragmentHistory);
                            fragmentTransaction.commit();

                        }
                    });
                }
                listofdates.clear();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reff.addListenerForSingleValueEvent(valueEventListener);


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


}
