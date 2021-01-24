package com.example.challenge;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.challenge.MyAdapter.customButtonListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;

public class    FragmentMyFriends extends Fragment  {
    private DatabaseReference reff,reffs,reffsrequest,reffsktoname,reffsdelete;
    private FirebaseAuth mAuth;
    private FragmentMyFriendsListener listener;
    private EditText e1,e2;
    private ListView l1,l2;
    private List<String>listofkey=new ArrayList<>();
    private List<String>listofkeyrequest=new ArrayList<>();
    private List<String>listofuserspended=new ArrayList<>();
    private List<String>listofkeyspended=new ArrayList<>();
    private List<String>listofkeyfriend=new ArrayList<>();
    private ArrayList<SingleRow>mylist;
    private  ArrayList<SingleRow>pendlist;
    private MyAdapter myAdapterPend;
    private AdapterDelete myAdapter;
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
                    keytopoints(listofkeyfriend.get(i));
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

    public void keytoname(final String key,final String points)
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
                    lvl+=" ("+points+" pkt)";
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
    public void keytopoints(final String key)
    {

        String user_id = mAuth.getCurrentUser().getUid();
        reffsktoname= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child("Historia").child(key);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>newlist=new ArrayList<String>();
                String points =  String.valueOf(dataSnapshot.child("punkty").getValue());
                System.out.println("punkty"+points);
                keytoname(key,points);
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