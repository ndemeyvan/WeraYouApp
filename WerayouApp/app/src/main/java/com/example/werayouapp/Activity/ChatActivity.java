package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.example.werayouapp.UtilsForChat.ChatAdapter;
import com.example.werayouapp.UtilsForChat.DisplayAllChat;
import com.example.werayouapp.UtilsForChat.ModelChat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    String id_user;
    DatabaseReference Db;
    RecyclerView mRecyclerView;
    CircleImageView profil_image;
    TextView nom_profil;
    //
    FirebaseAuth user ;
    String userID;
    String nom;
    DisplayAllChat contact;
    //
    EditText editText;
    ImageButton sendButton;
    List<ModelChat> modelChatList;
    ChatAdapter chatAdapter;
    DatabaseReference reference;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        profil_image=findViewById(R.id.profil_image);
        nom_profil=findViewById(R.id.nom_profil);
        id_user=getIntent().getStringExtra("id");
        editText=findViewById(R.id.editText);
        sendButton=findViewById(R.id.sendButton);

        //
        user= FirebaseAuth.getInstance();
        userID=user.getCurrentUser().getUid();
        //
        mRecyclerView=findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize ( true );
        //image_en_fond=findViewById ( R.id.image_en_fond );
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager( ChatActivity.this );
        linearLayoutManager.setStackFromEnd ( true );
        mRecyclerView.setLayoutManager ( linearLayoutManager );
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
        //appel de fonction
        getUserData();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String msg= editText.getText().toString();
               if (!msg.isEmpty()){
                   sendmessage(userID,id_user,msg);
               }else{
                    makeToast("entrer un message");
               }
            }
        });

        readMessage(userID,id_user);
    }

    //recuperer les information de l'utilisateur
    public void getUserData(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user);
                Db.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void sendmessage(final String expediteur, final String recepteur, final String message){

        final DatabaseReference reference =FirebaseDatabase.getInstance ().getReference ();
        Calendar calendar=Calendar.getInstance ();
        SimpleDateFormat currentDate=new SimpleDateFormat (" dd MMM yyyy" );
        String saveCurrentDate=currentDate.format ( calendar.getTime () );
        String date=saveCurrentDate;
        final HashMap<String,Object> messageMap = new HashMap<> (  );
        messageMap.put ( "expediteur",expediteur );
        messageMap.put ( "recepteur",recepteur );
        messageMap.put ( "message",message );
        messageMap.put ( "createdDate",date );
        reference.child ( "Chats" ).push ().setValue ( messageMap ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                editText.setText("");
            }
        });
        //
        contact =new DisplayAllChat(  );
        contact.setId_recepteur ( recepteur );
        contact.setId_expediteur ( expediteur );
        contact.setDernier_message ( message );
        //
        reference.child ( "dernier_message" )
                .child(expediteur)
                .child("contacts")
                .child(recepteur).push ().setValue ( contact )
                .addOnSuccessListener ( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
        //
                contact =new DisplayAllChat (  );
                contact.setId_recepteur ( expediteur );
                contact.setId_expediteur ( recepteur );
                contact.setDernier_message ( message );
        //
                reference.child ( "dernier_message" )
                        .child(recepteur)
                        .child("contacts")
                        .child(expediteur).push ().setValue ( contact ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        } );


    }


    public void readMessage(final String monId, final String sonID){
        // modelChatList.clear();
        modelChatList=new ArrayList<>(  );
        reference=FirebaseDatabase.getInstance ().getReference ("Chats");
        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelChatList.clear ();
                for (DataSnapshot snapshot:dataSnapshot.getChildren ()){
                    ModelChat chat = snapshot.getValue (ModelChat.class);
                    if (chat.getRecepteur ().equals ( monId )&&chat.getExpediteur ().equals ( sonID)||chat.getRecepteur ().equals ( sonID )&&chat.getExpediteur ().equals ( monId )){
                        modelChatList.add ( chat );
                    }
                    chatAdapter=new ChatAdapter(getApplicationContext (),modelChatList,true);
                    mRecyclerView.setAdapter ( chatAdapter );
                    chatAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );



    }

    void makeToast(String msg){
        Toast.makeText(ChatActivity.this,msg,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
