package com.example.challenge;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.model.Polyline;
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
    DatabaseReference reff,reffname,refquest,reffpkt,refflvl,reffpointsto,refquestday
            ,reffquestdaynumber,reffweekdistnumber,reffweekavgnumber,reffcount,refcheckavg,reffcheckcount;
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
    boolean onceT=false;
    boolean onceAvg=false;
    boolean onceDistWeek=false;
    boolean onceCountWeek=false;
    boolean checktask=false;
    boolean checktaskAvg=false;
    boolean checkWeekTaskAvg=false;
    boolean checkWeekTaskDist=false;
    boolean checkWeekTaskCount=false;
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
    TextView questinfo;
    TextView pktinfoT;
    TextView lvlinfo;
    TextView pkttoLvl;
    TextView distancequest,avgquest,counttrainquest,daydistancequest,dayavgquest;
    public AlertDialog alertDialog;
    TextView checkdistanceweek,checkdistanceavg,checkdistanceCount,checkdaydistanceday,checkavgday;
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

        alertDialog = new AlertDialog.Builder(getContext()).create();

        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("historia");

        pktinfoT= (TextView)   root.findViewById(R.id.textViewPkt);
        lvlinfo= (TextView)   root.findViewById(R.id.lvl);
        pkttoLvl= (TextView)   root.findViewById(R.id.textViewPktLvl);
        distancequest= (TextView)   root.findViewById(R.id.textViewDist);
        avgquest= (TextView)   root.findViewById(R.id.textViewAvg);
        counttrainquest= (TextView)   root.findViewById(R.id.textViewCount);
        dayavgquest=(TextView)   root.findViewById(R.id.textViewDayAvg);
        daydistancequest= (TextView)   root.findViewById(R.id.textViewDayDist);
        checkdistanceweek= (TextView)   root.findViewById(R.id.checkweekDist);
        checkdistanceavg= (TextView)   root.findViewById(R.id.checkweekavg);
        checkdistanceCount= (TextView)   root.findViewById(R.id.checkweekCount);
        checkdaydistanceday= (TextView)   root.findViewById(R.id.checkdayDist);
        checkavgday= (TextView)   root.findViewById(R.id.checkdayAvg);

        /*DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("lvl");
        pkt.setValue("niski lvl");
*/
        maxTask();
        refflvl = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("lvl");

        refflvl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String lvlinfo = dataSnapshot.getValue(String.class);
                setTextLvl(lvlinfo);
                showQuestDay(lvlinfo);
                showQuestForLvl(lvlinfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;

    }

    public void stats()

    {
        finalavgofdistance=0.0;
        finalavgday=0.0;
        finaldistanceday=0.0;
        String user_id = mAuth.getCurrentUser().getUid();
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("historia");
        ValueEventListener valueEventListener = new ValueEventListener(){
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
                if(onceAvg==false)
                {
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
                    onceAvg=true;
                }
                for(int i=0;i<listofmaxavgfromday.size();i++)
                {
                    System.out.println("list "+ i+" "+listofmaxavgfromday.get(i));
                }



                /*
                 * count distance from each day
                 * */

                Set<String> keys = m.keySet();
                if(onceT==false)
                {
                    for (String keyprint : keys) {
                        // System.out.println("Key = " + keyprint);
                        Collection<Double> values = m.get(keyprint);
                        double simplyday=0.0;
                        for(Double value : values){
                            simplyday+=value;
                            System.out.println("Value= "+ value);
                        }
                        listofmaxdistancefromday.add(simplyday);
                        simplyday=0.0;
                    }
                    onceT=true;
                }

                if(onceCountWeek==false)
                {
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
                            if(counteroffirweek !=0 && counteroffirweek >1)
                            {
                                double maxavg=avgofdistancefir/counteroffirweek;
                                listofmaxavgfromweek.add(maxavg);
                            }
                        }

                        counteroffirweek=0;
                        avgofdistancefir=0;
                        sumaofdistancefir=0;
                    }
                    onceCountWeek=true;
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
                if(onceDistWeek==false)
                {
                    Set<Map.Entry<Integer,Double>> entrySet = distanceinweek.entrySet();
                    for(Map.Entry<Integer, Double> entry: entrySet) {
                        if (entry.getValue()==max) {
                            finaldist=entry.getValue();
                        }
                        //  System.out.println(entry.getKey() + " : " + entry.getValue());
                    }
                    onceDistWeek=true;
                }

                //  System.out.println("Wynik:\n" + finaldist);
                final double finaldistweek=finaldist;
                try {
                    finalavgofdistance=Collections.max(listofmaxavgfromweek);//maksymalna srednia tygodniowa z listy
                    finalnumberoftrain=Collections.max(listofnumberofromweek);// maksymalna liczba treningow w tygodniu
                    finaldistanceday=Collections.max(listofmaxdistancefromday);


                    finalavgday=Collections.max(listofmaxavgfromday);
                    DecimalFormat dfsuma = new DecimalFormat("#.#");
                    listofmaxdistancefromday.clear();


                    checkdaydistanceday.setText(dfsuma.format(finaldistanceday)+"m.");
                    checkavgday.setText(dfsuma.format(finalavgday)+"km/h");

                }
                catch (Exception e)
                {

                }
                final  String user_id = mAuth.getCurrentUser().getUid();

           /* DatabaseReference nrweek= FirebaseDatabase.getInstance().getReference().child("Users").child
                    ("Customers").child("Historia").child(user_id).child("nrTygodniowyCount");
            nrweek.setValue(0);
            */
                final DecimalFormat dff = new DecimalFormat("#");
                reffweekdistnumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowegoDist");
                reffweekdistnumber.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {


                            final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                            int pktinfo = dataSnapshot.getValue(Integer.class);
                            if(finaldistweek>Double.valueOf(listofdistancequest.get(pktinfo))&& pktinfo!=2 && checkWeekTaskDist!=true)
                            {
                                if(pktinfo!=2)
                                {
                                    pktinfo++;
                                }


                                DatabaseReference nrweek= FirebaseDatabase.getInstance().getReference().child("Users").child
                                        ("Customers").child("Historia").child(user_id).child("nrTygodniowegoDist");
                                nrweek.setValue(pktinfo);


                                reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Punkty zostały dodane za pokonany najlepszy dystans w tygodniu, następny quest poniżej");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

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

                                            }
                                        });
                                alertDialog.show();
                                if(numberofQuestDay==2)
                                {
                                    DatabaseReference nrweekch= FirebaseDatabase.getInstance().getReference().child("Users").child
                                            ("Customers").child("Historia").child(user_id).child("checknrTygodniowegoDist");
                                    nrweekch.setValue(true);
                                }
                            }

                            distancequest.setText("Dodatkowo musisz pokonać dystans: "+listofdistancequest.get(numberofQuestDay));

                                Integer val=Integer.valueOf(listofdistancequest.get(numberofQuestDay));
                                String dist=dff.format(finaldistweek);
                                Integer finaldistweekInt=Integer.valueOf(dist);
                                Integer finaldiffbeetweenquestandyou=val-finaldistweekInt;

                            if(finaldiffbeetweenquestandyou>0)
                            {
                                distancequest.setText("Dodatkowo musisz pokonać dystans: "+finaldiffbeetweenquestandyou);
                            }
                            else
                            {
                                distancequest.setText("Questy dystansowe tygodniowe zrobione");
                            }

                        }
                        else
                        {
                            distancequest.setText("Dodatkowo musisz pokonać dystans:: "+listofdistancequest.get(0));

                        }

                    }

                    @Override public void onCancelled(DatabaseError databaseError) {}
                });

                reffweekavgnumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowegoAvg");
                reffweekavgnumber.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                            int pktinfo = dataSnapshot.getValue(Integer.class);
                            //System.out.println("weekquest"+finalavgofdistance);
                            // System.out.println("weeklist"+Double.valueOf(listofaveragequest.get(pktinfo)));
                            if(finalavgofdistance>Double.valueOf(listofaveragequest.get(pktinfo))&& pktinfo!=2 && checkWeekTaskAvg!=true)
                            {
                                if(pktinfo!=2)
                                {
                                    pktinfo++;
                                }

                                DatabaseReference nrweek= FirebaseDatabase.getInstance().getReference().child("Users").child
                                        ("Customers").child("Historia").child(user_id).child("nrTygodniowegoAvg");
                                nrweek.setValue(pktinfo);


                                reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");

                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Punkty zostały dodane za uzyskana najlepsza srednia w tygodniu, następny quest poniżej");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {


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

                                            }
                                        });
                                alertDialog.show();
                                if(numberofQuestDay==2)
                                {
                                    DatabaseReference nrweekch= FirebaseDatabase.getInstance().getReference().child("Users").child
                                            ("Customers").child("Historia").child(user_id).child("checknrTygodniowegoAvg");
                                    nrweekch.setValue(true);
                                }
                            }
                            Double val=Double.valueOf(listofaveragequest.get(numberofQuestDay));
                            final DecimalFormat dffs = new DecimalFormat("#.#");

                            Double finaldiffbeetweenquestandyou=val-finalavgofdistance;
                            String dffinaldiffbeetweenquestandyou=dffs.format(finaldiffbeetweenquestandyou);
                            if(finaldiffbeetweenquestandyou>0)
                            {
                                avgquest.setText("Musisz uzyskać srednią lepszą o: "+dffinaldiffbeetweenquestandyou+"km/h");
                            }
                            else
                            {
                                avgquest.setText("Questy tygodniowę na średnią zrobione");
                            }

                        }
                        else {
                            avgquest.setText("Musisz uzyskać srednią lepszą       o: "+listofaveragequest.get(0)+"km/h");

                        }

                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });

                reffcount = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrTygodniowyCount");
                reffcount.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                            int pktinfo = dataSnapshot.getValue(Integer.class);
                            if(finalnumberoftrain>Double.valueOf(listofcounttrainquest.get(pktinfo))&& pktinfo!=2 && checkWeekTaskCount!=true)
                            {
                                if(pktinfo!=2)
                                {
                                    pktinfo++;
                                }

                                DatabaseReference nrweek= FirebaseDatabase.getInstance().getReference().child("Users").child
                                        ("Customers").child("Historia").child(user_id).child("nrTygodniowyCount");
                                nrweek.setValue(pktinfo);

                                reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");


                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Punkty został dodane za liczbe treningow, następny quest ponizej");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

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

                                                dialog.dismiss();

                                            }
                                        });
                                alertDialog.show();

                                if(numberofQuestDay==2)
                                {
                                    DatabaseReference nrweekch= FirebaseDatabase.getInstance().getReference().child("Users").child
                                            ("Customers").child("Historia").child(user_id).child("checknrTydziennegoCount");
                                    nrweekch.setValue(true);
                                }
                            }
                            Integer val=Integer.valueOf(listofcounttrainquest.get(numberofQuestDay));
                            String counttrain=dff.format(finalnumberoftrain);
                            Integer finalcounttweekInt=Integer.valueOf(counttrain);
                            Integer finaldiffbeetweenquestandyou=val-finalcounttweekInt;

                            if(finaldiffbeetweenquestandyou>0)
                            {
                                counttrainquest.setText("Wykonaj więcej treningów: "+finaldiffbeetweenquestandyou);
                            }
                            else
                            {
                                counttrainquest.setText("Questy tygodniowę na liczbę treningów zrobione");
                            }
                        }
                        else
                        {
                            counttrainquest.setText("Wykonaj więcej treningów: "+listofcounttrainquest.get(0));

                        }

                    }

                    @Override public void onCancelled(DatabaseError databaseError) {}
                });

                DecimalFormat dfsuma = new DecimalFormat("#.#");
                checkdistanceweek.setText(dfsuma.format(finaldist)+"m");
                checkdistanceavg.setText(dfsuma.format(finalavgofdistance)+"km/h");
                checkdistanceCount.setText(String.valueOf(finalnumberoftrain));
                reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrDziennego");
                reffquestdaynumber.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                            int pktinfo = dataSnapshot.getValue(Integer.class);

                            int countquest= dataSnapshot.getValue(Integer.class);
                            System.out.println("dayquest"+pktinfo);
                            if(finaldistanceday>Double.valueOf(listofdistancequestday.get(pktinfo) )&& pktinfo!=3 && checktask!=true)
                            {

                                if(pktinfo!=2) {
                                    pktinfo++;
                                    countquest++;
                                }
                                System.out.println("d"+countquest);


                                DatabaseReference nrday = FirebaseDatabase.getInstance().getReference().child("Users").child
                                        ("Customers").child("Historia").child(user_id).child("nrDziennego");
                                nrday.setValue(pktinfo);

                                reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");


                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Punkty zostały dodane za uzyskana najlepszy dystans w dniu, następny quest poniżej");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                reffpkt.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    boolean processDone = false;

                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists() && !processDone) {
                                                            int pktinfo = dataSnapshot.getValue(Integer.class);

                                                            System.out.println("inf"+pktinfo);
                                                            addpoints(pktinfo,numberofQuestDay);


                                                        } else {
                                                            // do process 2
                                                            processDone = true;
                                                        }
                                                    }

                                                    @Override public void onCancelled(DatabaseError databaseError) {}
                                                });

                                                dialog.dismiss();

                                            }
                                        });
                                alertDialog.show();

                                if(numberofQuestDay==2)
                                {
                                    DatabaseReference nrweekch= FirebaseDatabase.getInstance().getReference().child("Users").child
                                            ("Customers").child("Historia").child(user_id).child("checknrDziennego");
                                    nrweekch.setValue(true);
                                }
                            }

                            Integer val=Integer.valueOf(listofdistancequestday.get(numberofQuestDay));
                            String dist=dff.format(finaldistweek);
                            Integer finaldistweekInt=Integer.valueOf(dist);
                            Integer finaldiffbeetweenquestandyou=val-finaldistweekInt;
                            if(finaldiffbeetweenquestandyou>0)
                            {
                                daydistancequest.setText("Dodatkowo musisz pokonać dystans: "+finaldiffbeetweenquestandyou);
                            }
                            else
                            {
                                daydistancequest.setText("Questy dystansowe dzienne zrobione");
                            }


                        } else {
                            daydistancequest.setText("Dodatkowo musisz pokonać dystans:  "+listofdistancequestday.get(0));

                        }

                    }

                    @Override public void onCancelled(DatabaseError databaseError) {}
                });



                reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("nrDziennegoAvg");
                reffquestdaynumber.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final int numberofQuestDay = dataSnapshot.getValue(Integer.class);
                            int pktinfoAvg = dataSnapshot.getValue(Integer.class);



                            if(finalavgday>Double.valueOf(listofavgquestday.get(pktinfoAvg))&& pktinfoAvg!=2 && checktaskAvg!=true)
                            {

                                if(pktinfoAvg!=2)
                                {
                                    pktinfoAvg++;
                                }

                                DatabaseReference nravgday = FirebaseDatabase.getInstance().getReference().child("Users").child
                                        ("Customers").child("Historia").child(user_id).child("nrDziennegoAvg");
                                nravgday.setValue(pktinfoAvg);


                                reffpkt = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("punkty");



                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Punkty zostały dodane za uzyskana najlepszy średnia  w dniu, następny quest poniżej");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

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
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                if(numberofQuestDay==2)
                                {
                                    DatabaseReference nrweekch= FirebaseDatabase.getInstance().getReference().child("Users").child
                                            ("Customers").child("Historia").child(user_id).child("checknrDziennegoAvg");
                                    nrweekch.setValue(true);
                                }
                            }
                            Double val=Double.valueOf(listofavgquestday.get(numberofQuestDay));
                            final DecimalFormat dffs = new DecimalFormat("#.#");
                            Double finaldiffbeetweenquestandyou=val-finalavgday;
                            String dffinaldiffbeetweenquestandyou=dffs.format(finaldiffbeetweenquestandyou);
                            if(finaldiffbeetweenquestandyou>0)
                            {
                                dayavgquest.setText("Musisz uzyskać srednią lepszą       o: "+dffinaldiffbeetweenquestandyou+"km/h");
                            }
                            else {
                                dayavgquest.setText("Questy na średnią predkość dzienna zrobione");
                            }


                            listofmaxavgfromday.clear();
                        } else {
                            dayavgquest.setText("Musisz uzyskać srednią lepszą       o: "+listofavgquestday.get(0)+"km/h");
                        }
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });

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

        reffpointsto.addListenerForSingleValueEvent(new ValueEventListener() {
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
        });

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

        reffpkt.addListenerForSingleValueEvent(new ValueEventListener() {
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
        });

    }


    public void setTextPkt(String value)
    {
        String user_id = mAuth.getCurrentUser().getUid();
        pktinfoT.setText("Twoje punkty: "+value);

        if(Integer.valueOf(value)<2000)
        {
            DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                    (user_id).child("lvl");
            pkt.setValue("Easy");
            lvlinfo.setText("Level: "+ "Easy");
            int max=2000;
            int diff=max-Integer.valueOf(value);
            pkttoLvl.setText("Punkty do nastepnęgo lvl:"+String.valueOf(diff));

        }

        if(Integer.valueOf(value)>2000)
        {
            DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                    (user_id).child("lvl");
            lvlinfo.setText("Level: "+ "Medium");
            int max=4000;
            int diff=max-Integer.valueOf(value);
            pkttoLvl.setText("Punkty do nastepnęgo lvl:"+String.valueOf(diff));


        }
        if(Integer.valueOf(value)>4000)
        {
            DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                    (user_id).child("lvl");
            pkt.setValue("High");
            lvlinfo.setText("Level: "+ "High");

        }

    }
    public void setTextLvl(String value)
    {
        lvlinfo.setText("Level: "+value);
        ///getFragmentManager().beginTransaction().replace(R.id.fragment_container,
        //  new FragmentQuest()).commit();

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
            add=100;
        }
        if(number ==1 )
        {
            add=200;
        }
        if(number ==2 )
        {
            add=250;
        }

        final int score=pktinfo+add;
        String pktint=String.valueOf(score);
        DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("punkty");
        pkt.setValue(score);
        pktinfoT.setText("Twoje punkty: "+score);
        int max=0;
        if(score>2000 && score<3999)
        {
            int diff=4000-Integer.valueOf(score);
            pkttoLvl.setText("Punkty do nastepnęgo lvl:"+String.valueOf(diff));
        }
        if(score<1999)
        {
            int diff=2000-Integer.valueOf(score);
            pkttoLvl.setText("Punkty do nastepnęgo lvl:"+String.valueOf(diff));
        }


        if(pktinfo>1751&& pktinfo<1999 && score >2000)
        {


            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Awansowałes na wyższy lvl");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference pkt2 = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id)
                                    .child("lvl");
                            pkt2.setValue("Medium");
                            resetTask();
                            resetchecks();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        if(pktinfo>3800 && pktinfo<3999)
        {

            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Awansowałes na wyższy lvl");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference pkt2 = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id)
                                    .child("lvl");
                            pkt2.setValue("High");
                            resetchecks();
                            resetTask();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        listofmaxdistancefromday.clear();
        finalavgday=0.0;
        finalavgofdistance=0.0;
        listofmaxavgfromday.clear();
        listofmaxavgfromweek.clear();
        finaldistanceday=0.0;

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
        DatabaseReference pkt = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("punkty");
        pkt.setValue(score);
        pktinfoT.setText("Twoje punkty: "+score);
        int max=0;
        if(score>2000 && score<3999)
        {
            int diff=4000-Integer.valueOf(score);
            pkttoLvl.setText("Punkty do nastepnęgo lvl:"+String.valueOf(diff));
        }
        if(score<1999)
        {
            int diff=2000-Integer.valueOf(score);
            pkttoLvl.setText("Punkty do nastepnęgo lvl:"+String.valueOf(diff));
        }


        if(pktinfo>2000)
        {


            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Awansowałes na wyższy lvl");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            resetTask();
                            DatabaseReference pkt2 = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id)
                                    .child("lvl");
                            pkt2.setValue("Medium");



                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        if(pktinfo>4000)
        {

            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Awansowałes na wyższy lvl");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference pkt2 = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id)
                                    .child("lvl");
                            pkt2.setValue("High");
                            resetTask();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        listofmaxdistancefromday.clear();
        finaldistanceday=0.0;


    }

    public static FragmentQuest newInstance() {
        return new FragmentQuest();
    }
    public void resetTask()
    {
        final  String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference nravgday = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("nrDziennegoAvg");
        nravgday.setValue(0);
        DatabaseReference nrdistday = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("nrDziennego");
        nrdistday.setValue(0);
        DatabaseReference nrweekdistday = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("nrTygodniowegoDist");
        nrweekdistday.setValue(0);
        DatabaseReference nrweekavg = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("nrTygodniowegoAvg");
        nrweekavg.setValue(0);
        DatabaseReference nrweekcount = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("nrTygodniowyCount");
        nrweekcount.setValue(0);
    }
    public void maxTask()
    {
        final  String user_id = mAuth.getCurrentUser().getUid();
        reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("checknrDziennego");
        reffquestdaynumber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checktask = dataSnapshot.getValue(Boolean.class);

                    System.out.println("checktask "+checktask);
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        });
        reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("checknrDziennegoAvg");
        reffquestdaynumber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checktaskAvg = dataSnapshot.getValue(Boolean.class);

                    System.out.println("checktaskAvg "+checktaskAvg);
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        });
        reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia")
                .child(user_id).child("checknrTygodniowegoDist");
        reffquestdaynumber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkWeekTaskDist = dataSnapshot.getValue(Boolean.class);

                    System.out.println("checkWeekTaskDist "+checkWeekTaskDist);
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        });


        reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia")
                .child(user_id).child("checknrTygodniowegoAvg");
        reffquestdaynumber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkWeekTaskAvg = dataSnapshot.getValue(Boolean.class);

                    System.out.println("checkWeekTaskAvg "+checkWeekTaskAvg);
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        });


        reffquestdaynumber = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia")
                .child(user_id).child("checknrTydziennegoCount");
        reffquestdaynumber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkWeekTaskCount = dataSnapshot.getValue(Boolean.class);

                    System.out.println("checkWeekTaskCount "+checkWeekTaskCount);
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {}
        });

    }
    public void resetchecks()
    {
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference nravgday = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("checknrDziennego");
        nravgday.removeValue();
        DatabaseReference countsda = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("checkWeekTaskCount");
        countsda.removeValue();
        DatabaseReference nrweekavg = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("checkWeekTaskAvg");
        nrweekavg.removeValue();
        DatabaseReference nrweekdist = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("checkWeekTaskDist");
        nrweekdist.removeValue();
        DatabaseReference nrcountweek = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("checkWeekTaskCount");
        nrcountweek.removeValue();

        FragmentQuest questfragment=new FragmentQuest();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,questfragment).commit();
    }

}
