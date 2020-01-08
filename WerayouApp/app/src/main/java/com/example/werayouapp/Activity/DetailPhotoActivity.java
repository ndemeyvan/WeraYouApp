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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

public class DetailPhotoActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
               // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                //deletePost();
                return true;
            case R.id.edit:
                editDesc();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void deletePost(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("posts").child(id_post);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("info", "impossible de supprimer ", databaseError.toException());
            }
        });
    }

    void editDesc(){
        ///
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailPhotoActivity.this);
        View parientView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout,null);
        bottomSheetDialog.setContentView(parientView);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View)parientView.getParent());
        //bottomSheetBehavior.setPeekHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics()));
        bottomSheetBehavior.setPeekHeight(410);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        bottomSheetDialog.show();
        final ProgressBar progressBar3= parientView.findViewById(R.id.progressBar3);

        /*ImageView close_bottom_sheet=parientView.findViewById(R.id.close_bottom_sheet);
        final EditText post_detail_comment=parientView.findViewById(R.id.post_detail_comment);
        final Button post_detail_add_comment_btn=parientView.findViewById(R.id.post_detail_add_comment_btn);
        final TextView comment_empty_text=parientView.findViewById(R.id.comment_empty_text);
        RecyclerView rv_comment=parientView.findViewById(R.id.rv_comment);
        commentaires_modelList=new ArrayList<>();
        commentaire_adapter=new Commentaire_Adapter(commentaires_modelList,DetailActivity.this);
        rv_comment.setAdapter(commentaire_adapter);
        rv_comment.setLayoutManager(new LinearLayoutManager(DetailActivity.this,LinearLayoutManager.VERTICAL,false));
        firebaseFirestore.collection ( "publication" ).document ("categories").collection (categories ).document (iddupost).collection("commentaires").addSnapshotListener ( DetailActivity.this,new EventListener<QuerySnapshot> () {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.isEmpty ()){
                    comment_empty_text.setVisibility(VISIBLE);
                }
            }
        } );*/
        /*close_bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });*/

       /* post_detail_add_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post_detail_comment.getText().toString().equals("")){
                    ////////
                    Date date=new Date();
                    Calendar calendarOne=Calendar.getInstance ();
                    SimpleDateFormat currentDateOne=new SimpleDateFormat (" dd MMM yyyy hh:mm" );
                    String saveCurrentDateOne=currentDateOne.format ( calendarOne.getTime () );
                    String randomKey=saveCurrentDateOne;
                    final Map <String,Object> notification_map = new HashMap ();
                    notification_map.put ( "nom_du_produit",titreDuProduit );
                    notification_map.put ( "decription_du_produit",description );
                    notification_map.put ( "prix_du_produit",prix_produit );
                    notification_map.put ( "date_du_like",randomKey );
                    notification_map.put ( "image_du_produit",lien_image);
                    notification_map.put("categories",categories);
                    notification_map.put("id_de_utilisateur",utilisateur_actuel);
                    notification_map.put("id_du_post",iddupost);
                    notification_map.put("post_id",iddupost);
                    notification_map.put("action","commantaire");
                    notification_map.put("commantaire",post_detail_comment.getText().toString());
                    notification_map.put("is_new_notification","true");
                    notification_map.put("collection",choix);

                    /////
                    firebaseFirestore.collection("mes donnees utilisateur").document(current_user_id).get().addOnCompleteListener(DetailActivity.this,new OnCompleteListener<DocumentSnapshot> () {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult ().exists ()){
                                    String prenom=task.getResult ().getString ( "user_prenom" );
                                    String name_user= task.getResult ().getString ( "user_name" );
                                    String image_user=task.getResult ().getString ( "user_profil_image" );
                                    if (!current_user_id.equals ( utilisateur_actuel )){
                                        /////////////
                                        String TOPIC = "/topics/"+user_id_message; //topic has to match what the receiver subscribed to
                                        JSONObject notification = new JSONObject();
                                        JSONObject notifcationBody = new JSONObject ();
                                        try {
                                            notifcationBody.put("title", "nouvelle reaction");
                                            notifcationBody.put("message",name_user +" "+prenom +" reagi sur votre post") ;
                                            notifcationBody.put("id", user_id_message);
                                            notifcationBody.put ( "viens_de_detail","faux" );
                                            notifcationBody.put ( "id_recepteur",user_id_message );
                                            notifcationBody.put ( "image_en_vente",lien_image );
                                            notification.put("to", TOPIC);
                                            notification.put("categories_name", categories);
                                            notification.put("data", notifcationBody);
                                            notifcationBody.put ( "viens_de_detail","vrai" );

                                        } catch (JSONException e) {
                                            Log.e(TAG, "onCreate: " + e.getMessage() );
                                        }
                                        //sendNotification(notification);
                                        /////////////
                                    }


                                    ////end test noti

                                    //////////////////////////////

                                    if (!utilisateur_actuel.equals ( current_user_id )){
                                        firebaseFirestore.collection ( "mes donnees utilisateur" ).document (current_user_id).collection ( "mes notification" ).document(iddupost).set(notification_map).addOnSuccessListener(DetailActivity.this,new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void documentReference) {

                                            }

                                        }).addOnFailureListener ( DetailActivity.this, new OnFailureListener () {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        } );

                                        DocumentReference user = firebaseFirestore.collection("mes donnees utilisateur" ).document(current_user_id);
                                        user.update("has_notification", "true")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                    }
                                                });
                                    }

                                    ///////////////////////////
                                }
                            }else {
                                String error=task.getException().getMessage();
                                Toast.makeText ( getApplicationContext (), error, Toast.LENGTH_LONG ).show ();

                            }
                        }
                    });



                    post_detail_add_comment_btn.setVisibility(INVISIBLE);
                    progressBar3.setVisibility(VISIBLE);
                    SimpleDateFormat sdf= new SimpleDateFormat("d/MM/y H:mm:ss");
                    Calendar calendar=Calendar.getInstance ();
                    SimpleDateFormat currentDate=new SimpleDateFormat (" dd MMM yyyy H:mm" );
                    String saveCurrentDate=currentDate.format ( calendar.getTime () );
                    final Map<String,Object> user_comment = new HashMap();
                    comment = post_detail_comment.getText().toString();
                    user_comment.put ( "contenu",comment );
                    user_comment.put ( "heure",saveCurrentDate );
                    user_comment.put ( "id_user",utilisateur_actuel );
                    firebaseFirestore.collection ( "publication" ).document ("categories").collection (categories ).document (iddupost).collection("commentaires").add(user_comment).addOnSuccessListener(DetailActivity.this, new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String id_commentaire = documentReference.getId();
                            firebaseFirestore.collection ( "publication" ).document ("categories").collection ("nouveaux" ).document (iddupost).collection("commentaires").document(id_commentaire).set(user_comment).addOnSuccessListener(DetailActivity.this,new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                            firebaseFirestore.collection ( "publication" ).document ("post utilisateur").collection ( current_user_id ).document(iddupost).collection("commentaires").document(id_commentaire).set(user_comment).addOnSuccessListener(DetailActivity.this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    post_detail_comment.setText("");
                                    comment_empty_text.setVisibility(INVISIBLE);
                                    progressBar3.setVisibility(INVISIBLE);
                                    post_detail_add_comment_btn.setVisibility(VISIBLE);
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"vide",Toast.LENGTH_LONG).show();
                }
            }
        });

        commentaire();*/

        ////





    }

    void getLikeCount(){
        final DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes");
        like.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    Log.e("nombreLike",snap.getChildrenCount() + "");
                    likeNumber=snap.getChildrenCount();
                    if (likeNumber<=0){
                        likecommentsNumbers.setText((likeNumber) +" Like(s) - " + commentNumber + " Commentaires");
                    }else {
                        likecommentsNumbers.setText((likeNumber-1) +" Like(s) - " + commentNumber + " Commentaires");
                        //likecommentsNumbers.setText((likeNumber) +" Like(s) - " + commentNumber + " Commentaires");

                    }
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
                    like_icon.setAnimation ( AnimationUtils.loadAnimation ( DetailPhotoActivity.this,R.anim.fade_scale ) );
                    like_icon.setImageResource(R.drawable.ic_heart_like);
                    islike=true;
                    getLikeCount();

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
                        //like_icon.setAnimation ( AnimationUtils.loadAnimation ( DetailPhotoActivity.this,R.anim.fade_scale ) );
                        like_icon.setImageResource(R.drawable.ic_heart_empty);
                        islike=false;
                        getLikeCount();
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
                if (likeNumber<=0){
                    likecommentsNumbers.setText((likeNumber) +" Like(s) - " + commentNumber + " Commentaires");
                }else {
                    //likecommentsNumbers.setText((likeNumber-1) +" Like(s) - " + commentNumber + " Commentaires");
                    likecommentsNumbers.setText((likeNumber) +" Like(s) - " + commentNumber + " Commentaires");

                }

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
                                String prenomFinal = prenom.substring(0, 1).toUpperCase() + prenom.substring(1);
                                String nomFinal = nom.substring(0, 1).toUpperCase() + nom.substring(1);
                                nom_profil.setText(prenomFinal + " " + nomFinal);
                                toolbar.setTitle("Publication de "+prenomFinal + " " + nomFinal);
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
    //envoie le commentaire dans la bd
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
    //fait un toast
    void makeToast(String msg , Context context){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
    //Quand on
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
        finish();
    }
}
