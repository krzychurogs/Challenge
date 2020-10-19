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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FragmentAllTraining extends Fragment {
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private FragmentAllTrainingListener listener;
    Button[] btnWord = new Button[3];
    LinearLayout linearlayout;
    List<Button> btnList = new ArrayList<>();
    int count=0;
    List<Integer>counter=new ArrayList<>();
    ArrayList<Integer> newList = new ArrayList<Integer>();//lista tygodni bez duplikatow
    ArrayList<String> listofdates = new ArrayList<String>();
    int counterofweekwithoutduplicates=0;


    public interface FragmentAllTrainingListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        final View root = inflater.inflate(R.layout.fragment_fragment_all_training, container, false);
        linearlayout= (LinearLayout)  root.findViewById(R.id.linearl);
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
                fork();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);


        return root;

    }
    public void fork()
    {
        for (Integer element : counter) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        int conc=0;
        for (int i=0;i<newList.size();i++)
        {
            SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
            String start ="";
            String end ="";
            int year = 2020;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            calendar.set(Calendar.WEEK_OF_YEAR, newList.get(i));
            start = dt.format(calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            end = dt.format(calendar.getTime());
            Button button=new Button(getActivity());
            button.setLayoutParams(new LinearLayout.LayoutParams(600    ,150));
            button.setId(count);
            button.setText(start+" |"+end);
            final int counter=i;

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lookfordateindatabase(newList.get(counter));


                }
            });
            conc+=1;
            linearlayout.addView(button);
            if(newList.size()==conc)
            {
                break;
            }

        }
    }
    public static boolean isDateInCurrentWeek(Date date,int week) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.WEEK_OF_YEAR,week);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        System.out.println("week"+week);
        System.out.println("targetweek"+targetWeek);
        return week == targetWeek && year == targetYear;
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

    public void lookfordateindatabase(final int nrofweek)
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
                    int yearinint=Integer.parseInt(years);
                    int yean=yearinint+120;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac

                    int monthinint=Integer.parseInt(month);
                    int mon=monthinint+1;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac
                    String fulldate=day+"/"+mon+"/"+yean;
                    listofdates.add(fulldate);

                        // System.out.println(isDateInCurrentWeek(convertStringToDate(listofdates.get(i)),nrofweek));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);
        for(int i=0;i<listofdates.size();i++)
        {
            System.out.println(listofdates.get(i));

        }

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
        if(context instanceof FragmentAllTrainingListener){
            listener= (FragmentAllTrainingListener) context;

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
