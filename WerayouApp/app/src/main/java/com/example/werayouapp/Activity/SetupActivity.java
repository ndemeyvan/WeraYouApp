package com.example.werayouapp.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    String[] genre={"Que recherchez vous ?","Homme","Femme"};
    Uri mImageUri;
    byte[] final_image;
    ImageButton imageButton;
    boolean ischange=false;
    Button button;
    String country;
    FirebaseAuth user ;
    String recherche;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        profile_image=findViewById(R.id.profile_image);
        imageButton=findViewById(R.id.imageButton);
        ville_user=findViewById(R.id.ville_user);
        age_user=findViewById(R.id.age_user);
        pays_user=findViewById(R.id.pays_user);
        spinner=findViewById(R.id.spinner);
        button=findViewById(R.id.button);
        phone_number=findViewById(R.id.phone_number);
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pays=pays_user.getText().toString();
                String phone =phone_number.getText().toString();
                String ville=ville_user.getText().toString();
                String ageUser=age_user.getText().toString();
                Map<String, String> user_data = new HashMap<>();
                user_data.put ( "pays",pays);
                user_data.put ( "phone",phone);
                user_data.put ( "ville", ville );
                user_data.put ( "age", ageUser );
                user_data.put ( "recherche",recherche);

                if (recherche.isEmpty()||pays.isEmpty()||phone.isEmpty()||ville.isEmpty()||ageUser.isEmpty()){
                    toast("veillez remplir tous les champs");
                }else{
                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(recherche).child(userID).child("name");
                    userDb.setValue(user_data);
                    Intent intent = new Intent(SetupActivity.this,ActivityPrincipal.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


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
                            .setQuality(40)
                            .compressToBitmap(actualImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 40, baos);
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
        recherche=genre[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
