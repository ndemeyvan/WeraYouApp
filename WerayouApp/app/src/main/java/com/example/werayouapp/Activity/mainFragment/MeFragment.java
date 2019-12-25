package com.example.werayouapp.Activity.mainFragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.example.werayouapp.model.Cards;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {
    FirebaseAuth user ;
    String userSex;
    String oppositeUserSex;
    CircleImageView cardView2;
    private DatabaseReference usersDb;
    TextView nomUser;
    private String nom;
    TextView age;
    private String userAge;
    private String profileImageUrl;
    TextView cherche;
    TextView sexe;
    private String prenom;


    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_me, container, false);
        cardView2=v.findViewById(R.id.cardView2);
        nomUser=v.findViewById(R.id.nomUser);
        age=v.findViewById(R.id.age);
        user=FirebaseAuth.getInstance();
        cherche=v.findViewById(R.id.cherche);
        sexe=v.findViewById(R.id.sexe);
        checkUserSex();
        getUserInfo();
        return v;

    }

    private void getUserInfo() {


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
                    usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(user.getUid());
                    usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                if(map.get("nom")!=null){
                                    nom = map.get("nom").toString();
                                    makeToast(getActivity(),nom);

                                }
                                if(map.get("prenom")!=null){
                                    prenom = map.get("prenom").toString();
                                    nomUser.setText(nom +" " + prenom);

                                }
                                if(map.get("age")!=null){
                                    userAge = map.get("age").toString();
                                    age.setText(userAge +" ans");
                                }
                                if(map.get("ville")!=null){
                                    userSex = map.get("ville").toString();
                                }
                                if(map.get("image")!=null){
                                    profileImageUrl = map.get("image").toString();
                                    Picasso.with(getActivity()).load(profileImageUrl).placeholder(R.drawable.logo).into(cardView2);

                                }
                                //
                                if(map.get("recherche")!=null){
                                    String recherche = map.get("recherche").toString();
                                    cherche.setText(recherche);

                                }
                                if(map.get("sexe")!=null){
                                    String userSexe = map.get("sexe").toString();
                                    sexe.setText(userSexe);

                                }
                                //
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
                    usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(user.getUid());
                    usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                if(map.get("nom")!=null){
                                    nom = map.get("nom").toString();

                                    makeToast(getActivity(),nom);
                                }
                                if(map.get("prenom")!=null){
                                     prenom = map.get("prenom").toString();
                                    nomUser.setText(nom +" " + prenom);

                                }
                                if(map.get("age")!=null){
                                    userAge = map.get("age").toString();
                                    age.setText(userAge +" ans");
                                }
                                if(map.get("ville")!=null){
                                    userSex = map.get("ville").toString();
                                }
                                if(map.get("image")!=null){
                                    profileImageUrl = map.get("image").toString();
                                    Picasso.with(getActivity()).load(profileImageUrl).placeholder(R.drawable.logo).into(cardView2);

                                }
                                //
                                if(map.get("recherche")!=null){
                                    String recherche = map.get("recherche").toString();
                                    cherche.setText(recherche);

                                }
                                if(map.get("sexe")!=null){
                                    String userSexe = map.get("sexe").toString();
                                    sexe.setText(userSexe);

                                }
                                //
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

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

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }



}
