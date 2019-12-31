package com.example.werayouapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.werayouapp.R;

public class DetailPhotoActivity extends AppCompatActivity {
    String id_post;
    String id_user;
    String description;
    String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);
        id_post=getIntent().getStringExtra("id_post");
        id_user=getIntent().getStringExtra("id_user");
        description=getIntent().getStringExtra("description");
        image=getIntent().getStringExtra("image");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
