package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.werayouapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPhotoActivity extends AppCompatActivity {
    String id_post;
    String id_user;
    String description;
    String image;
    String date;
    //
    CircleImageView profil_image;
    TextView nom_profil;
    TextView date_publication;
    TextView description_view;
    ImageView imageView;
    ImageView like_image;
    TextView like_count;
    TextView comment_count;
    RecyclerView recyclerView;
    EditText comment_edittext;
    ImageButton send_comment_button;
    String nom;
    DatabaseReference usersDb;
    ProgressBar progressBar;
    Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);
        id_post=getIntent().getStringExtra("id_post");
        id_user=getIntent().getStringExtra("id_user");
        description=getIntent().getStringExtra("description");
        image=getIntent().getStringExtra("image");
        date=getIntent().getStringExtra("date");
        //
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        profil_image=findViewById(R.id.profil_image);
        nom_profil=findViewById(R.id.nom_profil);
        date_publication=findViewById(R.id.date_publication);
        description_view=findViewById(R.id.description_view);
        imageView=findViewById(R.id.image);
        like_image=findViewById(R.id.like_image);
        like_count=findViewById(R.id.like_count);
        comment_count=findViewById(R.id.comment_count);
        progressBar=findViewById(R.id.progressBar);
        // set les extras recuperez
        description_view.setText(description);
        Picasso.with(DetailPhotoActivity.this).load(image).into(imageView);
        date_publication.setText(date);
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserData();


    }

    public void getUserData(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
                usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if(map.get("nom")!=null){
                                nom = map.get("nom").toString();
                            }
                            if(map.get("prenom")!=null){
                                String prenom = map.get("prenom").toString();
                                nom_profil.setText(nom +" " + prenom);
                                toolbar.setTitle(nom);

                            }
                            if(map.get("image")!=null){
                                String profileImageUrl = map.get("image").toString();
                                Picasso.with(DetailPhotoActivity.this).load(profileImageUrl).placeholder(R.drawable.ic_launcher_background).into(profil_image);
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

       /* //femme
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
        });*/
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
