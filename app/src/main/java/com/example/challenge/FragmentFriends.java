package com.example.challenge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragmentFriends extends Fragment implements TextWatcher {
    DatabaseReference reff, reffs, reffrequest;
    private FirebaseAuth mAuth;
    private FragmentFriendsListener listener;
    EditText e1;
    ListView l1;
    List<String> listofkey = new ArrayList<String>();
    List<String> listofname = new ArrayList<String>();

    List<LatLng> points;
    String[] name = {"Pakistan", "India", "Szwecja"};
    String[] lvl = {"Easy", "Medium", "High"};
    ArrayList<SingleRow> mylist;
    MyAdapter myAdapter;

    public interface FragmentFriendsListener {
        void onInputSent(CharSequence input);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mylist = new ArrayList<>();
        SingleRow singleRow;


        final View root = inflater.inflate(R.layout.fragment_fragment_friends, container, false);

        e1 = (EditText) root.findViewById(R.id.editsearch);
        l1 = (ListView) root.findViewById(R.id.sampleListView);
        e1.addTextChangedListener(this);
        reff = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = reff.orderByChild("name").equalTo("Krzysztof");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> newlist = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = String.valueOf(ds.getKey());
                    String name = String.valueOf(ds.getValue());
                    listofkey.add(key);
                }
                for (int i = 1; i < listofkey.size(); i++) {
                    stats(i);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);
        return root;

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        this.myAdapter.getFilter().filter(charSequence);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentFriendsListener) {
            listener = (FragmentFriendsListener) context;

        } else {
            throw new RuntimeException(context.toString() + " trzeba implementowac Listnera");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void stats(int i) {


        reffs = FirebaseDatabase.getInstance().getReference().child("Users").child(listofkey.get(i));
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> newlist = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = String.valueOf(ds.getValue());
                    SingleRow singleRow = new SingleRow(name, "easy");
                    mylist.add(singleRow);

                    myAdapter = new MyAdapter(getActivity(), mylist);
                    l1.setAdapter(myAdapter);

                    l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            // System.out.println(listofname.get(position));
                            String user_id = mAuth.getCurrentUser().getUid();
                            keyfriendname(listofkey.get(position+1));
                        }
                    });
                    listofname.add(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reffs.addListenerForSingleValueEvent(valueEventListener);
    }

    public void keyfriendname(String key) {
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference requestfriend = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("FriendsRequest").child(key);
        requestfriend.setValue("pended");

        }
    }

