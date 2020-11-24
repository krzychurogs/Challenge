package com.example.challenge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SymbolTable;
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
import com.example.challenge.MyAdapter.customButtonListener;
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

public class FragmentMyFriends extends Fragment  {
    DatabaseReference reff,reffs,reffsrequest,reffsktoname,reffsdelete;
    private FirebaseAuth mAuth;
    private FragmentMyFriendsListener listener;
    EditText e1,e2;
    ListView l1,l2;
    List<String>listofkey=new ArrayList<String>();
    List<String>listofname=new ArrayList<String>();
    List<String>listoffriendname=new ArrayList<String>();
    List<String>listofkeyrequest=new ArrayList<String>();
    List<String>listofuserspended=new ArrayList<String>();
    List<String>listofkeyspended=new ArrayList<String>();
    List<String>listofkeyfriend=new ArrayList<String>();
    List<String>listofkeybyname=new ArrayList<String>();

    List<LatLng> points ;
    String []name={"Pakistan","India","Szwecja"};
    String []lvl={"Easy","Medium","High"};
    ArrayList<SingleRow>mylist;
    ArrayList<SingleRow>pendlist;
    MyAdapter myAdapterPend;
    AdapterDelete myAdapter;
    public interface FragmentMyFriendsListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mylist=new ArrayList<>();
        pendlist=new ArrayList<>();
        SingleRow singleRow;


        final View root = inflater.inflate(R.layout.fragment_my_friends, container, false);

        e1=(EditText)root.findViewById(R.id.editsearch);
        e2=(EditText)root.findViewById(R.id.editsearchrequest);
        l1=(ListView)root.findViewById(R.id.sampleListView);
        l2=(ListView)root.findViewById(R.id.sampleListViewRequest);

        friends();
        showAllUsers(); //pending

        return root;

    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentMyFriendsListener){
            listener= (FragmentMyFriendsListener) context;

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
    public void friends()
    {
        System.out.println("test");
        listofkeyfriend.clear();
        String user_id = mAuth.getCurrentUser().getUid();
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("friends");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String state =  String.valueOf(ds.getValue());
                    String friend =  String.valueOf(ds.getKey());
                    listofkeyfriend.add(friend);
                }
                for(int i=0;i<listofkeyfriend.size();i++)
                {
                    keytoname(listofkeyfriend.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);





    }


    public void showRequest()
    {
        final String user_id = mAuth.getCurrentUser().getUid();
        reffsrequest = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                    .child("Historia").child(user_id).child("FriendsRequest");
        listofkeyrequest.clear();

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        System.out.println("test");
                    }



                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String key = String.valueOf(ds.getKey());
                            String name = String.valueOf(ds.getValue());
                            if(name.equals("pended"))
                            {
                                listofkeyrequest.add(key);
                            }
                    }


                    for (int i = 0; i < listofkeyrequest.size(); i++) {
                        System.out.println("req"+listofkeyrequest.get(i));
                        showNameByKey(listofkeyrequest.get(i));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reffsrequest.addListenerForSingleValueEvent(valueEventListener);

    }

    public void showNameByKey(final String key)
    {
      //  System.out.println("ka"+key);
        listofuserspended.clear();
        listofkeyspended.clear();
        reffs= FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                String namefriend =  String.valueOf(dataSnapshot.child("name").getValue());
                String lvl =  String.valueOf(dataSnapshot.child("lvl").getValue());
                   // System.out.println("K"+key);
                   // System.out.println("V"+namefriend);
                    listofuserspended.add(namefriend);
                    listofkeyspended.add(key);
                    showUsersInTable(namefriend,key,lvl);



            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reffs.addListenerForSingleValueEvent(valueEventListener);

    }
    public void showUsersInTable(final String name,final String key,String lvl)
    {
        System.out.println("na"+name);

        SingleRow singleRow = new SingleRow(name, lvl);
        pendlist.add(singleRow);
        myAdapterPend=new MyAdapter(getActivity(),pendlist);
        myAdapterPend.setCustomButtonListner(new customButtonListener() {
            @Override
            public void onButtonClickListner(int position) {
                changeStatus(listofkeyspended.get(position));
                pendlist.clear();
                myAdapterPend.notifyDataSetChanged();
               showRequest();



            }
        });
        l2.setAdapter(myAdapterPend);
        myAdapterPend.notifyDataSetChanged();
        e2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myAdapterPend.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void changeStatus(String key) {
        final String user_id = mAuth.getCurrentUser().getUid();
        System.out.println(key);


        DatabaseReference requestpend = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("FriendsRequest").child(key);
        requestpend.setValue("confirmed");

        DatabaseReference requestfriend = FirebaseDatabase.getInstance().getReference().child("Users").child
                ("Customers").child("Historia").child(user_id).child("friends").child(key);
        requestfriend.setValue("easy");
        l1.setAdapter(null);
        friends();
    }

    public void showAllUsers()
    {

        final String user_id = mAuth.getCurrentUser().getUid();
        reff = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = reff.orderByChild("name").equalTo("Krzysztof");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = String.valueOf(ds.getKey());
                    String name = String.valueOf(ds.getValue());
                    if(!key.equals(user_id))
                    {
                        listofkey.add(key);
                    }

                }

                    showRequest();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reff.addListenerForSingleValueEvent(valueEventListener);
    }

    public void keytoname(final String key)
    {
        l1.invalidateViews();
        mylist.clear();
        String user_id = mAuth.getCurrentUser().getUid();
        reffsktoname= FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                    String namefriend =  String.valueOf(dataSnapshot.child("name").getValue());
                    String lvl =  String.valueOf(dataSnapshot.child("lvl").getValue());
                    SingleRow singleRow = new SingleRow(namefriend,lvl);
                    mylist.add(singleRow);
                    myAdapter = new AdapterDelete(getActivity(), mylist);
                    myAdapter.setCustomButtonListner(new AdapterDelete.customButtonListener() {
                        @Override
                        public void onButtonClickListner(int position) {

                            String user_id = mAuth.getCurrentUser().getUid();
                            deletefriend(listofkeyfriend.get(position));
                            mylist.remove(position);
                            myAdapter.notifyDataSetChanged();
                            friends();

                        }
                    });
                    l1.setAdapter(myAdapter);

                    e1.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            myAdapter.getFilter().filter(s);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reffsktoname.addListenerForSingleValueEvent(valueEventListener);
    }

    public void deletefriend(String key)
    {
        final String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("friends").child(key);
        db_node.removeValue();
        DatabaseReference db_node1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("FriendsRequest").child(key);
        db_node1.removeValue();
    }

}