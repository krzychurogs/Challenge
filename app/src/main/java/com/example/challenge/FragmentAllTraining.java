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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.tokopedia.expandable.ExpandableOptionRadio;

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
import java.util.Random;
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
    ArrayList<String> listofdateswithoutduplicates = new ArrayList<String>();//lista tygodni bez duplikatow
    ArrayList<String> listofdates = new ArrayList<String>();
    ArrayList<String> elementofdates = new ArrayList<String>();
    int counterofweekwithoutduplicates=0;
    double sumaofdistance=0.0;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TrainAdapter mainRecyclerAdapter;

    public interface FragmentAllTrainingListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        final View root = inflater.inflate(R.layout.fragment_fragment_all_training, container, false);
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
        ArrayList<ExampleItem>exampleList=new ArrayList<>();
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
            System.out.println(start);
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            end = dt.format(calendar.getTime());
            exampleList.add(new ExampleItem(R.drawable.ic_circle_50dp,start+"-",end));

        }

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ExampleAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }
    private  View.OnClickListener getOnClick(final int i)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int max=newList.size()-1;
                lookfordateindatabase(newList.get(i),i,max);
            }
        };
    }
    public static boolean isDateInCurrentWeek(Date date,int week) {
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

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reff.addListenerForSingleValueEvent(valueEventListener);

        int ctr=0;
        TextView mTextView;
        for(int i=0;i<listofdates.size();i++)
        {
            if(isDateInCurrentWeek(convertStringToDate(listofdates.get(i)),nrofweek)==true) {
                ctr += 1;
                // System.out.println(ctr);
                Button button = new Button(getActivity());
                button.setLayoutParams(new LinearLayout.LayoutParams(600, 150));
                button.setId(numberoflin + 1);
                int max = numberoflin + 1;
                final String[] partshour = elementofdates.get(i).split("/");

                button.setText(partshour[0]+":"+partshour[1]+" "+partshour[3]+"/"+partshour[4]+"/"+partshour[5]);
                button.setFocusableInTouchMode(false);
                final int x=i;
                if (maksfork == numberoflin) {
                    linearlayout.addView(button, numberoflin + 1);

                }
                else {
                    linearlayout.addView(button, numberoflin + max);
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle=new Bundle();
                        bundle.putString("hour",elementofdates.get(x))  ;
                        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                        FragmentHistory fragmentHistory=new FragmentHistory();
                        fragmentHistory.setArguments(bundle);
                        fragmentTransaction.replace(R.id.drawer_layout,fragmentHistory);
                        fragmentTransaction.commit();
                    }
                });

            }
        }
        listofdates.clear();
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