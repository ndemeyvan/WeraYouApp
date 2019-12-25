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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

public class SetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    CircleImageView profile_image;
    EditText ville_user;
    EditText age_user;
    Spinner spinner;
    EditText pays_user;
    EditText phone_number;
    EditText user_prenom;
    EditText user_nom;
    String[] genre={"Quel est votre sex ?","Homme","Femme"};
    Uri mImageUri;
    byte[] final_image;
    ImageButton imageButton;
    boolean ischange=false;
    Button button;
    String country;
    FirebaseAuth user ;
    String sexe;
    String recherche;
    private String userID;
    private static StorageReference storageReference;
    private String lien;
    private Uri FilePathUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        storageReference= FirebaseStorage.getInstance ().getReference ();
        profile_image=findViewById(R.id.profile_image);
        imageButton=findViewById(R.id.imageButton);
        ville_user=findViewById(R.id.ville_user);
        age_user=findViewById(R.id.age_user);
        pays_user=findViewById(R.id.pays_user);
        spinner=findViewById(R.id.spinner);
        button=findViewById(R.id.button);
        phone_number=findViewById(R.id.phone_number);
        user_prenom=findViewById(R.id.user_prenom);
        user_nom=findViewById(R.id.user_nom);
        spinner.setOnItemSelectedListener(this);
        country=getIntent().getStringExtra("country");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,genre);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(arrayAdapter);
        setImage();
        user=FirebaseAuth.getInstance();
        userID=user.getCurrentUser().getUid();
        pays_user.setText(country);
        phone_number.setText(user.getCurrentUser().getPhoneNumber());
        getuserdata();




    }

    void toast(String msg){
        Toast.makeText(SetupActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    void setImage(){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(SetupActivity.this);

                    }catch (Exception e){
                        e.printStackTrace ();
                    }
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(SetupActivity.this);
                }
            }
        });

    }

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        sexe=genre[i];
        if (sexe.equals("Homme")){
            recherche="Femme";
        }else{
            recherche="Homme";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    public void getuserdata(){
        button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                ///////////
                final String pays=pays_user.getText().toString();
                final String phone =phone_number.getText().toString();
                final String ville=ville_user.getText().toString();
                final String ageUser=age_user.getText().toString();
                final String nom = user_nom.getText().toString();
                final String prenom=user_prenom.getText().toString();

                //////////
                /////////// envoi des fichier dans la base de donnee
                if (ischange) {
                    if (!TextUtils.isEmpty ( pays ) && !TextUtils.isEmpty ( phone ) && !TextUtils.isEmpty ( ville )&& mImageUri != null && !TextUtils.isEmpty ( ageUser )&& !TextUtils.isEmpty ( nom )&& !TextUtils.isEmpty ( prenom )) {

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
                                        // Handle failures
                                        // ...
                                    }
                                    stockage ( task,nom,prenom,pays,phone,ville,ageUser);

                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                        ////////fin de l'nvoie
                    } else {
                        Toast.makeText ( getApplicationContext (), "remplir tous les champs", Toast.LENGTH_LONG ).show ();
                    }
                }else{

                    stockage ( null, nom,prenom,pays,phone,ville, ageUser);

                }
            }
        });
    }

    public void stockage(@NonNull Task<Uri> task,String nom,String prenom,String pays,String phone,String ville,String ageUser ){
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
        user_data.put ( "pays",pays);
        user_data.put ( "phone",phone);
        user_data.put ( "ville", ville );
        user_data.put ( "age", ageUser );
        user_data.put ( "sexe",sexe);
        user_data.put ( "recherche",recherche);
        user_data.put("createdDate",randomKey);
        user_data.put("image",downloadUri.toString());
        user_data.put("forfait","gratuit");
        user_data.put("id",userID);
        user_data.put("apropos","ras");

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(sexe).child(userID);
        userDb.setValue(user_data);
        Intent intent = new Intent(SetupActivity.this,ActivityPrincipal.class);
        startActivity(intent);
        finish();


    }

}
