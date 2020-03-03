package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.example.werayouapp.adapter.PostAdapter;
import com.example.werayouapp.model.Cards;
import com.example.werayouapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {
    FirebaseAuth user;
    ImageView cardView2;
    private DatabaseReference usersDb;
    TextView nomUser;
    private String nom;
    TextView age;
    private String userAge;
    private String profileImageUrl;
    TextView cherche;
    TextView sexe;
    private String prenom;
    ProgressBar progressBar;
    TextView paysView;
    private RecyclerView mRecyclerView;
    List<Post> postList;
    private RecyclerView.Adapter adapter;
    ProgressBar progressBarTwo;
    TextView aucun_post;
    TextView villeView;
    private String id_user;
    Toolbar toolbar;
    private String currentUser;
    Button addButton;
    Button deniedButton;
    boolean isFriend;
    //
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        cardView2 = findViewById(R.id.cardView2);
        nomUser = findViewById(R.id.nomUser);
        age = findViewById(R.id.age);
        user = FirebaseAuth.getInstance();
        cherche = findViewById(R.id.cherche);
        user = FirebaseAuth.getInstance();
        currentUser = user.getCurrentUser().getUid();
        id_user = getIntent().getStringExtra("id");
        sexe = findViewById(R.id.sexe);
        progressBarTwo = findViewById(R.id.progressBarTwo);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        aucun_post = findViewById(R.id.aucun_post);
        paysView = findViewById(R.id.paysView);
        villeView = findViewById(R.id.villeView);
        toolbar = findViewById(R.id.toolbar);
        addButton = findViewById(R.id.addButton);
        deniedButton = findViewById(R.id.deniedButton);
        //
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        //
        //
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ProfilActivity.this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // mRecyclerView.addItemDecoration(new Grids(2, dpToPx(8), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        //
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
                finish();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        postList = new ArrayList<>();
        CheckifIsFriend();
        getUserData();
        getPost();
        //action d'ecrire ou accepter
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFriend == true) {
                    Intent intent = new Intent(ProfilActivity.this, ChatActivity.class);
                    intent.putExtra("id", id_user);
                    startActivity(intent);
                    //il faudra une condition pour verifier si l'utilisateur est bloque ou pas
                } else {
                    makeToast("vous pouvez accepter cette personne comme amies");
                    accpet();
                }
            }
        });
        //action de bloquer ou refuser
        deniedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFriend == false) {
                    //makeToast("vous n'etes pas amies vous pouvez refuser vous pouvez refuser sa demande");
                    reject();
                } else {
                    //makeToast("vous etes  amies et vous pouvez la bloquer");
                    // blockUser();
                }
            }
        });


    }

    //recupere tout ce que l'utilisateur a poste
    void getPost() {
        //adding an event listener to fetch values
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts");
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
                if (postList.size() == 0) {
                    aucun_post.setVisibility(View.VISIBLE);
                    progressBarTwo.setVisibility(View.INVISIBLE);
                }
                //creating adapter
                adapter = new PostAdapter(postList, ProfilActivity.this);
                //adding adapter to recyclerview
                mRecyclerView.setAdapter(adapter);
                // adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //refuser une demande
    void reject() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        final String date = saveCurrentDate;
        final Map<String, String> user_data = new HashMap<>();
        user_data.put("updatedDate", date);
        user_data.put("id", id_user);

        /*ici il est question d'ajouter un utilisateur ajouter de la collection de d'amies */
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("connections").child("refuser").child(id_user);
        db.setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                /*ici il est question de supprimer un utilisateur  de la collection de demande d'amies */
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("connections").child("accepter").child(id_user);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    void blockUser() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        final String date = saveCurrentDate;
        final Map<String, String> user_data = new HashMap<>();
        user_data.put("updatedDate", date);
        user_data.put("id", id_user);
        DatabaseReference boquer = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("connections").child("bloquer").child(id_user);
        boquer.setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                makeToast("bloquer");
            }
        });
    }

    ///recupere les information de l'utilisateur
    public void getUserData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
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

    void data(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
            if (map.get("nom") != null) {
                nom = map.get("nom").toString();

            }
            if (map.get("prenom") != null) {
                prenom = map.get("prenom").toString();
                String nomFinal = nom.substring(0, 1).toUpperCase() + nom.substring(1);
                String prenomFinal = prenom.substring(0, 1).toUpperCase() + prenom.substring(1);
                nomUser.setText(prenomFinal + " " + nomFinal);
                toolbar.setTitle(prenomFinal + " " + nomFinal);

            }
            if (map.get("age") != null) {
                userAge = map.get("age").toString();
                String ageFinal = userAge.substring(0, 1).toUpperCase() + userAge.substring(1);
                age.setText(ageFinal + " ans");
            }
            if (map.get("ville") != null) {
                String ville = map.get("ville").toString();
                String villeFinal = ville.substring(0, 1).toUpperCase() + ville.substring(1);
                villeView.setText(villeFinal);

            }
            if (map.get("image") != null) {
                profileImageUrl = map.get("image").toString();
                Picasso.with(ProfilActivity.this).load(profileImageUrl).into(cardView2);
                progressBar.setVisibility(View.INVISIBLE);

            }
            //
            if (map.get("recherche") != null) {
                String recherche = map.get("recherche").toString();
                String rechercheFinal = recherche.substring(0, 1).toUpperCase() + recherche.substring(1);
                cherche.setText(rechercheFinal);


            }
            if (map.get("sexe") != null) {
                String userSexe = map.get("sexe").toString();
                sexe.setText(userSexe);

            }
            if (map.get("pays") != null) {
                String pays = map.get("pays").toString();
                String paysFinal = pays.substring(0, 1).toUpperCase() + pays.substring(1);
                paysView.setText(paysFinal);

            }
            //
        }

    }


    void CheckifIsFriend() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("connections").child("mesAmis");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("mesAmis").hasChild(currentUser)) {
                    deniedButton.setText("Bloquer");
                    addButton.setText("Ecrire");
                    isFriend = true;
                } else {
                    isFriend = false;
                    deniedButton.setText("Refuser");
                    addButton.setText("Accepter");
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

    //accepter une demande
    void accpet() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        final String date = saveCurrentDate;
        Map<String, String> user_data = new HashMap<>();
        user_data.put("updatedDate", date);
        user_data.put("id", id_user);

        /*ici il est question d'ajouter un utilisateur ajouter de la collection de d'amies */
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("connections").child("mesAmis").child(id_user);
        db.setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                /*ici il est question de supprimer un utilisateur  de la collection de demande d'amies */
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("connections").child("accepter").child(id_user);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                ///mettre l'utilisateur dans les valider
                Map<String, String> user_data = new HashMap<>();
                user_data.put("updatedDate", date);
                user_data.put("id", id_user);
                /*ici il est question d'ajouter un utilisateur ajouter de la collection de d'amies */
                DatabaseReference db_ = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("connections").child("valider").child(id_user);
                db_.setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, String> dataForAsker = new HashMap<>();
                        dataForAsker.put("updatedDate", date);
                        dataForAsker.put("id", currentUser);
                        DatabaseReference dbTwoAskUser = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("connections").child("mesAmis").child(currentUser);
                        dbTwoAskUser.setValue(dataForAsker);
                    }
                });


            }
        });

    }

    void makeToast(String msg) {
        Toast.makeText(ProfilActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    void setStatus(String status){
        Map<String, Object> user_data = new HashMap<>();
        user_data.put("isOnline", status);
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        userDb.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Intent intent = new Intent(SettingActivity.this,ActivityPrincipal.class);
                //startActivity(intent);
                // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setStatus("online");
    }
}
