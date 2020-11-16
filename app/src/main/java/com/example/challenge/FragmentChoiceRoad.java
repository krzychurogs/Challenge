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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragmentChoiceRoad extends Fragment {
    DatabaseReference reff;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Location flocation;
    private FragmentChoiceRoadListener listener;
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
    private ChoiceRoadAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ChoiceRoadItem>exampleList=new ArrayList<>();



    public interface FragmentChoiceRoadListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();


        final View root = inflater.inflate(R.layout.fragment_fragment_choice_road, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        final Bundle bundle=new Bundle();


        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String trasa=String.valueOf(ds.getKey());
                    String message=String.valueOf(ds.child("image").getValue());
                    String dist=String.valueOf(ds.child("odleglosc").getValue());
                    String maxavg=String.valueOf(ds.child("avgmax").getValue());
                    System.out.println(maxavg);
                    ChoiceRoadItem choiceRoadItem=new ChoiceRoadItem();
                    choiceRoadItem.setmImageResource(message);
                    choiceRoadItem.setmImageResourceAvg(R.drawable.ic_timer_black_52dp);
                    choiceRoadItem.setmImageResourceDist(R.drawable.ic_location_on_black_52dp);
                    choiceRoadItem.setmText1(trasa);
                    choiceRoadItem.setmText2(dist+"m");
                    if(maxavg.equals("null"))
                    {
                        choiceRoadItem.setmText3("brak");
                    }
                    else {
                        choiceRoadItem.setmText3(maxavg+" km/h");
                    }

                    roadlists.add(trasa);
                    exampleList.add(choiceRoadItem);
                }


                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mAdapter = new ChoiceRoadAdapter(exampleList,getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new ChoiceRoadAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        System.out.println(position);
                        bundle.putString("name",roadlists.get(position));
                        FragmentRoad roadfragment=new FragmentRoad();
                        roadfragment.setArguments(bundle);
                        FragmentTransaction transaction=getFragmentManager().beginTransaction();
                        transaction.replace(R.id.drawer_layout,roadfragment);
                        transaction.commit();

                    }
                });

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
        if(context instanceof FragmentChoiceRoadListener){
            listener= (FragmentChoiceRoadListener) context;

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
