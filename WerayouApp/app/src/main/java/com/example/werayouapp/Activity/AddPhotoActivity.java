package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class AddPhotoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int MAX_LENGTH = 100;

    ImageView image;
    EditText post_description;
    Button post_button;
    ProgressBar progressBar;
    Toolbar toolbar;
    Uri mImageUri;
    StorageReference storageReference;

    //
    String userID;
    FirebaseAuth user;
    //
    byte[] final_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        image = findViewById(R.id.image);
        storageReference = FirebaseStorage.getInstance().getReference();
        post_description = findViewById(R.id.post_description);
        post_button = findViewById(R.id.post_button);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(AddPhotoActivity.this,ActivityPrincipal.class);
                //startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
                finish();
            }
        });
        getuserdata();
        setImage();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.image:
                setImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.image:
                setImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void makeToast(String msg) {
        Toast.makeText(AddPhotoActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    void setImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddPhotoActivity.this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(AddPhotoActivity.this);
        }

    }

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
                } catch (Exception e) {

                }
                image.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void getuserdata() {
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                post_button.setVisibility(View.INVISIBLE);
                final String description = post_description.getText().toString();
                /////////// envoi des fichier dans la base de donnee
                if (!TextUtils.isEmpty(description)) {
                    //debut envoie dans storage
                    String random = random();
                    final StorageReference ref = storageReference.child("image_de_posts").child(random + " .jpg");
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
                                stockageWithURI(task, description);

                            } else {

                                // Handle failures
                                // ...
                            }
                        }
                    });
                    ////////fin de l'envoie dans storage
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    post_button.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.write_all), Toast.LENGTH_LONG).show();
                }

            }


        });


    }


    void stockageWithURI(@NonNull Task<Uri> task, String description) {
        Uri downloadUri;
        if (task != null) {
            downloadUri = task.getResult();
        } else {
            downloadUri = mImageUri;
        }

        //
        String key = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("posts").push().getKey();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        String date = saveCurrentDate;
        Date jour= new Date();
        final long time = jour.getTime();
        Map<String, Object> post_data = new HashMap<>();
        post_data.put("image", downloadUri.toString());
        post_data.put("description", description);
        post_data.put("id_post", key);
        post_data.put("id_user", userID);
        post_data.put("statut", "public");
        post_data.put("createdDate", date);
        post_data.put("serverTime",time);

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("posts").child(key);
        userDb.setValue(post_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                /*Intent intent = new Intent(AddPhotoActivity.this,ActivityPrincipal.class);
                startActivity(intent);*/
                // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
                finish();
                makeToast(getResources().getString(R.string.is_publish));
            }
        });
        //

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(AddPhotoActivity.this,ActivityPrincipal.class);
        startActivity(intent);*/
        //overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
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
