package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.werayouapp.R;
import com.example.werayouapp.adapter.CommentAdapter;
import com.example.werayouapp.model.CommentModel;
import com.google.android.gms.tasks.OnCanceledListener;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPhotoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String id_post;
    String id_user;
    String description;
    String image;
    String date;
    //
    FirebaseAuth user;
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
    DatabaseReference Db;
    ProgressBar progressBar;
    Toolbar toolbar;
    ProgressBar progressBar2;
    ViewGroup rootView;
    ImageView like_icon;
    long likeNumber;
    long commentNumber;
    boolean islike;
    RequestQueue requestQueue;
    String URL = "https://fcm.googleapis.com/fcm/send";
    private String notificationName;
    private String commentaire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        id_post = getIntent().getStringExtra("id_post");
        id_user = getIntent().getStringExtra("id_user");
        description = getIntent().getStringExtra("description");
        image = getIntent().getStringExtra("image");
        date = getIntent().getStringExtra("date");
        requestQueue = Volley.newRequestQueue(this);


        //
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();

        //
        rootView = findViewById(R.id.root_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        profil_image = findViewById(R.id.profil_image);
        nom_profil = findViewById(R.id.nom_profil);
        date_publication = findViewById(R.id.date_publication);
        description_view = findViewById(R.id.description_view);
        imageView = findViewById(R.id.image);
        like_icon = findViewById(R.id.like_icon);
        progressBar = findViewById(R.id.progressBar);
        send_comment_button = findViewById(R.id.sendButton);
        aucun_commentaires = findViewById(R.id.aucun_commentaires);
        progressBar2 = findViewById(R.id.progressBar3);
        likecommentsNumbers = findViewById(R.id.likecommentsNumbers);
        comment_edittext = findViewById(R.id.editText);
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
        mRecyclerView = findViewById(R.id.recyclerView);
        //
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // mRecyclerView.addItemDecoration(new Grids(2, dpToPx(8), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(true);
        commentList = new ArrayList<>();
        //
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        getLikeCount();
        checkifLike();
        getComments();
        getInfo();

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
        if (userID.equals(id_user)) {
            inflater.inflate(R.menu.detail_menu, menu);
        } else {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                new AlertDialog.Builder(DetailPhotoActivity.this)
                        .setTitle(getResources().getString(R.string.delete))
                        .setMessage(getResources().getString(R.string.delete_explain))

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deletePost();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


                return true;
            case R.id.edit:
                editDesc();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                new AlertDialog.Builder(DetailPhotoActivity.this)
                        .setTitle(getResources().getString(R.string.delete))
                        .setMessage(getResources().getString(R.string.delete_explain))
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deletePost();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            case R.id.edit:
                editDesc();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void deletePost() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Users").child(id_user).child("posts").child(id_post);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            makeToast("error please try later",getApplicationContext());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               // Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
//        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post);
//        db.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
//                    appleSnapshot.getRef().removeValue();
////                    Intent intent = new Intent(getApplicationContext(),ActivityPrincipal.class);
////                    startActivity(intent);
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("info", "impossible de supprimer ", databaseError.toException());
//            }
//        });
    }

    void editDesc() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailPhotoActivity.this);
        View parientView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(parientView);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parientView.getParent());
        //bottomSheetBehavior.setPeekHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics()));
        bottomSheetBehavior.setPeekHeight(510);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();
        final ProgressBar progressBar3 = parientView.findViewById(R.id.progressBar3);
        final EditText descEditText = parientView.findViewById(R.id.editText);
        final ImageButton button = parientView.findViewById(R.id.sendButton);
        final ImageView close_bottom = parientView.findViewById(R.id.close_bottom);
        final ImageView up_bottomshet = parientView.findViewById(R.id.up_bottomshet);
        up_bottomshet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        descEditText.setText(description);
        close_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = descEditText.getText().toString();
                if (!desc.isEmpty()) {
                    progressBar3.setVisibility(View.VISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    Map<String, Object> post_data = new HashMap<>();
                    post_data.put("description", desc);
                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post);
                    userDb.updateChildren(post_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                            //Intent intent = new Intent(DetailPhotoActivity.this,ActivityPrincipal.class);
                            //startActivity(intent);
                            // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
                            //finish();
                            makeToast(getResources().getString(R.string.modify), DetailPhotoActivity.this);
                            getNewDesc();
                            bottomSheetDialog.dismiss();
                        }
                    });

                } else {
                    progressBar3.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.VISIBLE);
                    makeToast(getResources().getString(R.string.write_all), DetailPhotoActivity.this);
                }
            }
        });

    }

    //recuprer la nouvelle description
    public void getNewDesc() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post);
                Db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if (map.get("description") != null) {
                                String desc = map.get("description").toString();
                                description_view.setText(desc);
                            }
                            //
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

    //le nombre de like
    void getLikeCount() {
        final DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes");
        like.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    likeNumber = dataSnapshot.getChildrenCount();
                    likecommentsNumbers.setText((likeNumber) + " Like(s) - " + commentNumber + " " + getResources().getString(R.string.comments) + "(s)");
                } else {
                    likecommentsNumbers.setText((0) + " Like(s) - " + commentNumber + " " + getResources().getString(R.string.comments) + "(s)");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // ajout le like dans la bd
    void setLike() {
        if (islike == false) {
            //String key = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes").push().getKey();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
            String saveCurrentDate = currentDate.format(calendar.getTime());
            String date = saveCurrentDate;
            Date jour = new Date();
            final long time = jour.getTime();
            Map<String, Object> comment_data = new HashMap<>();
            comment_data.put("id", user.getUid());
            comment_data.put("createdDate", date);
            comment_data.put("time", time);
            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes").child(userID);
            userDb.setValue(comment_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //like_icon.setAnimation ( AnimationUtils.loadAnimation ( DetailPhotoActivity.this,R.anim.fade_scale ) );
                    like_icon.setImageResource(R.drawable.ic_heart_like);
                    islike = true;
                    getLikeCount();
                    checkifLike();

                }
            });

        } else {
            //remove value
            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes").child(userID);
            userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        //like_icon.setAnimation ( AnimationUtils.loadAnimation ( DetailPhotoActivity.this,R.anim.fade_scale ) );
                        like_icon.setImageResource(R.drawable.ic_heart_empty);
//                        islike = false;
                        getLikeCount();
                        checkifLike();
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
    void checkifLike() {
        DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("likes").child(userID);
        like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    islike = true;
                    like_icon.setImageResource(R.drawable.ic_heart_like);
                    Log.i("StatusLike ", "J'ai deja liker");
                } else {
                    like_icon.setImageResource(R.drawable.ic_heart_empty);
                    islike = false;
                    Log.i("StatusLike ", "absent dans mes gens bloquer");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //

    }

    //recupere tout ce que l'utilisateur a poste
    void getComments() {
        //adding an event listener to fetch values
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("commentaires");
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
                Log.e("size", commentList.size() + "");
                commentNumber = commentList.size();
                if (likeNumber <= 0) {
                    likecommentsNumbers.setText((likeNumber) + " Like(s) - " + commentNumber + " " + getResources().getString(R.string.comments) + "(s)");
                } else {
                    //likecommentsNumbers.setText((likeNumber-1) +" Like(s) - " + commentNumber + " Commentaires");
                    likecommentsNumbers.setText((likeNumber) + " Like(s) - " + commentNumber + " " + getResources().getString(R.string.comments) + "(s)");

                }
                //creating adapter
                adapter = new CommentAdapter(commentList, DetailPhotoActivity.this);
                //adding adapter to recyclerview
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //recuperer les information de l'utilisateur
    public void getUserData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
                Db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if (map.get("nom") != null) {
                                nom = map.get("nom").toString();
                            }
                            if (map.get("prenom") != null) {
                                String prenom = map.get("prenom").toString();
                                String prenomFinal = prenom.substring(0, 1).toUpperCase() + prenom.substring(1);
                                String nomFinal = nom.substring(0, 1).toUpperCase() + nom.substring(1);
                                nom_profil.setText(prenomFinal + " " + nomFinal);
                                toolbar.setTitle(prenomFinal + " " + nomFinal);
                            }
                            if (map.get("image") != null) {
                                String profileImageUrl = map.get("image").toString();
                                Picasso.with(DetailPhotoActivity.this).load(profileImageUrl).placeholder(R.drawable.ic_launcher_background).into(profil_image);
                                profil_image.setAnimation(AnimationUtils.loadAnimation(DetailPhotoActivity.this, R.anim.fade_scale));

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
    public void sendComment() {
        send_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentaire = comment_edittext.getText().toString();
                String key = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child("posts").child(id_post).child("commentaires").push().getKey();

                if (!TextUtils.isEmpty(commentaire)) {
                    send_comment_button.setVisibility(View.INVISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
                    String saveCurrentDate = currentDate.format(calendar.getTime());
                    final String date = saveCurrentDate;
                    Map<String, Object> comment_data = new HashMap<>();
                    comment_data.put("id", user.getUid());
                    comment_data.put("commentaire", commentaire);
                    comment_data.put("createdDate", date);
                    comment_data.put("id_commentaire", key);
                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("posts").child(id_post).child("commentaires").child(key);
                    userDb.setValue(comment_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // makeToast("enregister",DetailPhotoActivity.this);
                            comment_edittext.setText("");
                            send_comment_button.setVisibility(View.VISIBLE);
                            progressBar2.setVisibility(View.INVISIBLE);


                            if (!id_user.equals(userID)) {
                                Log.i("it's the same user ","its is : "+id_user.equals(userID));
                                //send notification
                                JSONObject json = new JSONObject();
                                try {
                                    //json.put("to","/topics/"+id_user);
                                    json.put("to", "/topics/" + id_user);
                                    JSONObject notificationObj = new JSONObject();
                                    notificationObj.put("title",
                                            notificationName + " " +
                                                    getResources().getString(R.string.comment_your_post));
                                    notificationObj.put("body", commentaire);
                                    JSONObject extraData = new JSONObject();
                                    extraData.put("id_recepteur", "");
                                    extraData.put("type", "post_notification");
                                    extraData.put("id_post", id_post);
                                    extraData.put("id_user", id_user);
                                    extraData.put("description", description);
                                    extraData.put("image", image);
                                    extraData.put("date", date);
                                    json.put("notification", notificationObj);
                                    json.put("data", extraData);

                                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                                            json,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {

                                                    Log.d("MUR", "onResponse: ");
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("MUR", "onError: " + error.networkResponse);
                                        }
                                    }
                                    ) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> header = new HashMap<>();
                                            header.put("content-type", "application/json");
                                            header.put("authorization", "key=AIzaSyDXuRqLiT6p9MlCt1lg8MEqpkx67Tm0NpA");
                                            return header;
                                        }
                                    };
                                    requestQueue.add(request);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });



                } else {
                    makeToast(getResources().getString(R.string.write_all), DetailPhotoActivity.this);

                }
            }
        });
    }

    public void getInfo() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                Db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if (map.get("nom") != null) {
                                notificationName = map.get("nom").toString();
                            }
                            if (map.get("prenom") != null) {
                                notificationName = map.get("prenom").toString();
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


    //fait un toast
    void makeToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    //Quand on rentre en arriere
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}
