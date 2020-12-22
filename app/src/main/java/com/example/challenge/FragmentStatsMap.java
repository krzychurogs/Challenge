package com.example.challenge;

        import android.content.Context;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.location.Location;
        import android.media.Image;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageButton;
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
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.firestore.auth.User;
        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Iterator;
        import java.util.LinkedHashSet;
        import java.util.List;
        import java.util.Set;
        import java.util.stream.Collectors;

public class FragmentStatsMap extends Fragment {
    DatabaseReference reff,reffs;
    Query reffname;
    private FirebaseAuth mAuth;
    private FragmentStatsListener listener;
    List<String>listofnamedup=new ArrayList<String>();
    List<String>listOfYourLvlNameDup=new ArrayList<String>();
    List<String>listOfYourLvlAvgDup=new ArrayList<String>();
    List<String>listofnamefrienddup=new ArrayList<String>();
    List<String>listofavgdup=new ArrayList<String>();
    List<String>listofavgefrienddup=new ArrayList<String>();

    List<String>reverselistofnamedup=new ArrayList<String>();
    List<String>reverselistofavgdup=new ArrayList<String>();
    List<String>reverseListOfYourLvlNameDup=new ArrayList<String>();
    List<String>reverseeListOFYourLvlAvgDup=new ArrayList<String>();

    List<String>reverselistofFriendnamedup=new ArrayList<String>();
    List<String>reverselistofFriendavgdup=new ArrayList<String>();
    List<String>listofnameDuplicates=new ArrayList<String>();
    List<String>listofnameYourLvlDuplicates=new ArrayList<String>();
    List<String>listofFriendnameDuplicates=new ArrayList<String>();
    List<String>listofName=new ArrayList<String>();
    List<String>listofavgDuplicates=new ArrayList<String>();
    ArrayList<TableItem>exampleList=new ArrayList<TableItem>();
    ArrayList<TableItem>yourLvlList=new ArrayList<TableItem>();
    ArrayList<TableItem>friendList=new ArrayList<TableItem>();
    List<String>listofNameLvl=new ArrayList<String>();

    List<String>listoffriendname=new ArrayList<String>();
    private RecyclerView mRecyclerView,mRecyclerViewFriend,mRecyclerYourLvl;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutFriendManager,mLayoutYourLvlManager;
    private TableAdapter mAdapter,yourLvlAdapter,friendAdapter;

    ArrayList<TableItem>reverseList=new ArrayList<>();
    ArrayList<TableItem>reverseListYourLvl=new ArrayList<>();
    ArrayList<TableItem>reverseListFriend=new ArrayList<>();
    List<String>listoffriendkey=new ArrayList<String>();
    ImageButton changetable,changetableyourlvl;
    ImageView imageuser;
    ImageButton changetableAllUsers;
    ImageButton changetableAllUsersInMain;
    int counter=1;
    int counterYourLvl=1;
    int counterfriend=1;
    String lvlOfUser;
    TextView MScore,MFriendInfo,MInfo,MYourLvlInfo;
    int finaluserposition;
    int finallvluserposition;
    boolean checkfriend=true;
    boolean checknofriend=false;


    public interface FragmentStatsListener{
        void onInputSent(CharSequence input);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        final View root = inflater.inflate(R.layout.fragment_fragment_stats_map, container, false);
        showFriend();
        mRecyclerView = root.findViewById(R.id.recyclerViewMovieList);
        imageuser=(ImageView) root.findViewById(R.id.imagemedal);
        mRecyclerYourLvl= root.findViewById(R.id.recyclerYourLvl);
        mRecyclerViewFriend= root.findViewById(R.id.recyclerViewFriend);
        changetableyourlvl=(ImageButton) root.findViewById(R.id.changetableToYourLvl);
        changetable=(ImageButton) root.findViewById(R.id.changetable);
        changetableAllUsersInMain=(ImageButton) root.findViewById(R.id.changetable1);
        MScore=(TextView) root.findViewById(R.id.textViewYourScore);
        MYourLvlInfo=(TextView) root.findViewById(R.id.textViewYourLvl);
        MFriendInfo=(TextView) root.findViewById(R.id.textViewFriendTitl);
        MInfo=(TextView) root.findViewById(R.id.textViewTitle);
        imageuser.setImageResource(R.drawable.profile);
        mRecyclerViewFriend.setVisibility(View.INVISIBLE);
        mRecyclerYourLvl.setVisibility(View.INVISIBLE);
        MInfo.setVisibility(View.VISIBLE);
        MFriendInfo.setVisibility(View.INVISIBLE);
        changetableAllUsers=(ImageButton) root.findViewById(R.id.changetableToAll);
        changetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mRecyclerYourLvl.setVisibility(View.INVISIBLE);
                mRecyclerViewFriend.setVisibility(View.VISIBLE);
                changetableAllUsers.setVisibility(View.VISIBLE);
                changetableAllUsersInMain.setVisibility(View.INVISIBLE);
                changetableyourlvl.setVisibility(View.INVISIBLE);
                changetable.setVisibility(View.INVISIBLE);
                MYourLvlInfo.setVisibility(View.INVISIBLE);
                MInfo.setVisibility(View.INVISIBLE);
                MFriendInfo.setVisibility(View.VISIBLE);


            }
        });

        changetableyourlvl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mRecyclerYourLvl.setVisibility(View.VISIBLE);
                mRecyclerViewFriend.setVisibility(View.INVISIBLE);
                changetableAllUsers.setVisibility(View.VISIBLE);
                changetableyourlvl.setVisibility(View.INVISIBLE);
                changetable.setVisibility(View.INVISIBLE);
                changetableAllUsersInMain.setVisibility(View.INVISIBLE);
                MYourLvlInfo.setVisibility(View.VISIBLE);
                MInfo.setVisibility(View.INVISIBLE);
                MFriendInfo.setVisibility(View.INVISIBLE);


            }
        });
        changetableAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerViewFriend.setVisibility(View.INVISIBLE);
                mRecyclerYourLvl.setVisibility(View.INVISIBLE);
                changetableAllUsers.setVisibility(View.INVISIBLE);
                changetable.setVisibility(View.VISIBLE);
                changetableyourlvl.setVisibility(View.VISIBLE);
                MInfo.setVisibility(View.VISIBLE);
                MYourLvlInfo.setVisibility(View.INVISIBLE);
                MFriendInfo.setVisibility(View.INVISIBLE);

            }
        });

        Bundle bundle=getArguments();
        System.out.println("bund"+bundle.getString("name"));
        return root;

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentStatsListener){
            listener= (FragmentStatsListener) context;

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

    public void showTable()
    {


        exampleList.clear();
        yourLvlList.clear();
        friendList.clear();
        String user_id = mAuth.getCurrentUser().getUid();
        final int limit=40;
        final Bundle bundle=getArguments();

        reffname= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Road").child(bundle.getString("name"))
                .child("meta").orderByChild("srednia").limitToLast(limit);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String>newlist=new ArrayList<String>();
                int count=10;
                int max=40 ;
                int maxfr=5;
                int size=0;
                int sizefr=0;
                int licz=2;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(max>size)
                    {   String name =    String.valueOf(ds.child("name").getValue() );
                        String srednia =  String.valueOf(ds.child("srednia").getValue());
                        lvlOfUser=String.valueOf(ds.child("lvl").getValue());

                        if(name!=null && srednia!=null && lvlOfUser.equals(listofNameLvl.get(0)))
                        {
                            //System.out.println("nams"+name);
                            listOfYourLvlNameDup.add(name);
                            listOfYourLvlAvgDup.add(srednia);
                        }
                        if(name!=null && srednia!=null )
                        {
                            listofnamedup.add(name);
                            listofavgdup.add(srednia);
                        }

                        //exampleList.add(new TableItem(String.valueOf(1),name,srednia+" km/h"));
                        for(int i=0;i<listoffriendname.size();i++) {
                            if(name.contains(listoffriendname.get(i)))
                            {
                                listofnamefrienddup.add(name);

                              //  System.out.println("namefr"+name);
                                listofavgefrienddup.add(srednia);
                            }
                        }
                    }
                    size++;
                    count--;

                }
                int position=0;
                int yourlvlposition=0;


                reverseListOfYourLvlNameDup=reverseList(listOfYourLvlNameDup);
                reverseeListOFYourLvlAvgDup=reverseList(listOfYourLvlAvgDup);
                for(int i=0;i<reverseListOfYourLvlNameDup.size();i++)
                {
                    if(!listofnameYourLvlDuplicates.contains(reverseListOfYourLvlNameDup.get(i)))
                    {
                        listofnameYourLvlDuplicates.add(reverseListOfYourLvlNameDup.get(i));
                        if(!reverseListOfYourLvlNameDup.get(i).equals("null"))
                        {
                            // System.out.println("reve"+reverseListOfYourLvlNameDup.get(i));
                            yourLvlList.add(new TableItem(String.valueOf(counterYourLvl),reverseListOfYourLvlNameDup.get(i),reverseeListOFYourLvlAvgDup.get(i)+" km/h"));
                            counterYourLvl++;
                        }
                        System.out.println("lvlposition"+reverseListOfYourLvlNameDup.get(i));
                        if(reverseListOfYourLvlNameDup.get(i).equals(listofName.get(0)))
                        {

                            finallvluserposition=yourlvlposition;
                        }
                        yourlvlposition++;
                    }

                }
                reverselistofnamedup=reverseList(listofnamedup);
                reverselistofavgdup=reverseList(listofavgdup);
                for(int i=0;i<reverselistofnamedup.size();i++)
                {
                    if(!listofnameDuplicates.contains(reverselistofnamedup.get(i)))
                    {

                        listofnameDuplicates.add(reverselistofnamedup.get(i));

                        if(!reverselistofnamedup.get(i).equals("null"))
                        {

                            System.out.println("lvlps"+reverselistofnamedup);
                            exampleList.add(new TableItem(String.valueOf(counter),reverselistofnamedup.get(i),reverselistofavgdup.get(i)+" km/h"));
                            counter++;
                        }
                        if(reverselistofnamedup.get(i).equals(listofName.get(0)))
                        {
                            MScore.setText("Twój wynik to "+reverselistofavgdup.get(i)+"km/h");
                            finaluserposition=position;
                          //  System.out.println("userpost"+finaluserposition);

                        }
                        position++;
                    }

                }

                Set<String> hashSet = new LinkedHashSet(listofnamefrienddup);
                ArrayList<String> removedDuplicatesName = new ArrayList(hashSet);
                Set<String> hashSet1 = new LinkedHashSet(listofavgefrienddup);
                ArrayList<String> removedDuplicatesAvg= new ArrayList(hashSet1);

                reverselistofFriendnamedup=reverseList(removedDuplicatesName);
                reverselistofFriendavgdup=reverseList(removedDuplicatesAvg);
                Set<String> hashSet2 = new LinkedHashSet(reverselistofFriendnamedup);
                ArrayList<String> finalremovedDuplicatesName= new ArrayList(hashSet2);

                Set<String> hashSet3 = new LinkedHashSet(reverselistofFriendavgdup);
                ArrayList<String> finalremovedDuplicatesAvg= new ArrayList(hashSet3);
                for(int i=0;i<finalremovedDuplicatesName.size();i++)
                {
                    if(!listofFriendnameDuplicates.contains(finalremovedDuplicatesName.get(i)))
                    {
                        listofFriendnameDuplicates.add(finalremovedDuplicatesName.get(i));
                        if(!finalremovedDuplicatesName.get(i).equals("null"))
                        {

                            friendList.add(new TableItem(String.valueOf(counterfriend),finalremovedDuplicatesName.get(i),finalremovedDuplicatesAvg.get(i)+" km/h"));
                            counterfriend++;
                        }

                    }

            }



                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mAdapter = new TableAdapter(exampleList,finaluserposition,checknofriend);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                reverseListFriend = reverseList(friendList);
                mLayoutFriendManager = new LinearLayoutManager(getActivity());
                friendAdapter= new TableAdapter(friendList,finaluserposition,checkfriend);
                mRecyclerViewFriend.setLayoutManager(mLayoutFriendManager);
                mRecyclerViewFriend.setAdapter(friendAdapter);


                mRecyclerYourLvl.setHasFixedSize(true);
                reverseListFriend = reverseList(yourLvlList);
                mLayoutFriendManager = new LinearLayoutManager(getActivity());
                yourLvlAdapter= new TableAdapter(yourLvlList,finallvluserposition,checknofriend);
                mRecyclerYourLvl.setLayoutManager(mLayoutFriendManager);
                mRecyclerYourLvl.setAdapter(yourLvlAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reffname.addListenerForSingleValueEvent(valueEventListener);

    }
    public static<T> ArrayList<T> reverseList(List<T> list)
    {
        ArrayList<T> reverse = new ArrayList<>(list);
        Collections.reverse(reverse);
        return reverse;
    }

    public void showNameAfterId(String key)
    {
        String user_id = mAuth.getCurrentUser().getUid();
        reffs= FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    // System.out.println(ds.getKey());
                    // System.out.println(ds.getValue());
                    String namefriend =  String.valueOf(ds.getValue()   );
                    listoffriendname.add(namefriend);


                }
                showLoggedName();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reffs.addListenerForSingleValueEvent(valueEventListener);
    }
    public void showFriend()
    {
        String user_id = mAuth.getCurrentUser().getUid();
        reffs= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(user_id).child("friends");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String namefriend =  String.valueOf(ds.getKey());

                    listoffriendkey.add(namefriend);
                    showNameAfterId(namefriend);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reffs.addListenerForSingleValueEvent(valueEventListener);
    }


    public void showLoggedName()
    {
        String user_id = mAuth.getCurrentUser().getUid();
        reffname= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String lvlname = dataSnapshot.child("lvl").getValue(String.class);
                    listofName.add(name);
                    listofNameLvl.add(lvlname);
                    // imie po id

                }
                showTable();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;
        reffname.addListenerForSingleValueEvent(valueEventListener1);
    }


}
