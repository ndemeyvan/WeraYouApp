package com.example.werayouapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.werayouapp.R;
import com.squareup.picasso.Picasso;

public class DetailPubActivity extends AppCompatActivity {

    private String image;
    private boolean hasWebsite;
    private String desc;
    private String title;
    private String websiteLink;
    TextView titleTextview;
    TextView description;
    ImageView imagePub;
    Button button;
    Toolbar toolbar;
    String bannerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        image = getIntent().getStringExtra("image");
        hasWebsite = getIntent().getBooleanExtra("hasWebsite", false);
        Log.i("hasWebsite", "" + hasWebsite);
        desc = getIntent().getStringExtra("desc");
        title = getIntent().getStringExtra("title");
        websiteLink = getIntent().getStringExtra("websiteLink");
        bannerImage = getIntent().getStringExtra("bannerImage");

        setContentView(R.layout.activity_detail_pub);
        titleTextview = findViewById(R.id.title);
        description = findViewById(R.id.description);
        imagePub = findViewById(R.id.imagePub);
        button = findViewById(R.id.button);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //
        titleTextview.setText(title);
        description.setText(desc);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
                finish();
            }
        });
        toolbar.setTitle(title);
        Picasso.with(DetailPubActivity.this).load(image).into(imagePub);
        if (hasWebsite == false) {
            button.setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPubActivity.this, WebViewActivity.class);
                intent.putExtra("websiteLink", websiteLink);
                startActivity(intent);
            }
        });

    }
}