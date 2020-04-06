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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class SetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView profile_image;
    ProgressBar progressBar;
    EditText ville_user;
    EditText age_user;
    EditText user_prenom;
    EditText user_nom;
    String[] recherche = {"Que recherchez vous ?", "Homme", "Femme", "Les deux"};
    String interesse;
    Uri mImageUri;
    byte[] final_image;
    ImageButton imageButton;
    boolean ischange = false;
    Button button;
    String country;
    FirebaseAuth user;
    String sexe;
    private RadioGroup mRadioGroup;
    RadioButton radio_homme;
    RadioButton radio_femme;
    TextView place;
    TextView phone;
    private String userID;
    private static StorageReference storageReference;
    EditText Apropos;
    Spinner spinnerTwo;
    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        storageReference = FirebaseStorage.getInstance().getReference();
        profile_image = findViewById(R.id.profile_image);
        imageButton = findViewById(R.id.imageButton);
        ville_user = findViewById(R.id.ville_user);
        age_user = findViewById(R.id.age_user);
        phone = findViewById(R.id.phone);
        button = findViewById(R.id.button);
        place = findViewById(R.id.place);
        user_prenom = findViewById(R.id.user_prenom);
        user_nom = findViewById(R.id.user_nom);
        spinnerTwo = findViewById(R.id.spinnerTwo);
        spinnerTwo.setOnItemSelectedListener(this);
        mRadioGroup = (RadioGroup) findViewById(R.id.spinner);
        radio_homme = (RadioButton) findViewById(R.id.radio_homme);
        radio_femme = (RadioButton) findViewById(R.id.radio_femme);
        country = getIntent().getStringExtra("country");
        countryCode = getIntent().getStringExtra("countryCode");
        Log.i("CountryCode",countryCode);


        ArrayAdapter arrayAdapterTwo = new ArrayAdapter(this, android.R.layout.simple_spinner_item, recherche);
        arrayAdapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        Apropos = findViewById(R.id.apropos);
        progressBar = findViewById(R.id.progressBar);
        spinnerTwo.setAdapter(arrayAdapterTwo);
        setImage();
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        place.setText(country);
        phone.setText(user.getCurrentUser().getPhoneNumber());
        getuserdata();

    }

    void toast(String msg) {
        Toast.makeText(SetupActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    void setImage() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(SetupActivity.this);

                    } catch (Exception e) {
                        e.printStackTrace();
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
                            .setQuality(80)
                            .compressToBitmap(actualImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    final_image = baos.toByteArray();
                } catch (Exception e) {

                }

                profile_image.setImageURI(mImageUri);
                ischange = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        interesse = recherche[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void getuserdata() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///////////
                final String apropos = Apropos.getText().toString();
                final String ville = ville_user.getText().toString();
                final String ageUser = age_user.getText().toString();
                final String nom = user_nom.getText().toString();
                final String prenom = user_prenom.getText().toString();
                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                if (radio_femme.isChecked()) {
                    // find the radiobutton by returned id
                    radio_femme = (RadioButton) findViewById(selectedId);
                    sexe = radio_femme.getText().toString();
                } else if (radio_homme.isChecked()) {
                    radio_homme = (RadioButton) findViewById(selectedId);
                    sexe = radio_homme.getText().toString();

                } else {
                    toast("choisir votre sexe svp ");
                }
                //////////
                /////////// envoi des fichier dans la base de donnee
              //  if (ischange) {
                    if (!interesse.equals("Que recherchez vous ?") ) {
                        //
                        if (!TextUtils.isEmpty(ville) && mImageUri != null && !TextUtils.isEmpty(ageUser) && !TextUtils.isEmpty(nom) && !TextUtils.isEmpty(prenom) && !TextUtils.isEmpty(apropos)&& sexe!=null) {

                            button.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);

                            final StorageReference ref = storageReference.child("image_de_profile").child(userID + " .jpg");
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

                                        stockage(task, nom, prenom, ville, ageUser, apropos);

                                    } else {
                                        button.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(), "Erreur , try later ", Toast.LENGTH_LONG).show();
                                        // Handle failures
                                        // ...
                                    }
                                }
                            });
                            ////////fin de l'nvoie

                        } else {
                            button.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "remplir tous les champs", Toast.LENGTH_LONG).show();
                        }
                        //

                    } else {
                        toast("Que recherchez vous ?");
                    }

//                } else {
//                    stockage(null, nom, prenom, ville, ageUser, apropos);
//                }
            }
        });
    }

    public void stockage(@NonNull Task<Uri> task, String nom, String prenom, String ville, String ageUser, String apropos) {
        Uri downloadUri;
        if (task != null) {
            downloadUri = task.getResult();
        } else {
            downloadUri = mImageUri;
        }
        if (downloadUri != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
            String saveCurrentDate = currentDate.format(calendar.getTime());
            String date = saveCurrentDate;
            Map<String, Object> user_data = new HashMap<>();
            user_data.put("nom", nom);
            user_data.put("prenom", prenom);
            user_data.put("pays", country);
            user_data.put("phone", user.getCurrentUser().getPhoneNumber());
            user_data.put("ville", ville);
            user_data.put("age", ageUser);
            user_data.put("sexe", sexe);
            user_data.put("recherche", interesse);
            user_data.put("UpdatedDate", date);
            user_data.put("image", downloadUri.toString());
            user_data.put("forfait", "gratuit");
            user_data.put("id", userID);
            user_data.put("apropos", apropos);
            user_data.put("isOnline", "true");
            user_data.put("countryCode", countryCode);
            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
            userDb.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(SetupActivity.this, ActivityPrincipal.class);
                    startActivity(intent);
                    finish();
                }
            });


        } else {
            toast("choisir une image");
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        user.signOut();
    }
}
