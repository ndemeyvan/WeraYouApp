package com.example.werayouapp.Activity.mainFragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.example.werayouapp.adapter.ArrayAdapter;
import com.example.werayouapp.model.Cards;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View v;
    Cards cards[];
    private com.example.werayouapp.adapter.ArrayAdapter arrayAdapter;
    private int i;
    private SwipeFlingAdapterView flingContainer;
    //String oppositeUserSex;
    FirebaseAuth user;
    ListView listView;
    List<Cards> rowsItems;
    ProgressBar progressBar;
    TextView messageDeDernierCards;
    DatabaseReference db;
    ImageView right;
    ImageView left;
    Cards obj;
    String countryName;
    //ProgressDialog dialog;
    //
    private String userSex;
    private String oppositeUserSex;
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
        v = inflater.inflate(R.layout.fragment_home2, container, false);
        Bundle bundle = getArguments();
        //nom du pays par defaut
         countryName = bundle.getString("country");
        user = FirebaseAuth.getInstance();
        currentUser = user.getCurrentUser().getUid();
        messageDeDernierCards = v.findViewById(R.id.messageDeDernierCards);
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        checkUserSex();
        right = v.findViewById(R.id.right);
        left = v.findViewById(R.id.leftButton);
        rowsItems = new ArrayList<Cards>();
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item, rowsItems);
        flingContainer = v.findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        progressBar = v.findViewById(R.id.progressBar);
        // dialog = ProgressDialog.show(getActivity(), "","Loading. Please wait...", true);

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
                obj = (Cards) o;
                String userId = obj.getId();
                //usersDb.child(userId).child("connections").child("refuser").child(currentUser).setValue(true);
                Map<String, String> data = new HashMap<>();
                data.put("id", currentUser);
                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("connections").child("refuser").child(currentUser);
                userDb.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                //makeToast(getActivity(), "left!");
            }

            @Override
            public void onRightCardExit(Object o) {
                obj = (Cards) o;
                String userId = obj.getId();
                Map<String, String> data = new HashMap<>();
                data.put("id", currentUser);
                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("connections").child("accepter").child(currentUser);
                userDb.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                //usersDb.child(userId).child("connections").child("accepter").child(currentUser).setValue(true);
                isConnectionMatch(userId);
                //makeToast(getActivity(), "Right!");

            }

            private void isConnectionMatch(String userId) {
                DatabaseReference currentUserConnectionsDb = usersDb.child(currentUser).child("connections").child("accepter").child(userId);
                currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getActivity(), "new Connection", Toast.LENGTH_LONG).show();
                            usersDb.child(dataSnapshot.getKey()).child("connections").child(currentUser).setValue(true);
                            usersDb.child(currentUser).child("connections").child(dataSnapshot.getKey()).setValue(true);
                            // String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                            //usersDb.child(dataSnapshot.getKey()).child("connections").child("correspondances").child(currentUser).child("ChatId").setValue(key);
                            //usersDb.child(currentUser).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                if (i <= 0) {
                    messageDeDernierCards.setVisibility(View.VISIBLE);
                    right.setEnabled(false);
                    left.setEnabled(false);
                } else {
                    messageDeDernierCards.setVisibility(View.INVISIBLE);
                    right.setEnabled(true);
                    left.setEnabled(true);
                }
                //makeToast(getActivity(), "plus de proposition !");
            }

            @Override
            public void onScroll(float v) {

            }
        });
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //makeToast(getContext(), "Clicked!");
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    flingContainer.getTopCardListener().selectRight();
                    String userId = obj.getId();
                    Map<String, String> data = new HashMap<>();
                    data.put("id", currentUser);
                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("connections").child("accepter").child(currentUser);
                    userDb.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    flingContainer.getTopCardListener().selectLeft();
                    String userId = obj.getId();
                    Map<String, String> data = new HashMap<>();
                    data.put("id", currentUser);
                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("connections").child("refuser").child(currentUser);
                    userDb.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });


        return v;
    }


    public void checkUserSex() {
        Log.i("currentUser", currentUser);
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("sexe").getValue() != null) {
                        userSex = dataSnapshot.child("sexe").getValue().toString();
                        Log.i("monSex", userSex);
                        switch (userSex) {
                            case "Homme":
                                oppositeUserSex = "Femme";
                                Log.i("Recherche", oppositeUserSex);
                                getOppositeSexUsers();
                                break;
                            case "Femme":
                                oppositeUserSex = "Homme";
                                Log.i("Recherche", oppositeUserSex);
                                getOppositeSexUsers();
                                break;
                            case "Les deux":
                                getTwoUsersSex();
                                break;
                        }
                        //getOppositeSexUsers();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getOppositeSexUsers() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("refuser").hasChild(currentUser) && !dataSnapshot.child("connections").child("accepter").hasChild(currentUser) && !dataSnapshot.child("connections").child("valider").hasChild(currentUser) && !dataSnapshot.child("connections").child("mesAmis").hasChild(currentUser) && dataSnapshot.child("sexe").getValue().toString().equals(oppositeUserSex)&& dataSnapshot.child("pays").getValue().toString().equals(countryName)) {
                    //
                    Cards item = new Cards(dataSnapshot.child("nom").getValue().toString(), dataSnapshot.child("prenom").getValue().toString(), dataSnapshot.child("image").getValue().toString(), dataSnapshot.child("id").getValue().toString(), dataSnapshot.child("pays").getValue().toString(), dataSnapshot.child("ville").getValue().toString(), dataSnapshot.child("apropos").getValue().toString(), dataSnapshot.child("age").getValue().toString());
                    progressBar.setVisibility(View.INVISIBLE);
                    rowsItems.add(item);
                    //dialog.dismiss();
                    arrayAdapter.notifyDataSetChanged();
                    //
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //pas vraiment neccesaire pour l'instant
                //rowsItems.clear();
                // getOppositeSexUsers();
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
        //
        if (rowsItems.size() <= 0) {
            //dialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void getTwoUsersSex() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("refuser").hasChild(currentUser) && !dataSnapshot.child("connections").child("accepter").hasChild(currentUser) && !dataSnapshot.child("connections").child("valider").hasChild(currentUser) && !dataSnapshot.child("connections").child("mesAmis").hasChild(currentUser) && dataSnapshot.child("pays").getValue().toString().equals(countryName)) {
                    //
                    Cards item = new Cards(dataSnapshot.child("nom").getValue().toString(), dataSnapshot.child("prenom").getValue().toString(), dataSnapshot.child("image").getValue().toString(), dataSnapshot.child("id").getValue().toString(), dataSnapshot.child("pays").getValue().toString(), dataSnapshot.child("ville").getValue().toString(), dataSnapshot.child("apropos").getValue().toString(), dataSnapshot.child("age").getValue().toString());
                    progressBar.setVisibility(View.INVISIBLE);
                    rowsItems.add(item);
                    //dialog.dismiss();
                    arrayAdapter.notifyDataSetChanged();
                    //
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //pas vraiment neccesaire pour l'instant
                //rowsItems.clear();
                // getOppositeSexUsers();
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
        //
        if (rowsItems.size() <= 0) {
            //dialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    static void makeToast(Context ctx, String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }


}
