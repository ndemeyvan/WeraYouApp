package com.example.werayouapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.werayouapp.R;
import com.example.werayouapp.intro.WelcomeActivity;

public class SplashcreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashcreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent gotochoice = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(gotochoice);
                finish();
            }
        }, 2000);
        //SystemClock.sleep(3000);

    }
}
