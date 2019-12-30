package com.example.werayouapp.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.werayouapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import id.zelory.compressor.Compressor;

public class AddPhotoActivity extends AppCompatActivity {
    ImageView image;
    EditText post_description;
    Button post_button;
    ProgressBar progressBar;
    Toolbar toolbar;
    Uri mImageUri;
    byte[] final_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        image=findViewById(R.id.image);
        post_description=findViewById(R.id.post_description);
        post_button=findViewById(R.id.post_button);
        progressBar=findViewById(R.id.progressBar);
        toolbar=findViewById(R.id.toolbar);
        setImage();
    }


    void makeToast(String msg){
        Toast.makeText(AddPhotoActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    void setImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddPhotoActivity.this);

            }catch (Exception e){
                e.printStackTrace ();
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(AddPhotoActivity.this);
        }

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
                image.setImageURI ( mImageUri );
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddPhotoActivity.this,ActivityPrincipal.class);
        startActivity(intent);
        finish();
    }
}
