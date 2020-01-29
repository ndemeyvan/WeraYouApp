package com.example.werayouapp.Activity.mainFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.werayouapp.Activity.ChatActivity;
import com.example.werayouapp.Activity.DetailPhotoActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.UtilsForChat.ChatAdapter;
import com.example.werayouapp.UtilsForChat.ModelChat;
import com.example.werayouapp.adapter.CommentAdapter;
import com.example.werayouapp.adapter.LastMessageChatAdapter;
import com.example.werayouapp.model.CommentModel;
import com.example.werayouapp.model.LastMessageModel;
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
public class MessageFragment extends Fragment {
    TextView message;
    RecyclerView mRecyclerView;
    List<LastMessageModel> modelChatList;
    LastMessageChatAdapter chatAdapter;
    DatabaseReference reference;
    FirebaseAuth user ;
    String userID;


    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        //
        user= FirebaseAuth.getInstance();
        userID=user.getCurrentUser().getUid();
        //
        message=v.findViewById(R.id.message);
        //
        mRecyclerView=v.findViewById(R.id.recyclerview);
        mRecyclerView=v.findViewById(R.id.recyclerview);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        modelChatList=new ArrayList<>();

        //getLastMessage();
        getMessage();

        return v;
    }

    //affiche les message dans la base de dooneee
    public void getLastMessage(){
        // modelChatList.clear();
        modelChatList=new ArrayList<>(  );
        reference= FirebaseDatabase.getInstance ().getReference ("dernier_message").child(userID).child("contacts");
        reference.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren ()){
                    LastMessageModel chat = snapshot.getValue (LastMessageModel.class);
                    modelChatList.add ( chat );
                    chatAdapter=new LastMessageChatAdapter(modelChatList,getActivity());
                    mRecyclerView.setAdapter ( chatAdapter );
                    chatAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );



    }


    //recupere tout ce que l'utilisateur a poste
    void getMessage(){
        //adding an event listener to fetch values
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("dernier_message").child(userID).child("contacts");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //iterating through all the values in database
                //modelChatList.clear();//vide la liste de la recyclrView pour eviter les doublons
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    LastMessageModel comment = postSnapshot.getValue(LastMessageModel.class);
                    modelChatList.add(comment);
                    //aucun_commentaires.setVisibility(View.INVISIBLE);
                }

               // commentNumber=commentList.size();

                //creating adapter
                chatAdapter = new LastMessageChatAdapter(modelChatList, getActivity());
                //adding adapter to recyclerview
                mRecyclerView.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


}
