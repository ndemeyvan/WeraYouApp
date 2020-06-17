package com.example.werayouapp.Activity.mainFragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.werayouapp.R;
import com.example.werayouapp.adapter.AddFriendsAdapteur;
import com.example.werayouapp.adapter.MyFriendAdapter;
import com.example.werayouapp.model.FriendsModel;
import com.example.werayouapp.model.MyFriendModel;
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
public class MyFriendFragment extends Fragment {
    RecyclerView mRecyclerView;
    List<MyFriendModel> friendsModelList;
    MyFriendAdapter adapter;
    String userID;
    FirebaseAuth user;
    TextView message;
    SharedPreferences sharedpreferences;
    String myPref = "firstOpen";
    ProgressBar progressBar;

    public MyFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_amies, container, false);
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        progressBar=v.findViewById(R.id.progressBar);
        message = v.findViewById(R.id.message);
        mRecyclerView = v.findViewById(R.id.recyclerview);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ///
        friendsModelList = new ArrayList<>();

        sharedpreferences = getActivity().getSharedPreferences(myPref,
                Context.MODE_PRIVATE);
        if (!sharedpreferences.contains(myPref)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Weareyou")
                    .setMessage(getResources().getString(R.string.close_app))
                    .setCancelable(false)
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean(myPref, true);
                            editor.commit();
                            getActivity().finishAffinity();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        getAsk();
        return v;
    }

    //recupere toutes les amies
    void getAsk() {
        //adding an event listener to fetch values
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("mesAmis");
        db.orderByChild("prenom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //iterating through all the values in database
                friendsModelList.clear();//vide la liste de la recyclrView pour eviter les doublons
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MyFriendModel ask = postSnapshot.getValue(MyFriendModel.class);
                    friendsModelList.add(ask);

                    progressBar.setVisibility(View.INVISIBLE);
                    message.setVisibility(View.INVISIBLE);
                }
                if (friendsModelList.size() == 0) {
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.no_proposition));
                    progressBar.setVisibility(View.INVISIBLE);
                }
                //creating adapter
                adapter = new MyFriendAdapter(friendsModelList, getActivity());
                //adding adapter to recyclerview
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                // adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


}
