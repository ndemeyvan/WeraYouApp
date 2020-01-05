package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.example.werayouapp.adapter.CommentAdapter;
import com.example.werayouapp.adapter.PostAdapter;
import com.example.werayouapp.model.CommentModel;
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
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPhotoActivity extends AppCompatActivity {
    String id_post;
    String id_user;
    String description;
    String image;
    String date;


    //
    FirebaseAuth user ;
    String userID;
    //
    CircleImageView profil_image;
    TextView nom_profil;
    TextView date_publication;
    TextView description_view;
    ImageView imageView;
    TextView likecommentsNumbers;
    TextView aucun_commentaires;
    RecyclerView mRecyclerView;
    List<CommentModel> commentList;
    RecyclerView.Adapter adapter;
    EditText comment_edittext;
    ImageButton send_comment_button;
    String nom;
    DatabaseReference usersDb;
    ProgressBar progressBar;
    Toolbar toolbar;
    ProgressBar progressBar2;
    ViewGroup rootView;
    ImageView like_icon;
    long likeNumber;
    long commentNumber;
    boolean islike;




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
        //
        user=FirebaseAuth.getInstance();
        userID=user.getCurrentUser().getUid();
        //
        //
        //
        rootView = findViewById(R.id.root_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        profil_image=findViewById(R.id.profil_image);
        nom_profil=findViewById(R.id.nom_profil);
        date_publication=findViewById(R.id.date_publication);
        description_view=findViewById(R.id.description_view);
        imageView=findViewById(R.id.image);
        like_icon=findViewById(R.id.like_icon);
        progressBar=findViewById(R.id.progressBar);
        send_comment_button=findViewById(R.id.send_comment_button);
        aucun_commentaires=findViewById(R.id.aucun_commentaires);
        progressBar2=findViewById(R.id.progressBar2);
        likecommentsNumbers=findViewById(R.id.likecommentsNumbers);
        //EmojiManager.install(new GoogleEmojiProvider());
        comment_edittext=findViewById(R.id.comment_edittext);
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
        sendComment();
        getUserData();
        mRecyclerView=findViewById(R.id.recyclerView);
        //
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // mRecyclerView.addItemDecoration(new Grids(2, dpToPx(8), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        commentList=new ArrayList<>();
        getComments();
        //
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        getLikeCount();
        checkifLike();
        like_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLike();
            }
        });
    }

    void getLikeCount(){
        final DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes");
        like.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    Log.e("nombreLike",snap.getChildrenCount() + "");
                    likeNumber=snap.getChildrenCount();
                    likecommentsNumbers.setText((likeNumber-1) +" Like(s) , " + commentNumber + " Commentaires");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // ajout le like dans la bd
    void setLike(){
        if (islike==false){
            //String key = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes").push().getKey();
            Calendar calendar=Calendar.getInstance ();
            SimpleDateFormat currentDate=new SimpleDateFormat (" dd MMM yyyy" );
            String saveCurrentDate=currentDate.format ( calendar.getTime () );
            String date=saveCurrentDate;
            Map<String, Object> comment_data = new HashMap<>();
            comment_data.put ( "id",user.getUid());
            comment_data.put ( "createdDate",date);
           // comment_data.put ( "id_like",key);
            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes").child(userID);
            userDb.setValue(comment_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    like_icon.setImageResource(R.drawable.ic_heart_like);
                    islike=true;

                }
            });

        }else{

            //remove value
            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes").child(userID);
            userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        like_icon.setImageResource(R.drawable.ic_heart_empty);
                        islike=false;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            //remove value


        }
    }
    // cherche a savoir si l'utilisateur a actuel a deja likez
    void checkifLike(){
        DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes");
        like.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChild(userID)){
                    islike=true;
                    like_icon.setImageResource(R.drawable.ic_heart_empty);

                }else{
                    like_icon.setImageResource(R.drawable.ic_heart_like);

                    islike=false;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    }
    //recupere tout ce que l'utilisateur a poste
    void getComments(){
        //adding an event listener to fetch values
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("posts").child(id_post).child("commentaires");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //iterating through all the values in database
                commentList.clear();//vide la liste de la recyclrView pour eviter les doublons
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    CommentModel comment = postSnapshot.getValue(CommentModel.class);
                    commentList.add(comment);
                    aucun_commentaires.setVisibility(View.INVISIBLE);
                }
                Log.e("size",commentList.size()+"");
                commentNumber=commentList.size();
                likecommentsNumbers.setText(likeNumber +" Like(s) , " + commentNumber + " Commentaires");

                //creating adapter
                adapter = new CommentAdapter(commentList, DetailPhotoActivity.this);
                //adding adapter to recyclerview
                mRecyclerView.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    //
    public void getUserData(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("data");
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
                                toolbar.setTitle("Publication de "+nom + " " + prenom);
                            }
                            if(map.get("image")!=null){
                                String profileImageUrl = map.get("image").toString();
                                Picasso.with(DetailPhotoActivity.this).load(profileImageUrl).placeholder(R.drawable.ic_launcher_background).into(profil_image);
                                profil_image.setAnimation ( AnimationUtils.loadAnimation ( DetailPhotoActivity.this,R.anim.fade_scale ) );

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


    }
    //
    public void sendComment(){
        send_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentaire = comment_edittext.getText().toString();
                String key = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("posts").child("posts").child(id_post).child("commentaires").push().getKey();

                if (!TextUtils.isEmpty ( commentaire )){
                    send_comment_button.setVisibility(View.INVISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    Calendar calendar=Calendar.getInstance ();
                    SimpleDateFormat currentDate=new SimpleDateFormat (" dd MMM yyyy" );
                    String saveCurrentDate=currentDate.format ( calendar.getTime () );
                    String date=saveCurrentDate;
                    Map<String, Object> comment_data = new HashMap<>();
                    comment_data.put ( "id",user.getUid());
                    comment_data.put ( "commentaire",commentaire);
                    comment_data.put ( "createdDate",date);
                    comment_data.put ( "id_commentaire",key);

                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("posts").child(id_post).child("commentaires").child(key);
                    userDb.setValue(comment_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           // makeToast("enregister",DetailPhotoActivity.this);
                            comment_edittext.setText("");
                            send_comment_button.setVisibility(View.VISIBLE);
                            progressBar2.setVisibility(View.INVISIBLE);

                        }
                    });


                }else{
                    makeToast("entrez un texte",DetailPhotoActivity.this);

                }
            }
        });
    }
    //
    void makeToast(String msg , Context context){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
    //
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
