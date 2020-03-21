package com.example.werayouapp.Activity.mainFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.werayouapp.Activity.ActivityPrincipal;
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
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment  {
    View v;
    Cards cards[];
    private com.example.werayouapp.adapter.ArrayAdapter arrayAdapter;
    private int i;
    private SwipeFlingAdapterView flingContainer;
    SharedPreferences sharedpreferences;

    FirebaseAuth user;
    List<Cards> rowsItems;
    ProgressBar progressBar;
    TextView messageDeDernierCards;
    DatabaseReference db;
    ImageView right;
    ImageView left;
    Cards obj;
    String contry;
    String myPref="countryPref";
    //
    private String recherche;
    //
    private String currentUser;

    //
    private DatabaseReference usersDb;
    private String pays;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_home2, container, false);
        sharedpreferences = getActivity().getSharedPreferences(myPref,
                Context.MODE_PRIVATE);

        user = FirebaseAuth.getInstance();
        currentUser = user.getCurrentUser().getUid();
        messageDeDernierCards = v.findViewById(R.id.messageDeDernierCards);
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        if (sharedpreferences.contains("lastCountrySave")) {
            contry=sharedpreferences.getString("lastCountrySave", "");
            checkUserSex(contry);
        }else{
            db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("pays").getValue() != null) {
                           String  myCountry = dataSnapshot.child("pays").getValue().toString();
                            checkUserSex(myCountry);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


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
                if (i == 0) {
                    messageDeDernierCards.setText("il n'y a plus de proposition ");
                    messageDeDernierCards.setVisibility(View.VISIBLE);
                    right.setEnabled(false);
                    left.setEnabled(false);
                } else {
                    messageDeDernierCards.setText("il n'y a plus de proposition " );
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



        ((ActivityPrincipal) getActivity()).passVal(new ActivityPrincipal.FragmentCommunicator() {
            @Override
            public void passData(String name) {
                Toast.makeText(getContext(), name.toLowerCase(), Toast.LENGTH_SHORT).show();
                contry=name.toLowerCase();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("lastCountrySave", contry);
                editor.commit();
                Intent i = new Intent(getContext(), ActivityPrincipal.class);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
                setStatus("online");
            }
        });

        showCase();


        return v;
    }

    void showCase(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "HomeFragment");
        sequence.setConfig(config);
        sequence.addSequenceItem(left,
                "Si un profil ne vous intéresse pas, cliquez sur le CROIX en bas de l'image ou faites glisser l'image vers la gauche", "OK");

        sequence.addSequenceItem(right,
                "Si un profil vous intéresse, cliquez sur le COEUR en bas de l'image ou faites glisser l'image vers la droite, l'utilisateur reçoit votre demande d'ami . ", "OK");

        sequence.start();


    }


    void setStatus(String status){
        Map<String, Object> user_data = new HashMap<>();
        user_data.put("isOnline", status);
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        userDb.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Intent intent = new Intent(SettingActivity.this,ActivityPrincipal.class);
                //startActivity(intent);
                // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);


            }
        });
    }



    public void checkUserSex(final String contry) {
        Log.i("currentUser", currentUser);
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("recherche").getValue() != null) {
                        recherche = dataSnapshot.child("recherche").getValue().toString();
                        switch (recherche) {
                            case "Homme":
                                getOppositeSexUsers(contry,recherche);
                                break;
                            case "Femme":
                                getOppositeSexUsers(contry,recherche);
                                break;
                            case "Les deux":
                                getTwoUsersSex(contry);
                                break;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeSexUsers(final String contry, final String oppositeUserSex) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("refuser").hasChild(currentUser) && !dataSnapshot.child("connections").child("accepter").hasChild(currentUser) && !dataSnapshot.child("connections").child("valider").hasChild(currentUser) && !dataSnapshot.child("connections").child("mesAmis").hasChild(currentUser) && dataSnapshot.child("sexe").getValue().toString().equals(oppositeUserSex)&& dataSnapshot.child("pays").getValue().toString().equals(contry)&& !dataSnapshot.child("id").getValue().toString().equals(currentUser)) {
                    //
                    Cards item = new Cards(dataSnapshot.child("nom").getValue().toString(), dataSnapshot.child("prenom").getValue().toString(), dataSnapshot.child("image").getValue().toString(), dataSnapshot.child("id").getValue().toString(), dataSnapshot.child("pays").getValue().toString(), dataSnapshot.child("ville").getValue().toString(), dataSnapshot.child("apropos").getValue().toString(), dataSnapshot.child("age").getValue().toString());
                    progressBar.setVisibility(View.INVISIBLE);
                    rowsItems.add(item);
                    //dialog.dismiss();
                    arrayAdapter.notifyDataSetChanged();
                    //
                }
                if (rowsItems.size() == 0) {
                    messageDeDernierCards.setText("il n'y a pas de proposition ");
                    messageDeDernierCards.setVisibility(View.VISIBLE);
                    right.setEnabled(false);
                    left.setEnabled(false);
                } else {
                    messageDeDernierCards.setText("il n'y a pas de proposition " );
                    messageDeDernierCards.setVisibility(View.INVISIBLE);
                    right.setEnabled(true);
                    left.setEnabled(true);
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

    public void getTwoUsersSex(final String contry) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("refuser").hasChild(currentUser) && !dataSnapshot.child("connections").child("accepter").hasChild(currentUser) && !dataSnapshot.child("connections").child("valider").hasChild(currentUser) && !dataSnapshot.child("connections").child("mesAmis").hasChild(currentUser)&& dataSnapshot.child("pays").getValue().toString().equals(contry)&&!dataSnapshot.child("id").getValue().toString().equals(currentUser)) {
                    //
                    Cards item = new Cards(dataSnapshot.child("nom").getValue().toString(), dataSnapshot.child("prenom").getValue().toString(), dataSnapshot.child("image").getValue().toString(), dataSnapshot.child("id").getValue().toString(), dataSnapshot.child("pays").getValue().toString(), dataSnapshot.child("ville").getValue().toString(), dataSnapshot.child("apropos").getValue().toString(), dataSnapshot.child("age").getValue().toString());
                    progressBar.setVisibility(View.INVISIBLE);
                    rowsItems.add(item);
                    //dialog.dismiss();
                    arrayAdapter.notifyDataSetChanged();
                    //
                }
                if (rowsItems.size() == 0) {
                    messageDeDernierCards.setText("il n'y a pas de proposition ");
                    messageDeDernierCards.setVisibility(View.VISIBLE);
                    right.setEnabled(false);
                    left.setEnabled(false);
                } else {
                    messageDeDernierCards.setText("il n'y a pas de proposition " );
                    messageDeDernierCards.setVisibility(View.INVISIBLE);
                    right.setEnabled(true);
                    left.setEnabled(true);
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

    ///recupere les information de l'utilisateur
//    public void getUserData() {
//        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
//        db.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
//                usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        data(dataSnapshot);
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//
//    }
//
//    void data(DataSnapshot dataSnapshot) {
//        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//            if (map.get("pays") != null) {
//                 contry = map.get("pays").toString();
//                 checkUserSex(contry);
//            }
//            //
//        }
//    }



}
