package com.example.challenge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.app.AlertDialog;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.LinearLayout.LayoutParams;
import com.google.firebase.firestore.auth.User;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FragmentQuest extends Fragment {
    DatabaseReference reff,reffname,refquest,reffpkt,refflvl,reffpointsto,refquestday,reffquestdaynumber,reffweekdistnumber,reffweekavgnumber,reffcount;
    private FirebaseAuth mAuth;
    private FragmentQuestListener listener;
    TableLayout tl;
    List<String>distancelist=new ArrayList<String>();
    List<String>averagelist=new ArrayList<>();
    private Polyline gpsTrack;
    ArrayList<Integer> newList = new ArrayList<Integer>();//lista tygodni bez duplikatow
    ArrayList<String> listofdates = new ArrayList<String>();
    List<Integer>counter=new ArrayList<>();
    List<Integer>licznik=new ArrayList<>();
    List<Integer>counterofweek=new ArrayList<>();
    List<String>listofdistancequest=new ArrayList<>();
    List<String>listofdistancequestday=new ArrayList<>();
    List<String>listofavgquestday=new ArrayList<>();
    List<String>listofcounttrainquest=new ArrayList<>();
    List<String>listofaveragequest=new ArrayList<>();
    List<Double>listofmaxdistancefromweek=new ArrayList<>();
    List<Double>listofmaxavgfromweek=new ArrayList<>();
    List<Integer>listofnumberofromweek=new ArrayList<>();
    List<Integer>listofweeksinyear=new ArrayList<>();
    Map<Integer, Double> distanceinweek = new TreeMap<>();
    Map<String, Double> distanceinday = new TreeMap<>();
    ListMultimap<String, Double> m = MultimapBuilder.hashKeys().linkedListValues().build(); //multimap for distance
    ListMultimap<String, Double> multimapavgday = MultimapBuilder.hashKeys().linkedListValues().build(); //multimap for distance
    List<Double>listofmaxdistancefromday=new ArrayList<>();
    List<Double>listofmaxavgfromday=new ArrayList<>();

    Map<Integer, Double> avginweek = new TreeMap<>();

    double sumaofdistanceday=0.0;
    double sumaofdistancefir=0.0;
    double sumaofdistancedw=0.0;
    double finalsumaofdistance=0.0;
    double finaldistanceday=0.0;
    double finalavgday=0.0;
    double finalavgofdistance=0.0;
    int finalnumberoftrain=0;
    double avgofdistancefir=0.0;
    double avgofdistancesec=0.0;
    double max=0;
    LinearLayout linearlayout;
    LinearLayout linearavg;
    LinearLayout linearcount;
    LinearLayout linearvertical;
    LinearLayout lineardayavg;
    LinearLayout lineardaydist;
    TextView questinfo;
    TextView pktinfo;
    TextView lvlinfo;
    TextView pkttoLvl;

    public interface FragmentQuestListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        final View root = inflater.inflate(R.layout.fragment_fragment_quest, container, false);
        LinearLayout layout=new LinearLayout(getActivity());

        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("historia");
        linearlayout= (LinearLayout)  root.findViewById(R.id.linearforcheck);
        linearavg= (LinearLayout)  root.findViewById(R.id.linearforavg);
        linearcount= (LinearLayout)  root.findViewById(R.id.linearforcount);
        linearvertical=(LinearLayout)  root.findViewById(R.id.linearquest);
        lineardayavg= (LinearLayout)    root.findViewById(R.id.linearfordayavg);
        lineardaydist= (LinearLayout)   root.findViewById(R.id.linearfordaydist);
        pktinfo= (TextView)   root.findViewById(R.id.textViewPkt);
        lvlinfo= (TextView)   root.findViewById(R.id.lvl);
        pkttoLvl= (TextView)   root.findViewById(R.id.textViewPktLvl);


        /*DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("lvl");
        pkt.setValue("niski lvl");
*/

        refflvl = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("lvl");

        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lvlinfo = dataSnapshot.getValue(String.class);
                setTextLvl(lvlinfo);
                showQuestDay(lvlinfo);
                showQuestForLvl(lvlinfo);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        refflvl.addValueEventListener(postListener1);

        return root;

    }

public void stats()
{
    String user_id = mAuth.getCurrentUser().getUid();
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
                averagelist.add(predkosc);
                counter.add(calendar(fulldate));
                m.put(fulldate,Double.valueOf(dystans.replace(",",".")));
                multimapavgday.put(fulldate,Double.valueOf(predkosc.replace(",",".")));
            }
            Set<Integer> unique = new HashSet<Integer>(counter);
            for (Integer key : unique) {
                licznik.add(key);
                counterofweek.add(Collections.frequency(counter, key));
            }
            int counteroffirweek=0;


            /*
             * count avg from each day
             * */
            Set<String> keys1 = m.keySet();
            for (String keyprint : keys1) {
                // System.out.println("Key = " + keyprint);
                Collection<Double> values = multimapavgday.get(keyprint);
                double simplydayavg=0.0;
                int counter=0;
                for(Double value : values){
                    simplydayavg+=value;
                    counter+=1;
                    // System.out.println("Value= "+ value);
                }
                listofmaxavgfromday.add(simplydayavg/counter);
                simplydayavg=0.0;
                counter=0;
            }

            /*
            * count distance from each day
            * */
            Set<String> keys = m.keySet();
            for (String keyprint : keys) {
               // System.out.println("Key = " + keyprint);
                Collection<Double> values = m.get(keyprint);
                double simplyday=0.0;
                for(Double value : values){
                    simplyday+=value;
                   // System.out.println("Value= "+ value);
                }
                listofmaxdistancefromday.add(simplyday);
                simplyday=0.0;
            }

                for (int i = 0; i < licznik.size(); i++) {

                for(int j=0;j<distancelist.size();j++)
                {
                    if(calendar(listofdates.get(j))== licznik.get(i))
                    {
                        sumaofdistancefir+=Double.valueOf(distancelist.get(j).replace(",","."));
                        avgofdistancefir+=Double.valueOf(averagelist.get(j).replace(",","."));
                        counteroffirweek+=1;
                        distanceinweek.put(licznik.get(i),sumaofdistancefir);
                        listofnumberofromweek.add(counteroffirweek);
                    }
                    if(counteroffirweek !=0)
                    {
                        double maxavg=avgofdistancefir/counteroffirweek;
                        listofmaxavgfromweek.add(maxavg);
                    }
                }

                counteroffirweek=0;
                avgofdistancefir=0;
                sumaofdistancefir=0;
            }

            Set<Integer> keySet = distanceinweek.keySet();
          //  System.out.println("Klucze:\n" + keySet);
            Collection<Double> values = distanceinweek.values();
           // System.out.println("Wartości:\n" + values);

            try {
               max = Collections.max(distanceinweek.values());
            }
            catch (Exception e)
            {

            }

            double finaldist=0; //maksymalny dystans w tygodniu
            Set<Map.Entry<Integer,Double>> entrySet = distanceinweek.entrySet();
            for(Map.Entry<Integer, Double> entry: entrySet) {
                if (entry.getValue()==max) {
                    finaldist=entry.getValue();
                }
              //  System.out.println(entry.getKey() + " : " + entry.getValue());
            }
          //  System.out.println("Wynik:\n" + finaldist);
            final double finaldistweek=finaldist;
            try {
                finalavgofdistance=Collections.max(listofmaxavgfromweek);//maksymalna srednia tygodniowa z listy
                finalnumberoftrain=Collections.max(listofnumberofromweek);// maksymalna liczba treningow w tygodniu
                finaldistanceday=Collections.max(listofmaxdistancefromday);
                finalavgday=Collections.max(listofmaxavgfromday);

            }
            catch (Exception e)
            {

            }




            final TextView distancequest = new TextView(getActivity());
            final TextView avgquest = new TextView(getActivity());
           final TextView counttrainquest = new TextView(getActivity());
            final TextView daydistancequest=new TextView(getActivity());
            final TextView dayavgquest=new TextView(getActivity());
            CheckBox checkdayavg=new CheckBox((getActivity()));
            CheckBox checkdaydist=new CheckBox((getActivity()));
            CheckBox checkdistance=new CheckBox((getActivity()));
            CheckBox checkavg=new CheckBox((getActivity()));
            CheckBox checkcounttrain=new CheckBox((getActivity()));
            final LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(10, 40, 10, 40);


            LayoutParams layoutParamscheck = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(10, 10, 10, 10  ); // (left, top, right, bottom)
            final  String user_id = mAuth.getCurrentUser().getUid();

           /* DatabaseReference nrweek= FirebaseDatabase.getInstance().getReference().child("Users").child
                    ("Customers").child("Historia").child(user_id).child("nrTygodniowyCount");
            nrweek.setValue(0);
            */
       reffweekdistnumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowegoDist");
            reffweekdistnumber.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                        int pktinfo = dataSnapshot.getValue(Integer.class);
                        if(finaldistweek>Double.valueOf(listofdistancequest.get(pktinfo)))
                        {
                            if(pktinfo!=2)
                            {
                                pktinfo++;
                            }

                            DatabaseReference nrweek= FirebaseDatabase.getInstance().getReference().child("Users").child
                                    ("Customers").child("Historia").child(user_id).child("nrTygodniowegoDist");
                            nrweek.setValue(pktinfo);

                            reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

                            reffpkt.addListenerForSingleValueEvent(new ValueEventListener() {
                                boolean processDone = false;

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && !processDone) {
                                        int pktinfo = dataSnapshot.getValue(Integer.class);
                                        System.out.println(pktinfo);
                                        addpoints(pktinfo,numberofQuestDay);

                                    } else {
                                        // do process 2
                                        processDone = true;
                                    }
                                }

                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });

                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Punkty zostały dodane za pokonany najlepszy dystans w tygodniu, następny quest poniżej");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        distancequest.setLayoutParams(layoutParams);
                        distancequest.setText(numberofQuestDay+"- Dystans w tygodniu: "+listofdistancequest.get(numberofQuestDay));
                        distancequest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                    }
                    else
                    {
                        distancequest.setLayoutParams(layoutParams);
                        distancequest.setText(0+"- Dystans w tygodniu: "+listofdistancequest.get(0));
                        distancequest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    }

                }

                @Override public void onCancelled(DatabaseError databaseError) {}
            });

            reffweekavgnumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowegoAvg");
            reffweekavgnumber.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                        int pktinfo = dataSnapshot.getValue(Integer.class);
                        System.out.println("weekquest"+finalavgofdistance);
                        System.out.println("weeklist"+Double.valueOf(listofaveragequest.get(pktinfo)));
                        if(finalavgofdistance>Double.valueOf(listofaveragequest.get(pktinfo)))
                        {
                            if(pktinfo!=2)
                            {
                                pktinfo++;
                            }

                            DatabaseReference nrweek= FirebaseDatabase.getInstance().getReference().child("Users").child
                                    ("Customers").child("Historia").child(user_id).child("nrTygodniowegoAvg");
                            nrweek.setValue(pktinfo);

                            reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

                            reffpkt.addListenerForSingleValueEvent(new ValueEventListener() {
                                boolean processDone = false;

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && !processDone) {
                                        int pktinfo = dataSnapshot.getValue(Integer.class);
                                        System.out.println(pktinfo);
                                        addpoints(pktinfo,numberofQuestDay);
                                    } else {
                                        // do process 2
                                        processDone = true;
                                    }
                                }

                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });

                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Punkty zostały dodane za uzyskana najlepsza srednia w tygodniu, następny quest poniżej");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        avgquest.setLayoutParams(layoutParams);
                        avgquest.setText(numberofQuestDay+"- Srednia w tygodniu: "+listofaveragequest.get(numberofQuestDay));
                        avgquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);




                    }
                    else {
                        avgquest.setLayoutParams(layoutParams);
                        avgquest.setText(0+"- Srednia w tygodniu: "+listofaveragequest.get(0));
                        avgquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    }

                }

                @Override public void onCancelled(DatabaseError databaseError) {}
            });


          // hex color 0xAARRGGBB

            reffcount = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowyCount");
            reffcount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                        int pktinfo = dataSnapshot.getValue(Integer.class);
                        if(finalnumberoftrain>Double.valueOf(listofcounttrainquest.get(pktinfo)))
                        {
                            if(pktinfo!=2)
                            {
                                pktinfo++;
                            }

                            DatabaseReference nrweek= FirebaseDatabase.getInstance().getReference().child("Users").child
                                    ("Customers").child("Historia").child(user_id).child("nrTygodniowyCount");
                            nrweek.setValue(pktinfo);

                            reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

                            reffpkt.addListenerForSingleValueEvent(new ValueEventListener() {
                                boolean processDone = false;

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && !processDone) {
                                        int pktinfo = dataSnapshot.getValue(Integer.class);
                                        System.out.println(pktinfo);
                                        addpointsCount(pktinfo,numberofQuestDay);
                                    } else {
                                        // do process 2
                                        processDone = true;
                                    }
                                }

                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });

                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Punkty został dodane za liczbe treningow, następny quest ponizej");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        counttrainquest.setLayoutParams(layoutParams);
                        counttrainquest.setText(numberofQuestDay+"- Liczba treningów w tygodniu: "+listofcounttrainquest.get(numberofQuestDay));
                        counttrainquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    }
                    else
                    {
                        counttrainquest.setLayoutParams(layoutParams);
                        counttrainquest.setText(0+"- Liczba treningów w tygodniu: "+listofcounttrainquest.get(0));
                        counttrainquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    }

                }

                @Override public void onCancelled(DatabaseError databaseError) {}
            });


            DecimalFormat dfsuma = new DecimalFormat("#.#");

            checkdistance.setLayoutParams(layoutParamscheck);
            checkdistance.setText(dfsuma.format(finaldist)+"m");
            checkdistance.setTextSize(16);


            checkavg.setLayoutParams(layoutParamscheck);
            checkavg.setText(dfsuma.format(finalavgofdistance)+"km/h");
            checkavg.setTextSize(16);


            checkcounttrain.setLayoutParams(layoutParamscheck);
            checkcounttrain.setText(String.valueOf(finalnumberoftrain));
            checkcounttrain.setTextSize(16);




            reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrDziennego");
            reffquestdaynumber.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                        int pktinfo = dataSnapshot.getValue(Integer.class);
                        //System.out.println("dayquest"+pktinfo);
                        if(finaldistanceday>Double.valueOf(listofdistancequestday.get(pktinfo)))
                        {
                            if(pktinfo!=2) {
                                pktinfo++;
                            }
                            DatabaseReference nrday = FirebaseDatabase.getInstance().getReference().child("Users").child
                                    ("Customers").child("Historia").child(user_id).child("nrDziennego");
                            nrday.setValue(pktinfo);

                            reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

                            reffpkt.addListenerForSingleValueEvent(new ValueEventListener() {
                                boolean processDone = false;

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && !processDone) {
                                        int pktinfo = dataSnapshot.getValue(Integer.class);
                                        System.out.println(pktinfo);
                                        addpoints(pktinfo,numberofQuestDay);
                                    } else {
                                        // do process 2
                                        processDone = true;
                                    }
                                }

                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });

                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Punkty zostały dodane za uzyskana najlepszy dystans w dniu, następny quest poniżej");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        daydistancequest.setLayoutParams(layoutParams);
                        daydistancequest.setText(numberofQuestDay+"- Dystans w dniu: "+listofdistancequestday.get(numberofQuestDay));
                        daydistancequest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    } else {
                        daydistancequest.setLayoutParams(layoutParams);
                        daydistancequest.setText(0+"- Dystans w dniu: "+listofdistancequestday.get(0));
                        daydistancequest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    }

                }

                @Override public void onCancelled(DatabaseError databaseError) {}
            });



            reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrDziennegoAvg");
            reffquestdaynumber.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                        System.out.println("inf"+pktinfo);
                        int pktinfoAvg = dataSnapshot.getValue(Integer.class);
                        System.out.println("poziom"+pktinfoAvg);



                        if(finalavgday>Double.valueOf(listofavgquestday.get(pktinfoAvg)))
                        {

                            if(pktinfoAvg!=2)
                            {
                                pktinfoAvg++;
                            }

                            DatabaseReference nravgday = FirebaseDatabase.getInstance().getReference().child("Users").child
                                    ("Customers").child("Historia").child(user_id).child("nrDziennegoAvg");
                            nravgday.setValue(pktinfoAvg);

                            reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

                            reffpkt.addListenerForSingleValueEvent(new ValueEventListener() {
                                boolean processDone = false;

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && !processDone) {
                                        int pktinfo = dataSnapshot.getValue(Integer.class);
                                        System.out.println(pktinfo);
                                        addpoints(pktinfo,numberofQuestDay);
                                    } else {
                                        // do process 2
                                        processDone = true;
                                    }
                                }

                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });

                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Punkty zostały dodane za uzyskana najlepszy średnia  w dniu, następny quest poniżej");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }

                        dayavgquest.setLayoutParams(layoutParams);
                        dayavgquest.setText(numberofQuestDay+"- Srednia w dniu: "+listofavgquestday.get(numberofQuestDay));
                        dayavgquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    } else {
                        dayavgquest.setLayoutParams(layoutParams);
                        dayavgquest.setText(0+"- Srednia w dniu: "+listofavgquestday.get(0));
                        dayavgquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    }

                }

                @Override public void onCancelled(DatabaseError databaseError) {}
            });


            checkdayavg.setLayoutParams(layoutParamscheck);
            checkdayavg.setText(dfsuma.format(finalavgday)+"km/h");
            checkdayavg.setTextSize(16);

            checkdaydist.setLayoutParams(layoutParamscheck);
            checkdaydist.setText(dfsuma.format(finaldistanceday)+"m.");
            checkdaydist.setTextSize(16);

            ; // hex color 0xAARRGGBB
            linearlayout.setOrientation(LinearLayout.HORIZONTAL);
            linearlayout.addView(distancequest);
            linearlayout.addView(checkdistance);
            linearcount.addView(counttrainquest);
            linearcount.setOrientation(LinearLayout.HORIZONTAL);
            linearcount.addView(checkcounttrain);
            linearavg.setOrientation(LinearLayout.HORIZONTAL);
            linearavg.addView(avgquest);
            linearavg.addView(checkavg);

            lineardayavg.setOrientation(LinearLayout.HORIZONTAL);
            lineardayavg.addView(dayavgquest);
            lineardayavg.addView(checkdayavg);
            lineardaydist.addView(daydistancequest);
            lineardaydist.addView(checkdaydist);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    reff.addListenerForSingleValueEvent(valueEventListener);
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
    public void showQuestForLvl(String lvl)
    {

        System.out.println("lev"+lvl);

        reffpointsto = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Questy").child(lvl);

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lvlinfo = String.valueOf(dataSnapshot.child("maxpunkty").getValue());
             //   System.out.println("pkt na lvl"+lvlinfo);
                showPoints(lvlinfo);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        reffpointsto.addValueEventListener(postListener2);
        refquest=FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Questy").child(lvl).child("tygodniowe");

        refquest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstlvldist=String.valueOf(dataSnapshot.child("dystans").child("1lvl").getValue());
               // System.out.println(firstlvldist);
                String secondlvldist=String.valueOf(dataSnapshot.child("dystans").child("2lvl").getValue());
                String thirdlvldist=String.valueOf(dataSnapshot.child("dystans").child("3lvl").getValue());
                String firstlvlsred=String.valueOf(dataSnapshot.child("srednia").child("1lvl").getValue());
                String secondlvlsred=String.valueOf(dataSnapshot.child("srednia").child("2lvl").getValue());
                String thirdlvlsred=String.valueOf(dataSnapshot.child("srednia").child("3lvl").getValue());
                String firstlvlcountt=String.valueOf(dataSnapshot.child("liczbatreningow").child("1lvl").getValue());
                String secondlvlcountt=String.valueOf(dataSnapshot.child("liczbatreningow").child("2lvl").getValue());
                String thirdlvlcountt=String.valueOf(dataSnapshot.child("liczbatreningow").child("3lvl").getValue());
                listofdistancequest.add(firstlvldist);
                listofdistancequest.add(secondlvldist);
                listofdistancequest.add(thirdlvldist);
                listofaveragequest.add(firstlvlsred);
                listofaveragequest.add(secondlvlsred);
                listofaveragequest.add(thirdlvlsred);
                listofcounttrainquest.add(firstlvlcountt);
                listofcounttrainquest.add(secondlvlcountt);
                listofcounttrainquest.add(thirdlvlcountt);
                for(int i=0;i<53;i++)
                {
                    listofweeksinyear.add(i);
                }
                stats();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void showPoints(final String pointstolvl)
    {
        String user_id = mAuth.getCurrentUser().getUid();
        reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    int pktinfo = dataSnapshot.getValue(Integer.class);
                    String pktint=String.valueOf(pktinfo);
                    // System.out.println("poin"+pointstolvl);
                    //  System.out.println(pktint);
                    int max=Integer.parseInt(pointstolvl);
                    int diff=max-pktinfo;
                    pkttoLvl.setText("Punkty do nastepnęgo lvl:"+String.valueOf(diff));
                    setTextPkt(pktint);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        reffpkt.addValueEventListener(postListener);
    }
    public void pointsToLvl()
    {

    }


public void setTextPkt(String value)
{
    String user_id = mAuth.getCurrentUser().getUid();
    pktinfo.setText("Twoje punkty: "+value);

    if(Integer.valueOf(value)<2000)
    {
        DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                (user_id).child("lvl");
        pkt.setValue("Easy");
    }

    if(Integer.valueOf(value)>2000)
    {
        DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                (user_id).child("lvl");
        pkt.setValue("Medium");
    }
    if(Integer.valueOf(value)>4000)
    {
        DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                (user_id).child("lvl");
        pkt.setValue("High");
    }

}
public void setTextLvl(String value)
    {
        lvlinfo.setText("Level: "+value);

    }
public void showQuestDay(String lvl)
{
    refquestday=FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Questy").child(lvl).child("dzienne");

    refquestday.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String firstlvldist=String.valueOf(dataSnapshot.child("dystans").child("1lvl").getValue());
          //  System.out.println(firstlvldist);
            String secondlvldist=String.valueOf(dataSnapshot.child("dystans").child("2lvl").getValue());
            String thirdlvldist=String.valueOf(dataSnapshot.child("dystans").child("3lvl").getValue());
            String firstlvlsred=String.valueOf(dataSnapshot.child("srednia").child("1lvl").getValue());
            String secondlvlsred=String.valueOf(dataSnapshot.child("srednia").child("2lvl").getValue());
            String thirdlvlsred=String.valueOf(dataSnapshot.child("srednia").child("3lvl").getValue());
            listofdistancequestday.add(firstlvldist);
            listofdistancequestday.add(secondlvldist);
            listofdistancequestday.add(thirdlvldist);
            listofavgquestday.add(firstlvlsred);
            listofavgquestday.add(secondlvlsred);
            listofavgquestday.add(thirdlvlsred);


        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
    public void addpoints(int pktinfo,int number)
    {
        final  String user_id = mAuth.getCurrentUser().getUid();
        int add=0;
        if(number == 0 )
        {
            add=300;
        }
        if(number ==1 )
        {
            add=400;
        }
        if(number ==2 )
        {
            add=500;
        }

        final int score=pktinfo+add;
        String pktint=String.valueOf(score);
        System.out.println(score);
        DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("punkty");
        pkt.setValue(score);

    }
    public void addpointsCount(int pktinfo,int number)
    {
        final  String user_id = mAuth.getCurrentUser().getUid();
        int add=0;
        if(number == 0 )
        {
            add=20;
        }
        if(number ==1 )
        {
            add=30;
        }
        if(number ==2 )
        {
            add=35;
        }

        final int score=pktinfo+add;
        String pktint=String.valueOf(score);
        System.out.println(score);
        DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("punkty");
        pkt.setValue(score);
    }

}
