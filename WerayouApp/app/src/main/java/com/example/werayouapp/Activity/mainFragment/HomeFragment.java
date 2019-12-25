package com.example.werayouapp.Activity.mainFragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.example.werayouapp.adapter.ArrayAdapter;
import com.example.werayouapp.model.Cards;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.FlingCardListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View v;
    Cards cards[];
    private com.example.werayouapp.adapter.ArrayAdapter arrayAdapter;
    private int i;
    private SwipeFlingAdapterView flingContainer;
    String userSex;
    String oppositeUserSex;
    FirebaseAuth user ;
    ListView listView;
    List<Cards> rowsItems;
    ProgressBar progressBar;
    //
    private String currentUser;

    //
    private DatabaseReference usersDb;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_home2, container, false);
        user=FirebaseAuth.getInstance();
        currentUser=user.getCurrentUser().getUid();

        checkUserSex();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        rowsItems = new ArrayList<Cards>();
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item, rowsItems);
        flingContainer=v.findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        progressBar=v.findViewById(R.id.progressBar);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowsItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Cards obj = (Cards) o;
                String userId = obj.getId();
                usersDb.child(oppositeUserSex).child(userId).child("connections").child("refuser").child(currentUser).setValue(true);
                makeToast(getActivity(), "left!");
            }

            @Override
            public void onRightCardExit(Object o) {
                Cards obj = (Cards) o;
                String userId = obj.getId();
                usersDb.child(oppositeUserSex).child(userId).child("connections").child("accepter").child(currentUser).setValue(true);
                isConnectionMatch(userId);
                makeToast(getActivity(), "Right!");

            }

            private void isConnectionMatch(String userId) {
                DatabaseReference currentUserConnectionsDb = usersDb.child(userSex).child(currentUser).child("connections").child("accepter").child(userId);
                currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Toast.makeText(getActivity(), "new Connection", Toast.LENGTH_LONG).show();
                            usersDb.child(oppositeUserSex).child(dataSnapshot.getKey()).child("correspondances").child(currentUser).setValue(true);
                            usersDb.child(userSex).child(currentUser).child("correspondances").child(dataSnapshot.getKey()).setValue(true);


                            // String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                           // usersDb.child(dataSnapshot.getKey()).child("connections").child("correspondances").child(currentUser).child("ChatId").setValue(key);
                           // usersDb.child(currentUser).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                // Ask for more data here
               // al.add("XML ".concat(String.valueOf(i)));
                /*arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");

                i++;*/
                //makeToast(getActivity(), "plus de proposition !");
            }

            @Override
            public void onScroll(float v) {

                }
        });
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(getContext(), "Clicked!");
            }
        });

        return v;
    }

    public void checkUserSex(){
        //homme
        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Homme");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals(user.getUid())){
                    userSex="Homme";
                    oppositeUserSex="Femme";
                    getOppositeSexUsers();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //femme
        DatabaseReference femmeDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Femme");
        femmeDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals(user.getUid())){
                    userSex="Femme";
                    oppositeUserSex="Homme";
                    getOppositeSexUsers();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getOppositeSexUsers(){
        DatabaseReference oppositeDb = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex);
        oppositeDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("refuser").hasChild(currentUser)&& !dataSnapshot.child("connections").child("accepter").hasChild(currentUser)) {
                    Cards item = new Cards(dataSnapshot.child("nom").getValue().toString(),dataSnapshot.child("prenom").getValue().toString(),dataSnapshot.child("image").getValue().toString(),dataSnapshot.child("id").getValue().toString(),dataSnapshot.child("pays").getValue().toString(),dataSnapshot.child("ville").getValue().toString());
                    rowsItems.add(item);
                    progressBar.setVisibility(View.INVISIBLE);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }


}
