package com.example.werayouapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.werayouapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailImageChat extends AppCompatActivity {

    ImageView imageView;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    private String imageLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image_chat);
        imageView=findViewById(R.id.image);
        toolbar=findViewById(R.id.toolbar);
        floatingActionButton=findViewById(R.id.floatingActionButton);
        imageLink=getIntent().getStringExtra("image");
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

        Glide.with(this)
                .load(imageLink)
                .into(imageView);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
