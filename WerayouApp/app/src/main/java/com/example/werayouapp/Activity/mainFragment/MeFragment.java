package com.example.werayouapp.Activity.mainFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.werayouapp.Activity.SettingActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.adapter.PostAdapter;
import com.example.werayouapp.model.Cards;
import com.example.werayouapp.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {
    FirebaseAuth user ;
    String userSex;
    ImageView cardView2;
    private DatabaseReference usersDb;
    TextView nomUser;
    private String nom;
    TextView age;
    private String userAge;
    private String profileImageUrl;
    TextView cherche;
    TextView sexe;
    private String userID;
    private String prenom;
    ImageButton setupButton;
    ProgressBar progressBar;
    TextView paysView;
    private RecyclerView mRecyclerView;
    List<Post> postList;
    private RecyclerView.Adapter adapter;
    ProgressBar progressBarTwo;
    TextView aucun_post;
    TextView villeView;



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
        user=FirebaseAuth.getInstance();
        userID=user.getCurrentUser().getUid();
        sexe=v.findViewById(R.id.sexe);
        progressBarTwo=v.findViewById(R.id.progressBarTwo);
        mRecyclerView=v.findViewById(R.id.mRecyclerView);
        aucun_post=v.findViewById(R.id.aucun_post);
        paysView=v.findViewById(R.id.paysView);
        villeView=v.findViewById(R.id.villeView);
        //
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
       // mRecyclerView.addItemDecoration(new Grids(2, dpToPx(8), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        //
        progressBar=v.findViewById(R.id.progressBar);
        setupButton=v.findViewById(R.id.setupButton);
        //setupButton.setVisibility(View.INVISIBLE);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
                //getActivity().finish();
            }
        });
        postList=new ArrayList<>();
        getUserData();
        getPost();

       // FetchPost();
        return v;

    }

   //recupere tout ce que l'utilisateur a poste
   void getPost(){
       //adding an event listener to fetch values
       DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("posts");
       db.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot snapshot) {
               //iterating through all the values in database
               postList.clear();//vide la liste de la recyclrView pour eviter les doublons
               for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                   Post post = postSnapshot.getValue(Post.class);
                   postList.add(post);
                   progressBarTwo.setVisibility(View.INVISIBLE);
               }
               if (postList.size()==0){
                   aucun_post.setVisibility(View.VISIBLE);
                   progressBarTwo.setVisibility(View.INVISIBLE);
               }
               //creating adapter
               adapter = new PostAdapter(postList, getActivity());
               //adding adapter to recyclerview
               mRecyclerView.setAdapter(adapter);
              // adapter.notifyDataSetChanged();
           }
           @Override
           public void onCancelled(DatabaseError databaseError) {
           }
       });

   }


    ///recupere les information de l'utilisateur
    public void getUserData(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        data(dataSnapshot);
                        getPost();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    data(dataSnapshot);
                getPost();
                if (postList.size()==0){

                    aucun_post.setVisibility(View.VISIBLE);
                    progressBarTwo.setVisibility(View.INVISIBLE);
                }
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

    void data(DataSnapshot dataSnapshot){
        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
            if(map.get("nom")!=null){
                nom = map.get("nom").toString();

            }
            if(map.get("prenom")!=null){
                prenom = map.get("prenom").toString();
                String nomFinal = nom.substring(0, 1).toUpperCase() + nom.substring(1);
                String prenomFinal = prenom.substring(0, 1).toUpperCase() + prenom.substring(1);
                nomUser.setText(prenomFinal +" " + nomFinal);

            }
            if(map.get("age")!=null){
                userAge = map.get("age").toString();
                String ageFinal = userAge.substring(0, 1).toUpperCase() + userAge.substring(1);
                age.setText(ageFinal +" ans");
            }
            if(map.get("ville")!=null){
                String ville = map.get("ville").toString();
                String villeFinal = ville.substring(0, 1).toUpperCase() + ville.substring(1);
                villeView.setText(villeFinal);

            }
            if(map.get("image")!=null){
                profileImageUrl = map.get("image").toString();
                Picasso.with(getActivity()).load(profileImageUrl).into(cardView2);
                progressBar.setVisibility(View.INVISIBLE);
                setupButton.setVisibility(View.VISIBLE);
                setupButton.setAnimation ( AnimationUtils.loadAnimation ( getActivity(),R.anim.fade_scale ) );

            }
            //
            if(map.get("recherche")!=null){
                String recherche = map.get("recherche").toString();
                String rechercheFinal = recherche.substring(0, 1).toUpperCase() + recherche.substring(1);
                cherche.setText(rechercheFinal);



            }
            if(map.get("sexe")!=null){
                String userSexe = map.get("sexe").toString();
                sexe.setText(userSexe);

            }
            if(map.get("pays")!=null){
                String pays = map.get("pays").toString();
                String paysFinal = pays.substring(0, 1).toUpperCase() + pays.substring(1);
                paysView.setText(paysFinal);

            }
            //
        }

    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }



}
