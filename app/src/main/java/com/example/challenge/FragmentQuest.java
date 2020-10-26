package com.example.challenge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FragmentQuest extends Fragment {
    DatabaseReference reff,reffname,refquest;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location flocation;
    private FragmentQuestListener listener;
    Button next,back;
    TableLayout tl;
    TextView textdistance,textspeed;
    private LatLng lastKnownLatLng;
    List<String>listofplace=new ArrayList<String>();
    List<String>distancelist=new ArrayList<String>();
    List<String>averagelist=new ArrayList<>();
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
    List<String>listofdistancequest=new ArrayList<>();
    List<String>listofcounttrainquest=new ArrayList<>();
    List<String>listofaveragequest=new ArrayList<>();
    List<Double>listofmaxdistancefromweek=new ArrayList<>();
    List<Double>listofmaxavgfromweek=new ArrayList<>();
    List<Integer>listofnumberofromweek=new ArrayList<>();
    double sumaofdistancefir=0.0;
    double sumaofdistancedw=0.0;
    double finalsumaofdistance=0.0;
    double finalavgofdistance=0.0;
    int finalnumberoftrain=0;
    double avgofdistancefir=0.0;
    double avgofdistancesec=0.0;
    LinearLayout linearlayout;
    LinearLayout linearavg;
    LinearLayout linearcount;
    LinearLayout linearvertical;
    LinearLayout lineardayavg;
    LinearLayout lineardaydist;
    TextView questinfo;

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
        lineardayavg= (LinearLayout)    root.findViewById(R.id.linearforavg);
        lineardaydist= (LinearLayout)   root.findViewById(R.id.linearfordaydist);
        refquest=FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Questy").child("Easy").child("tygodniowe");

        refquest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String firstlvldist=String.valueOf(dataSnapshot.child("dystans").child("1lvl").getValue());
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
                stats();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                // Display the view

            }

            Set<Integer> unique = new HashSet<Integer>(counter);
            for (Integer key : unique) {
                licznik.add(key);
                counterofweek.add(Collections.frequency(counter, key));
            }
            int counteroffirweek=0;
            int counterofsecrweek=0;
            for (int i = 0; i < distancelist.size(); i++) {


                if(calendar(listofdates.get(i))== 43)
                {

                    sumaofdistancefir+=Double.valueOf(distancelist.get(i).replace(",","."));
                    avgofdistancefir+=Double.valueOf(averagelist.get(i).replace(",","."));
                    counteroffirweek+=1;
                }
                if(calendar(listofdates.get(i))== 42)
                {

                    sumaofdistancedw+=Double.valueOf(distancelist.get(i).replace(",","."));
                    avgofdistancesec+=Double.valueOf(averagelist.get(i).replace(",","."));
                    counterofsecrweek+=1;

                }
                listofmaxavgfromweek.add(avgofdistancefir);
                listofmaxavgfromweek.add(avgofdistancesec);
                listofmaxdistancefromweek.add(sumaofdistancefir);
                listofmaxdistancefromweek.add(sumaofdistancedw);
                listofnumberofromweek.add(counteroffirweek);
                listofnumberofromweek.add(counterofsecrweek);
            }
            finalsumaofdistance=Collections.max(listofmaxdistancefromweek);
            finalavgofdistance=Collections.max(listofmaxdistancefromweek)/counteroffirweek;
            finalnumberoftrain=Collections.max(listofnumberofromweek);
            System.out.println("max "+finalsumaofdistance);
            System.out.println("43 "+sumaofdistancefir);
            System.out.println("42 "+sumaofdistancedw);
            TextView distancequest = new TextView(getActivity());
            TextView avgquest = new TextView(getActivity());
            TextView counttrainquest = new TextView(getActivity());
            TextView daydistancequest=new TextView(getActivity());
            TextView dayavgquest=new TextView(getActivity());
            CheckBox checkdayavg=new CheckBox((getActivity()));
            CheckBox checkdaydist=new CheckBox((getActivity()));
            CheckBox checkdistance=new CheckBox((getActivity()));
            CheckBox checkavg=new CheckBox((getActivity()));
            CheckBox checkcounttrain=new CheckBox((getActivity()));
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(10, 40, 10, 40);


            LayoutParams layoutParamscheck = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(10, 10, 10, 10  ); // (left, top, right, bottom)

            distancequest.setLayoutParams(layoutParams);
            distancequest.setText("Dystans w tygodniu: "+listofdistancequest.get(0));
            distancequest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
          // hex color 0xAARRGGBB

            avgquest.setLayoutParams(layoutParams);
            avgquest.setText("Srednia w tygodniu: "+listofaveragequest.get(0)+"km/h");
            avgquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            counttrainquest.setLayoutParams(layoutParams);
            counttrainquest.setText("Liczba treningow: "+listofaveragequest.get(0));
            counttrainquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            DecimalFormat dfsuma = new DecimalFormat("#.##");

            checkdistance.setLayoutParams(layoutParamscheck);
            checkdistance.setText(dfsuma.format(finalsumaofdistance)+"m.");
            checkdistance.setTextSize(16);
            checkdistance.setPadding(20,20,20,20);

            checkavg.setLayoutParams(layoutParamscheck);
            checkavg.setText(dfsuma.format(finalavgofdistance)+"km/h");
            checkavg.setTextSize(16);


            checkcounttrain.setLayoutParams(layoutParamscheck);
            checkcounttrain.setText(String.valueOf(finalnumberoftrain));
            checkcounttrain.setTextSize(16);

            daydistancequest.setLayoutParams(layoutParams);
            daydistancequest.setText("Dystans w dniu: "+listofdistancequest.get(0));
            daydistancequest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            dayavgquest.setLayoutParams(layoutParams);
            dayavgquest.setText("Srednia w tygodniu: "+listofdistancequest.get(0));
            dayavgquest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            checkdayavg.setLayoutParams(layoutParamscheck);
            checkdayavg.setText("");
            checkdayavg.setTextSize(16);

            checkdaydist.setLayoutParams(layoutParamscheck);
            checkdaydist.setText("");
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

}
