package com.example.werayouapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.werayouapp.R;

public class AddPhotoActivity extends AppCompatActivity {
    ImageView image;
    EditText post_description;
    Button post_button;
    ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        image=findViewById(R.id.image);
        post_description=findViewById(R.id.post_description);
        post_button=findViewById(R.id.post_button);
        progressBar=findViewById(R.id.progressBar);
        toolbar=findViewById(R.id.toolbar);
    }
}
