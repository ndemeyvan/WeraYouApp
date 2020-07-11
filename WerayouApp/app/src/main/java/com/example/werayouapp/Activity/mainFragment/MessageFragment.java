package com.example.werayouapp.Activity.mainFragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.werayouapp.Activity.ChatActivity;
import com.example.werayouapp.Activity.DetailPhotoActivity;
import com.example.werayouapp.Activity.DetailPubActivity;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    TextView message;
    RecyclerView mRecyclerView;
    List<LastMessageModel> modelChatList;
    LastMessageChatAdapter chatAdapter;
    DatabaseReference reference;
    FirebaseAuth user;
    String userID;
    SharedPreferences sharedpreferences;
    String myPref = "firstOpen";
    private DatabaseReference usersDb;

    ProgressBar progressBar;
    ViewFlipper viewFlipper;
    ImageView image6, image5, image4, image3, image2, image1;
    TextView image6Text, image5Text, image4Text, image3Text, image2Text, image1Text;
    private String image1a;
    private boolean hasWebsite1;
    private String desc1;
    private String title1;
    private String websiteLink1;
    private String image2a;
    private boolean hasWebsite2;
    private String desc2;
    private String title2;
    private String websiteLink2;
    private String image3a;
    private boolean hasWebsite3;
    private String desc3;
    private String title3;
    private String websiteLink3;
    private String image4a;
    private boolean hasWebsite4;
    private String desc4;
    private String title4;
    private String websiteLink4;
    private boolean hasWebsite5;
    private String image5a;
    private String esc5;
    private String title5;
    private String websiteLink5;
    private String desc5;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        //
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        viewFlipper = v.findViewById(R.id.viewFlipper);
        viewFlipper.setOutAnimation(getActivity(), android.R.anim.slide_out_right);
        viewFlipper.setInAnimation(getActivity(), android.R.anim.slide_in_left);
        //
        message = v.findViewById(R.id.message);
        progressBar = v.findViewById(R.id.progressBar);
        //
        mRecyclerView = v.findViewById(R.id.recyclerview);
        mRecyclerView = v.findViewById(R.id.recyclerview);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        modelChatList = new ArrayList<>();

        //image
        image6 = v.findViewById(R.id.image6);
        image5 = v.findViewById(R.id.image5);
        image4 = v.findViewById(R.id.image4);
        image3 = v.findViewById(R.id.image3);
        image2 = v.findViewById(R.id.image2);
        image1 = v.findViewById(R.id.image1);
        //text
        image6Text = v.findViewById(R.id.image6Text);
        image5Text = v.findViewById(R.id.image5Text);
        image4Text = v.findViewById(R.id.image4Text);
        image3Text = v.findViewById(R.id.image3Text);
        image2Text = v.findViewById(R.id.image2Text);
        image1Text = v.findViewById(R.id.image1Text);
        //getLastMessage();
        getMessage();
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
        getImagePub();
        return v;
    }

    //affiche les message dans la base de dooneee
    void getMessage() {
        //adding an event listener to fetch values
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("dernier_message").child(userID).child("contacts");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //iterating through all the values in database
                modelChatList.clear();//vide la liste de la recyclrView pour eviter les doublons
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    LastMessageModel comment = postSnapshot.getValue(LastMessageModel.class);
                    modelChatList.add(comment);

                    progressBar.setVisibility(View.INVISIBLE);
                    message.setVisibility(View.INVISIBLE);
//                    Collections.reverse(modelChatList);

                }
                Collections.sort(modelChatList);
                if (modelChatList.size() == 0) {
                    message.setVisibility(View.VISIBLE);
                    message.setText(getResources().getString(R.string.no_conversation));
                    progressBar.setVisibility(View.INVISIBLE);
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

    void checkIfEmpty() {
        if (modelChatList.size() == 0) {
            message.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.INVISIBLE);

        } else {
            message.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

        }
    }

    ///recupere les images de publicite
    public void getImagePub() {
        usersDb = FirebaseDatabase.getInstance().getReference().child("Ads").child("lastMessage").child("image1");
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("image") != null) {
                        image1a = map.get("image").toString();
                        hasWebsite1 = (boolean) map.get("hasWebsite");
                        desc1 = map.get("desc").toString();
                        title1 = map.get("title").toString();
                        websiteLink1 = map.get("websiteLink").toString();
                        Picasso.with(getActivity()).load(image1a).into(image1);

                        image1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetailPubActivity.class);
                                intent.putExtra("image", image1a);
                                intent.putExtra("hasWebsite", hasWebsite1);
                                intent.putExtra("desc", desc1);
                                intent.putExtra("title", title1);
                                intent.putExtra("websiteLink", websiteLink1);
                                startActivity(intent);
                            }
                        });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        usersDb = FirebaseDatabase.getInstance().getReference().child("Ads").child("lastMessage").child("image2");
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Log.i("pub", "pub");
                    if (map.get("image") != null) {
                        image2a = map.get("image").toString();
                        hasWebsite2 = (boolean) map.get("hasWebsite");
                        desc2 = map.get("desc").toString();
                        title2 = map.get("title").toString();
                        websiteLink2 = map.get("websiteLink").toString();
                        Picasso.with(getActivity()).load(image2a).into(image2);
                        image2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetailPubActivity.class);
                                intent.putExtra("image", image2a);
                                intent.putExtra("hasWebsite", hasWebsite2);
                                intent.putExtra("desc", desc2);
                                intent.putExtra("title", title2);
                                intent.putExtra("websiteLink", websiteLink2);
                                startActivity(intent);


                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        usersDb = FirebaseDatabase.getInstance().getReference().child("Ads").child("lastMessage").child("image3");
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Log.i("pub", "pub");
                    if (map.get("image") != null) {
                        image3a = map.get("image").toString();
                        hasWebsite3 = (boolean) map.get("hasWebsite");
                        desc3 = map.get("desc").toString();
                        title3 = map.get("title").toString();
                        websiteLink3 = map.get("websiteLink").toString();
                        Picasso.with(getActivity()).load(image3a).into(image3);
                        image3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetailPubActivity.class);
                                intent.putExtra("image", image3a);
                                intent.putExtra("hasWebsite", hasWebsite3);
                                intent.putExtra("desc", desc3);
                                intent.putExtra("title", title3);
                                intent.putExtra("websiteLink", websiteLink3);
                                startActivity(intent);

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        usersDb = FirebaseDatabase.getInstance().getReference().child("Ads").child("lastMessage").child("image4");
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Log.i("pub", "pub");
                    if (map.get("image") != null) {
                        image4a = map.get("image").toString();
                        hasWebsite4 = (boolean) map.get("hasWebsite");
                        desc4 = map.get("desc").toString();
                        title4 = map.get("title").toString();
                        websiteLink4 = map.get("websiteLink").toString();
                        Picasso.with(getActivity()).load(image4a).into(image4);
                        image4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetailPubActivity.class);
                                intent.putExtra("image", image4a);
                                intent.putExtra("hasWebsite", hasWebsite4);
                                intent.putExtra("desc", desc4);
                                intent.putExtra("title", title4);
                                intent.putExtra("websiteLink", websiteLink4);
                                startActivity(intent);

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        usersDb = FirebaseDatabase.getInstance().getReference().child("Ads").child("lastMessage").child("image5");
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Log.i("pub", "pub");
                    if (map.get("image") != null) {
                        image5a = map.get("image").toString();
                        hasWebsite5 = (boolean) map.get("hasWebsite");
                        desc5 = map.get("desc").toString();
                        title5 = map.get("title").toString();
                        websiteLink5 = map.get("websiteLink").toString();
                        Picasso.with(getActivity()).load(image5a).into(image5);
                        image5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetailPubActivity.class);
                                intent.putExtra("image", image5a);
                                intent.putExtra("hasWebsite", hasWebsite5);
                                intent.putExtra("desc", desc5);
                                intent.putExtra("title", title5);
                                intent.putExtra("websiteLink", websiteLink5);
                                startActivity(intent);

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        usersDb = FirebaseDatabase.getInstance().getReference().child("Ads").child("lastMessage").child("image6");
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Log.i("pub", "pub");
                    if (map.get("image") != null) {
                        final String image = map.get("image").toString();
                        final boolean hasWebsite = (boolean) map.get("hasWebsite");
//                        Log.i("hasWebsite",""+hasWebsite);
                        final String desc = map.get("desc").toString();
                        final String title = map.get("title").toString();
                        final String websiteLink = map.get("websiteLink").toString();
                        Picasso.with(getActivity()).load(image).into(image6);
                        image6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetailPubActivity.class);
                                intent.putExtra("image", image);
                                intent.putExtra("hasWebsite", hasWebsite);
                                intent.putExtra("desc", desc);
                                intent.putExtra("title", title);
                                intent.putExtra("websiteLink", websiteLink);
                                startActivity(intent);
                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
