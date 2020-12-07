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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.tokopedia.expandable.ExpandableOptionRadio;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FragmentAllTraining extends Fragment {
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private FragmentAllTrainingListener listener;
    List<Double>listofmaxdistancefromday=new ArrayList<>();
    List<String>distancelist=new ArrayList<String>();
    List<Integer>counter=new ArrayList<>();
    ArrayList<Integer> newList = new ArrayList<Integer>();//lista tygodni bez duplikatow
    ArrayList<String> listofdates = new ArrayList<String>();
    ArrayList<String> elementofdates = new ArrayList<String>();
    ArrayList<Integer> listOfForks = new ArrayList<Integer>();
    ArrayList<Integer> listOfForksReverse = new ArrayList<Integer>();
    int counterofweekwithoutduplicates=0;
    ArrayList<Integer> posWeek = new ArrayList<Integer>();
    List<String>averagelist=new ArrayList<>();
    double sumaofdistance=0.0;
    double avgofdistancefir=0.0;
    List<Double>listofmaxavgfromweek=new ArrayList<>();
    List<Integer>licznik=new ArrayList<>();
    private RecyclerView mRecyclerView;
    double sumaofdistancefir=0.0;
    Map<Integer, Double> distanceinweek = new TreeMap<>();
    Map<Integer, Double> avginweek = new TreeMap<>();
    Map<Integer, Integer> counterinweek = new TreeMap<>();
    private ExampleAdapter mAdapter;
    List<Integer>listofnumberofromweek=new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    ListMultimap<String, Double> m = MultimapBuilder.hashKeys().linkedListValues().build(); //multimap for distance
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
                    String predkosc = String.valueOf(ds.child("predkosc").getValue());
                    String years = String.valueOf(ds.child("data").child("year").getValue());
                    String dystans = String.valueOf(ds.child("dystans").getValue());
                    int yearinint=Integer.parseInt(years);
                    int yean=yearinint+120;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac

                    int monthinint=Integer.parseInt(month);
                    int mon=monthinint+1;//iteruje od 0 trzeba dodac 1 aby uzyskac dobry miesiac
                    String fulldate=day+"/"+mon+"/"+yean;
                    counter.add( calendar(fulldate));
                    listofdates.add(fulldate);
                    m.put(fulldate,Double.valueOf(dystans.replace(",",".")));
                    distancelist.add(dystans);
                    averagelist.add(predkosc);
                }
                Set<Integer> unique = new HashSet<Integer>(counter);
                for (Integer key : unique) {
                    licznik.add(key);

                }
                int counteroffirweek=0;


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

        int counteroffirweek=0;
        for (int i = 0; i < licznik.size(); i++) {

            for(int j=0;j<distancelist.size();j++)
            {
                if(calendar(listofdates.get(j))== licznik.get(i))
                {
                    sumaofdistancefir+=Double.valueOf(distancelist.get(j).replace(",","."));
                    avgofdistancefir+=Double.valueOf(averagelist.get(j).replace(",","."));
                    counteroffirweek+=1;
                    distanceinweek.put(licznik.get(i),sumaofdistancefir);
                    counterinweek.put(licznik.get(i),counteroffirweek);

                }
                if(counteroffirweek !=0)
                {
                    double maxavg=avgofdistancefir/counteroffirweek;
                    avginweek.put(licznik.get(i),maxavg);
                }
            }

            counteroffirweek=0;
            avgofdistancefir=0;
            sumaofdistancefir=0;
        }

        int conc=0;
        ArrayList<ExampleItem>exampleList=new ArrayList<>();
        int posi=0;
        for (int i = newList.size() - 1; i >= 0; i--)
        {
            SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
            String start ="";
            String end ="";
            String dist ="";
            String avg ="";
            String counter ="";
            int year = 2020;

            Calendar c = Calendar.getInstance();

            // Set the calendar to monday of the current week
                        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        c.set(Calendar.WEEK_OF_YEAR, newList.get(i));
            // Print dates of the current week starting on Monday
                        DateFormat dfsw = new SimpleDateFormat("dd/MM/yyyy");
                        for (int j = 0; j < 7; j++) {
                            if(j==0)
                            {
                                start=dfsw.format(c.getTime());

                            }
                            if(j==6)
                            {
                                end=dfsw.format(c.getTime());

                            }

                            c.add(Calendar.DATE, 1);
                        }
            String fork=start +"-" +end;
            Set<Map.Entry<Integer,Double>> entrySet = distanceinweek.entrySet();
            DecimalFormat df = new DecimalFormat("#");
            for(Map.Entry<Integer, Double> entry: entrySet) {
                if(entry.getKey() == newList.get(i))
                {
                //    System.out.println(entry.getKey() + " : " + entry.getValue());
                    dist=df.format(entry.getValue());
                }
            }
            Set<Map.Entry<Integer,Double>> entrySetavg = avginweek.entrySet();
            for(Map.Entry<Integer, Double> entry: entrySetavg) {
                if(entry.getKey() == newList.get(i))
                {
                 //   System.out.println(entry.getKey() + " : " + entry.getValue());
                    avg=String.valueOf(entry.getValue());

                }
            }
            Set<Map.Entry<Integer,Integer>> entrySetCounter = counterinweek.entrySet();
            for(Map.Entry<Integer, Integer> entry: entrySetCounter) {
                if(entry.getKey() == newList.get(i))
                {
                  //  System.out.println(entry.getKey() + " : " + entry.getValue());
                    counter=String.valueOf(entry.getValue());
                }
            }

            Double finalavg=Double.valueOf(avg);

            DecimalFormat dfs = new DecimalFormat("#.##");
            String exampleavg=dfs.format(finalavg);
            exampleList.add(new ExampleItem(R.drawable.ic_circle_50dp,R.drawable.ic_location_on_black_52dp,R.drawable.ic_timer_black_52dp,fork,dist+"m",exampleavg+" km/h",counter    ));
            listOfForks.add(posi);

            posi++;

        }



        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ExampleAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        listOfForksReverse=reverseList(listOfForks);
        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                System.out.println(position);
                System.out.println(listOfForksReverse.get(position));
                Bundle bundle=new Bundle();
                bundle.putInt("number",listOfForksReverse.get(position));
                FragmentAllTrainInWeek trainweek=new FragmentAllTrainInWeek();
                trainweek.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,trainweek).commit();
            }
        });
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
    public static<T> ArrayList<T> reverseList(List<T> list)
    {
        ArrayList<T> reverse = new ArrayList<>(list);
        Collections.reverse(reverse);
        return reverse;
    }



}