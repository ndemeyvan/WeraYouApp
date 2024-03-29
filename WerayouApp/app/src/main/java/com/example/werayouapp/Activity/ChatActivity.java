package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.werayouapp.UtilsForChat.ChatAdapter;
import com.example.werayouapp.UtilsForChat.DisplayAllChat;
import com.example.werayouapp.UtilsForChat.ModelChat;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    String id_user;
    DatabaseReference Db;
    RecyclerView mRecyclerView;
    CircleImageView profil_image;
    TextView nom_profil;
    FirebaseAuth user;
    String userID;
    String nom;
    DisplayAllChat contact;
    LinearLayout linearLayout;
    boolean isLock;
    EmojiconEditText editText;
    ImageButton emojiButton;
    ImageButton sendButton;
    ImageButton imageButton;
    ImageView imageToSend;
    ImageView resetImage;
    List<ModelChat> modelChatList;
    ChatAdapter chatAdapter;
    DatabaseReference reference;
    StorageReference storageReference;
    LinearLayout linearLayout3;
    byte[] final_image;
    boolean iamblocked;
    Uri mImageUri;
    static final int MAX_LENGTH = 100;
    boolean isWithImage = false;
    ProgressDialog dialog;
    View rootview;
    EmojIconActions emojIcon;
    TextView user_status;
    RequestQueue requestQueue;
    TextView iBlockHim;
    String URL = "https://fcm.googleapis.com/fcm/send";
    String Mon_nom;
    String Mon_prenom;
    MediaPlayer mMediaPlayer;
    SharedPreferences sharedpreferences;
    String myPref = "smsCount";
    ProgressBar message_progressBar;
    TextView message;
    TextView lock_unlock_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        storageReference = FirebaseStorage.getInstance().getReference();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        profil_image = findViewById(R.id.profil_image);
        nom_profil = findViewById(R.id.nom_profil);
        resetImage = findViewById(R.id.resetImage);
        editText = findViewById(R.id.editText);
        editText.setEmojiconSize(50);
        sendButton = findViewById(R.id.sendButton);
        imageButton = findViewById(R.id.imageButton);
        imageToSend = findViewById(R.id.imageToSend);
        emojiButton = findViewById(R.id.emojiButton);
        user_status = findViewById(R.id.user_status);
        iBlockHim = findViewById(R.id.iBlockHim);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout3 = findViewById(R.id.linearLayout3);
        message_progressBar = findViewById(R.id.message_progressBar);
        message = findViewById(R.id.message);
        lock_unlock_user = findViewById(R.id.lock_unlock_user);

        id_user = getIntent().getStringExtra("id");
        sharedpreferences = getSharedPreferences(id_user,
                Context.MODE_PRIVATE);
        //
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userID);
        //
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        //image_en_fond=findViewById ( R.id.image_en_fond );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
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
        //emoji
        rootview = findViewById(R.id.rootview);
        emojIcon = new EmojIconActions(this, rootview, editText, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {

            }

            @Override
            public void onKeyboardClose() {

            }
        });
        //annuler lenvoi d'une image
        resetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWithImage = false;
                imageToSend.setVisibility(View.GONE);
                resetImage.setVisibility(View.GONE);

            }
        });

        requestQueue = Volley.newRequestQueue(this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString();
                if (msg.isEmpty() && isWithImage == true) {
                    editText.setText("");
                    sendMessageWithImage(userID, id_user);
                    isWithImage = false;
                    imageToSend.setVisibility(View.GONE);
                    resetImage.setVisibility(View.GONE);
                    //setLastMessageStatuts("oui", id_user, userID);
                } else if (!msg.isEmpty() && isWithImage == false) {
                    editText.setText("");
                    sendmessage(userID, id_user, msg);
                    isWithImage = false;
                    imageToSend.setVisibility(View.GONE);
                    resetImage.setVisibility(View.GONE);
                    //setLastMessageStatuts("oui", id_user, userID);
                } else if (!msg.isEmpty() && isWithImage == true) {
                    editText.setText("");
                    isWithImage = false;
                    imageToSend.setVisibility(View.GONE);
                    resetImage.setVisibility(View.GONE);
                    sendMessageWithImageAndMessage(userID, id_user, msg);
                    //setLastMessageStatuts("oui", id_user, userID);
                }

                if (id_user != userID) {
                    //send notification
                    JSONObject json = new JSONObject();
                    try {
                        //json.put("to","/topics/"+id_user);
                        json.put("to", "/topics/" + id_user);
                        JSONObject notificationObj = new JSONObject();
                        notificationObj.put("title",Mon_nom + " " + Mon_prenom);
                        notificationObj.put("body", msg);

                        JSONObject extraData = new JSONObject();
                        extraData.put("id_recepteur", id_user);
                        extraData.put("type", "chat_notification");
                        extraData.put("id_post", "");
                        extraData.put("id_user", userID);
                        extraData.put("description", "");
                        extraData.put("image", "");
                        extraData.put("date", "");

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

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage();
            }
        });

        getUserData();
        getMyInfo();
        checkifIsBlcoked(userID, id_user);
        checkifIBlcoked();
        readMessage(userID, id_user);

        lock_unlock_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLock == true) {
                    Log.i("lol", "lol");
                    new AlertDialog.Builder(ChatActivity.this)
                            .setTitle("Weareyou")
                            .setMessage(getResources().getString(R.string.lock_user))
                            .setCancelable(false)
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    blockUser();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    //Log.i("lol","lol");
                    new AlertDialog.Builder(ChatActivity.this)
                            .setTitle("Weareyou")
                            .setMessage(getResources().getString(R.string.unlock_simple) + " ?")
                            .setCancelable(false)
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    unLock();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            }
        });

        setStatus(true);

    }


    void blockUser() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        final String date = saveCurrentDate;
        Date jour = new Date();
        final long time = jour.getTime();
        final Map<String, Object> user_data = new HashMap<>();
        user_data.put("updatedDate", date);
        user_data.put("id", id_user);
        user_data.put("time", time);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        DatabaseReference boquer = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("bloquer").child(id_user);
        boquer.setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                makeToast(getResources().getString(R.string.bloquer));
                lock_unlock_user.setText(getResources().getString(R.string.unlock_simple));
                isLock = true;
                //checkifIBlcoked();
            }
        });
    }

    void checkifIBlcoked() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("bloquer");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(id_user)) {
                    lock_unlock_user.setText(getResources().getString(R.string.unlock_simple));
                    isLock = true;
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    iBlockHim.setText(getResources().getString(R.string.unlock_first));
                    Log.i("lol", "lol");
                } else {
                    lock_unlock_user.setText(getResources().getString(R.string.lock));
                    isLock = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    void unLock() {
//        dialog.setMessage(getResources().getString(R.string.loading));
//        dialog.show();
        /*ici il est question de supprimer un utilisateur  de la collection de demande d'amies */
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("bloquer").child(id_user);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    progressDialog.dismiss();
                    snapshot.getRef().removeValue();
                    lock_unlock_user.setText(getResources().getString(R.string.lock));
                    isLock = false;
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearLayout3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void checkifIsBlcoked(final String myID, final String herID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(myID).child("connections").child("bloquer").child(herID);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    iBlockHim.setText(getResources().getString(R.string.unlock_first));
                    iBlockHim.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.GONE);
                    isLock = true;
                } else {
                    lock_unlock_user.setText(getResources().getString(R.string.lock));
                    isLock = false;
                    checkifSheBlcokedMe(herID, myID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void checkifSheBlcokedMe(String id_user, final String userID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("connections").child("bloquer").child(userID);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    iBlockHim.setText(getResources().getString(R.string.your_are_block) + " " + nom);
                    iBlockHim.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    emojiButton.setVisibility(View.INVISIBLE);
                    imageButton.setVisibility(View.INVISIBLE);
                    linearLayout3.setVisibility(View.GONE);
                    lock_unlock_user.setVisibility(View.GONE);

                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    iBlockHim.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout3.setVisibility(View.VISIBLE);
                    lock_unlock_user.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //cette methode appele l'activite de choix d'image
    void setImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatActivity.this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(ChatActivity.this);
        }

    }

    //cette methode permet de set l'image a imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                File actualImage = new File(mImageUri.getPath());
                try {
                    Bitmap compressedImage = new Compressor(this)
                            .setMaxWidth(250)
                            .setMaxHeight(250)
                            .setQuality(100)
                            .compressToBitmap(actualImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final_image = baos.toByteArray();
                    imageToSend.setImageURI(mImageUri);
                    isWithImage = true;
                    if (isWithImage == true) {
                        imageToSend.setVisibility(View.VISIBLE);
                        resetImage.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void stockageWithURI(@NonNull Task<Uri> task, final String expediteur, final String recepteur) {
        Uri downloadUri;
        if (task != null) {
            downloadUri = task.getResult();
        } else {
            downloadUri = mImageUri;
        }
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        String date = saveCurrentDate;
        final HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("expediteur", expediteur);
        messageMap.put("recepteur", recepteur);
        messageMap.put("message", downloadUri.toString());
        messageMap.put("image", downloadUri.toString());
        messageMap.put("createdDate", date);
        messageMap.put("type", "image");
        reference.child("Chats").push().setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                editText.setText("");
            }
        });
        //
        Date jour = new Date();
        final long time = jour.getTime();
        contact = new DisplayAllChat();
        contact.setId_recepteur(recepteur);
        contact.setId_expediteur(expediteur);
        contact.setDernier_message("image");
        contact.setServerTime(time);
        contact.setIsnew("non");
        //
        reference.child("dernier_message")
                .child(expediteur)
                .child("contacts")
                .child(recepteur).setValue(contact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //
                        contact = new DisplayAllChat();
                        contact.setId_recepteur(expediteur);
                        contact.setId_expediteur(recepteur);
                        contact.setDernier_message("image");
                        contact.setServerTime(time);
                        contact.setIsnew("oui");
                        //
                        reference.child("dernier_message")
                                .child(recepteur)
                                .child("contacts")
                                .child(expediteur)
                                .setValue(contact)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        updateNotification("newMessageNotif", true, recepteur);
                                    }
                                });
                    }
                });

    }

    void updateNotification(String key, boolean status, String userID) {
        Map<String, Object> user_data = new HashMap<>();
        user_data.put(key, status);
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        userDb.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    //cette methode sert a attribuer le type message et image pendant l'envoie
    void stockageWithURIForMessageAndImage(@NonNull Task<Uri> task, final String expediteur, final String recepteur, String msg) {
        Uri downloadUri;
        if (task != null) {
            downloadUri = task.getResult();
        } else {
            downloadUri = mImageUri;
        }
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        String date = saveCurrentDate;
        final HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("expediteur", expediteur);
        messageMap.put("recepteur", recepteur);
        messageMap.put("message", msg);
        messageMap.put("image", downloadUri.toString());
        messageMap.put("createdDate", date);
        messageMap.put("type", "msgAndImage");
        reference.child("Chats").push().setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                editText.setText("");
            }
        });
        //
        Date jour = new Date();
        final long time = jour.getTime();
        contact = new DisplayAllChat();
        contact.setServerTime(time);
        contact.setId_recepteur(recepteur);
        contact.setId_expediteur(expediteur);
        contact.setDernier_message("image");
        contact.setIsnew("non");
        //
        reference.child("dernier_message")
                .child(expediteur)
                .child("contacts")
                .child(recepteur).setValue(contact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //
                        contact = new DisplayAllChat();
                        contact.setId_recepteur(expediteur);
                        contact.setId_expediteur(recepteur);
                        contact.setDernier_message("image");
                        contact.setServerTime(time);
                        contact.setIsnew("oui");
                        //
                        reference.child("dernier_message")
                                .child(recepteur)
                                .child("contacts")
                                .child(expediteur).setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                updateNotification("newMessageNotif", true, recepteur);

                            }
                        });
                    }
                });

    }

    //recuperer les information de l'utilisateur a qui le message est envoye
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
                                toolbar.setTitle(getResources().getString(R.string.publication_of) + prenomFinal + " " + nomFinal);
                            }
                            if (map.get("isOnline") != null) {
                                boolean status = (Boolean) map.get("isOnline");
                                if (status == true) {
                                    user_status.setText(getResources().getString(R.string.online));
                                } else {
                                    user_status.setText(getResources().getString(R.string.offline));
                                }
                            }
                            if (map.get("image") != null) {
                                String profileImageUrl = map.get("image").toString();
                                Picasso.with(ChatActivity.this).load(profileImageUrl).placeholder(R.drawable.ic_launcher_background).into(profil_image);
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
                getUserstatus();
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

    // recupere les information de  qui le message est envoye
    public void getUserstatus() {
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
                            if (map.get("isOnline") != null) {
                                boolean status = (Boolean) map.get("isOnline");
                                if (status == true) {
                                    user_status.setText(getResources().getString(R.string.online));
                                } else {
                                    user_status.setText(getResources().getString(R.string.offline));
                                }
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

    //recupere les info de lutilisateur actuel
    public void getMyInfo() {
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
                                Mon_nom = map.get("nom").toString();
                            }
                            if (map.get("prenom") != null) {
                                Mon_prenom = map.get("prenom").toString();
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

    //cette fonction envoi uniquement les image en message
    void sendMessageWithImage(final String expediteur, final String recepteur) {
        dialog = ProgressDialog.show(ChatActivity.this, "", getResources().getString(R.string.send_image), true);
        //debut envoie dans storage
        String random = random();
        final StorageReference ref = storageReference.child("messages_images").child(random + " .jpg");
        UploadTask uploadTask = ref.putBytes(final_image);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                    } else {
                        // Handle failures
                        // ...
                    }
                    stockageWithURI(task, expediteur, recepteur);

                } else {

                    // Handle failures
                    // ...
                }
            }
        });
    }

    //cette fonction envoie uniquement les message
    public void sendmessage(final String expediteur, final String recepteur, final String message) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        String date = saveCurrentDate;
        final HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("expediteur", expediteur);
        messageMap.put("recepteur", recepteur);
        messageMap.put("message", message);
        messageMap.put("image", "");
        messageMap.put("createdDate", date);
        messageMap.put("type", "message");
        reference.child("Chats").push().setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                editText.setText("");
            }
        });
        //
        contact = new DisplayAllChat();
        contact.setId_recepteur(recepteur);
        contact.setId_expediteur(expediteur);
        contact.setDernier_message(message);
        Date jour = new Date();
        final long time = jour.getTime();
        contact.setServerTime(time);
        contact.setIsnew("non");
        //
        reference.child("dernier_message")
                .child(expediteur)
                .child("contacts")
                .child(recepteur).setValue(contact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //
                        contact = new DisplayAllChat();
                        contact.setId_recepteur(expediteur);
                        contact.setId_expediteur(recepteur);
                        contact.setDernier_message(message);
                        contact.setServerTime(time);
                        contact.setIsnew("oui");
                        //
                        reference.child("dernier_message")
                                .child(recepteur)
                                .child("contacts")
                                .child(expediteur).setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateNotification("newMessageNotif", true, recepteur);

                            }
                        });
                    }
                });


    }

    //cette fonction envoie les message et les image
    void sendMessageWithImageAndMessage(final String expediteur, final String recepteur, final String msg) {
        dialog = ProgressDialog.show(ChatActivity.this, "", getResources().getString(R.string.loading), true);
        //debut envoie dans storage
        String random = random();
        final StorageReference ref = storageReference.child("messages_images").child(random + " .jpg");
        UploadTask uploadTask = ref.putBytes(final_image);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                    } else {
                        // Handle failures
                        // ...
                    }
                    stockageWithURIForMessageAndImage(task, expediteur, recepteur, msg);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }


    //affiche les message dans la base de dooneee
    public void readMessage(final String monId, final String sonID) {
        // modelChatList.clear();
        modelChatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setLastMessageStatuts("non", id_user, userID);
                modelChatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelChat chat = snapshot.getValue(ModelChat.class);
                    if (chat.getRecepteur().equals(monId) && chat.getExpediteur().equals(sonID) || chat.getRecepteur().equals(sonID) && chat.getExpediteur().equals(monId)) {
                        modelChatList.add(chat);
                        message_progressBar.setVisibility(View.INVISIBLE);
                        message.setVisibility(View.INVISIBLE);

                    }


                    chatAdapter = new ChatAdapter(getApplicationContext(), modelChatList, true);
                    mRecyclerView.setAdapter(chatAdapter);
                    chatAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());


                    message_progressBar.setVisibility(View.INVISIBLE);
                    message.setVisibility(View.INVISIBLE);
                }
                if (modelChatList.size() == 0) {
                    message.setVisibility(View.VISIBLE);
                    message_progressBar.setVisibility(View.INVISIBLE);
                } else {
                    //play notification sound if user send or receive notification
//                play(ChatActivity.this,R.raw.notif);

//                play notification sound if user send or receive notification

                    if (!sharedpreferences.contains(id_user)) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        if (modelChatList.size() == 0) {
                            editor.putInt(id_user, 0);
                            editor.commit();
                        } else {
                            editor.putInt(id_user, chatAdapter.getItemCount());
                            editor.commit();
                        }

//                    play(ChatActivity.this,R.raw.newnotification);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stop();
                            }
                        }, 2000);
                    } else {
                        int number = sharedpreferences.getInt(id_user, 0);
                        if (chatAdapter.getItemCount() > number) {
                            play(ChatActivity.this, R.raw.notif);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt(id_user, chatAdapter.getItemCount());
                            editor.commit();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    stop();
                                }
                            }, 1500);
                        } else {

                        }
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context c, int rid) {
        stop();

        mMediaPlayer = MediaPlayer.create(c, rid);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

        mMediaPlayer.start();
    }

    void makeToast(String msg) {
        Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    void setStatus(boolean status) {
        Map<String, Object> user_data = new HashMap<>();
        user_data.put("isOnline", status);

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        userDb.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });

    }

    void setLastMessageStatuts(String status, String expediteur, String recepteur) {
        final Map<String, Object> user_data = new HashMap<>();
        user_data.put("isnew", status);
        final DatabaseReference db = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("dernier_message")
                .child(recepteur)
                .child("contacts")
                .child(expediteur);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    db.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Intent intent = new Intent(SettingActivity.this,ActivityPrincipal.class);
                            //startActivity(intent);
                            // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus(false);
//        FirebaseMessaging.getInstance().subscribeToTopic(userID);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        FirebaseMessaging.getInstance().subscribeToTopic(userID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setStatus(false);
//        FirebaseMessaging.getInstance().subscribeToTopic("");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setStatus(true);
//        FirebaseMessaging.getInstance().subscribeToTopic("");

    }


}
