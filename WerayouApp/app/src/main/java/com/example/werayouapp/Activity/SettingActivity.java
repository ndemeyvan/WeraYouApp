package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{
    CircleImageView profile_image;
    EditText ville_user;
    EditText age_user;
    EditText user_prenom;
    EditText user_nom;
    EditText pays_user;
    EditText phone_user;
    String[] recherche={"Que recherchez vous ?","Homme","Femme","Les deux"};
    String interesse;
    Uri mImageUri;
    byte[] final_image;
    ImageButton imageButton;
    boolean ischange=false;
    Button button;
    FirebaseAuth user ;
    private String userID;
    private static StorageReference storageReference;
    EditText Apropos;
    /*private RadioGroup mRadioGroup;
    RadioButton radio_homme;
    RadioButton radio_femme;*/
    Spinner spinnerTwo;
    private DatabaseReference usersDb;
    private String nom;
    private String prenom;
    private String userAge;
    private String ville;
    private String profileImageUrl;
    ProgressBar progressBar;
    ProgressBar progressBar3;
    private String userPays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        storageReference= FirebaseStorage.getInstance ().getReference ();
        profile_image=findViewById(R.id.profile_image);
        imageButton=findViewById(R.id.imageButton);
        ville_user=findViewById(R.id.ville_user);
        age_user=findViewById(R.id.age_user);
        button=findViewById(R.id.button);
        progressBar=findViewById(R.id.progressBar);
        progressBar3=findViewById(R.id.progressBar3);

        pays_user=pays_user=findViewById(R.id.pays_user);
        user_prenom=findViewById(R.id.user_prenom);
        user_nom=findViewById(R.id.user_nom);
        phone_user=findViewById(R.id.phone_user);
        spinnerTwo=findViewById(R.id.spinnerTwo);
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        spinnerTwo.setOnItemSelectedListener(this);
       /* mRadioGroup = (RadioGroup) findViewById(R.id.spinner);
        radio_homme = (RadioButton) findViewById(R.id.radio_homme);
        radio_femme = (RadioButton) findViewById(R.id.radio_femme);*/
        //
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,recherche);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        Apropos=findViewById(R.id.apropos);
        spinnerTwo.setAdapter(arrayAdapter);
        setImage();
        //
        user=FirebaseAuth.getInstance();
        userID=user.getCurrentUser().getUid();
        //
        getuserdata();
        getData();

    }
    //
    public void getData(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("data");
                usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if(map.get("nom")!=null){
                                nom = map.get("nom").toString();
                                user_nom.setText(nom);
                            }
                            if(map.get("prenom")!=null){
                                prenom = map.get("prenom").toString();
                                user_prenom.setText(prenom);

                            }
                            if(map.get("age")!=null){
                                userAge = map.get("age").toString();
                                age_user.setText(userAge+"");
                            }
                            if(map.get("ville")!=null){
                                ville = map.get("ville").toString();
                                ville_user.setText(ville);
                            }
                            if(map.get("image")!=null){
                                profileImageUrl = map.get("image").toString();
                                Picasso.with(SettingActivity.this).load(profileImageUrl).into(profile_image);
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                            // ce que l'utilisateur recher arranger dans le spinner
                            if(map.get("recherche")!=null){
                                String recherche = map.get("recherche").toString();
                                if (recherche.equals("Homme")){
                                    spinnerTwo.setSelection(1);
                                }else if (recherche.equals("Femme")){
                                    spinnerTwo.setSelection(2);
                                }else if (recherche.equals("Les deux")){
                                    spinnerTwo.setSelection(3);
                                }

                            }
                           /* // ce que l'utilisateur recher arranger dans le spinner
                            if(map.get("sexe")!=null){
                                String userSexe = map.get("sexe").toString();
                                if (userSexe.equals("Homme")){
                                    radio_homme.setChecked(true);
                                }else {
                                    radio_femme.setChecked(true);
                                }
                                //sexe.setText(userSexe);
                            }*/
                            if(map.get("pays")!=null){
                                 userPays = map.get("pays").toString();
                                pays_user.setText(userPays);

                            }
                            if(map.get("phone")!=null){
                                String userPhone = map.get("phone").toString();
                                phone_user.setText(userPhone);

                            }
                            if(map.get("apropos")!=null){
                                String apropos = map.get("apropos").toString();
                                Apropos.setText(apropos);

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
    //
    void makeToast(String msg){
        Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    //
    void setImage(){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(SettingActivity.this);

                    }catch (Exception e){
                        e.printStackTrace ();
                    }
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(SettingActivity.this);
                }
            }
        });

    }
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                File actualImage = new File(mImageUri.getPath());
                try{
                    Bitmap compressedImage = new Compressor(this)
                            .setMaxWidth(250)
                            .setMaxHeight(250)
                            .setQuality(80)
                            .compressToBitmap(actualImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    final_image = baos.toByteArray();
                }catch (Exception e){

                }
                profile_image.setImageURI ( mImageUri );
                ischange=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    //
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        interesse=recherche[i];
    }
    //
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //
    public void getuserdata(){
        button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                ///////////
                //int selectId = mRadioGroup.getCheckedRadioButtonId();
               // final RadioButton radioButton = (RadioButton) findViewById(selectId);
                //final String sexe = radioButton.getText().toString();
                final String apropos=Apropos.getText().toString();
                final String ville=ville_user.getText().toString();
                final String ageUser=age_user.getText().toString();
                final String nom = user_nom.getText().toString();
                final String prenom=user_prenom.getText().toString();

                //////////
                /////////// envoi des fichier dans la base de donnee
                if (ischange) {
                    if ( !TextUtils.isEmpty ( ville )&& mImageUri != null && !TextUtils.isEmpty ( ageUser )&& !TextUtils.isEmpty ( nom )&& !TextUtils.isEmpty ( prenom )&& !TextUtils.isEmpty ( apropos )) {
                        button.setVisibility(View.INVISIBLE);
                        progressBar3.setVisibility(View.VISIBLE);
                        //debut envoie dans storage
                        final StorageReference ref = storageReference.child ( "image_de_profile" ).child ( userID + " .jpg" );
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
                                        button.setVisibility(View.VISIBLE);
                                        progressBar3.setVisibility(View.INVISIBLE);
                                        makeToast("Erro , ry later");
                                        // Handle failures
                                        // ...
                                    }
                                    stockageWithURI ( task,nom,prenom,ville,ageUser,apropos);

                                } else {
                                    button.setVisibility(View.VISIBLE);
                                    progressBar3.setVisibility(View.INVISIBLE);
                                    makeToast("Error , ry later");
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                        ////////fin de l'envoie dans storage
                    } else {
                        button.setVisibility(View.VISIBLE);
                        progressBar3.setVisibility(View.INVISIBLE);
                        makeToast("Remplir tous les champs");
                    }
                }else{

                    stockageWithoutUri (  nom,prenom,ville, ageUser,apropos);

                }
            }
        });
    }
    //
    public void stockageWithURI(@NonNull Task<Uri> task,String nom,String prenom,String ville,String ageUser,String apropos ){
        Uri downloadUri;
        if (task!=null){
            downloadUri = task.getResult ();
        }else{
            downloadUri=mImageUri;
        }

        Calendar calendar=Calendar.getInstance ();
        SimpleDateFormat currentDate=new SimpleDateFormat (" dd MMM yyyy" );
        String saveCurrentDate=currentDate.format ( calendar.getTime () );
        String randomKey=saveCurrentDate;
        Map<String, String> user_data = new HashMap<>();
        user_data.put ( "nom",nom);
        user_data.put ( "prenom",prenom);
        user_data.put ( "ville", ville );
        user_data.put ( "age", ageUser );
        user_data.put ( "recherche",interesse);
        user_data.put("UpdatedDate",randomKey);
        user_data.put("image",downloadUri.toString());
        user_data.put("forfait","gratuit");
        user_data.put("id",userID);
        user_data.put("apropos",apropos);

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("data");
        userDb.setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(SettingActivity.this,ActivityPrincipal.class);
                startActivity(intent);
                finish();
                makeToast("enregister");
            }
        });


    }
    //
    public void stockageWithoutUri(String nom,String prenom,String ville,String ageUser,String apropos){
        Calendar calendar=Calendar.getInstance ();
        SimpleDateFormat currentDate=new SimpleDateFormat (" dd MMM yyyy" );
        String saveCurrentDate=currentDate.format ( calendar.getTime () );
        String date=saveCurrentDate;
        Map<String, Object> user_data = new HashMap<>();
        user_data.put ( "nom",nom);
        user_data.put ( "prenom",prenom);
        user_data.put ( "ville", ville );
        user_data.put ( "age", ageUser );
        user_data.put ( "recherche",interesse);
        user_data.put("UpdatedDate",date);
        user_data.put("forfait","gratuit");
        user_data.put("id",userID);
        user_data.put("apropos",apropos);
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("data");
        userDb.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(SettingActivity.this,ActivityPrincipal.class);
                startActivity(intent);
                finish();
                makeToast("enregister");
            }
        });


    }
    //
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingActivity.this,ActivityPrincipal.class);
        startActivity(intent);
        finish();
    }
}
