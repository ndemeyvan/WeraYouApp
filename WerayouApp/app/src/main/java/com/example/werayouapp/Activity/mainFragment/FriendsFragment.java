package com.example.werayouapp.Activity.mainFragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.werayouapp.R;
import com.example.werayouapp.adapter.AddFriendsAdapteur;
import com.example.werayouapp.model.FriendsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    List<FriendsModel> friendsModelList;
    private RecyclerView.Adapter adapter;
    private String userID;
    FirebaseAuth user;
    TextView message;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        message = v.findViewById(R.id.message);
        // mRecyclerView.addItemDecoration(new Grids(2, dpToPx(8), true));
        ///
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // mRecyclerView.addItemDecoration(new Grids(2, dpToPx(8), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ///
        friendsModelList = new ArrayList<>();
        getAsk();
        checkIfEmpty();

        return v;
    }


    //recupere tout ce que l'utilisateur a poste
    void getAsk() {
        //adding an event listener to fetch values
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("accepter");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //iterating through all the values in database

                friendsModelList.clear();//vide la liste de la recyclrView pour eviter les doublons
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FriendsModel ask = postSnapshot.getValue(FriendsModel.class);
                    friendsModelList.add(ask);
                    checkIfEmpty();
                    //progressBar.setVisibility(View.INVISIBLE);
                }

                //creating adapter
                adapter = new AddFriendsAdapteur(friendsModelList, getActivity());
                //adding adapter to recyclerview
                mRecyclerView.setAdapter(adapter);
                // adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    void checkIfEmpty() {
        if (friendsModelList.size() <= 0) {
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.INVISIBLE);
        }
    }


}
