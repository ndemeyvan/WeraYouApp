package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.werayouapp.R;
import com.example.werayouapp.intro.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class SplashcreenActivity extends AppCompatActivity {
    //
    FirebaseAuth user;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashcreen);

        //
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic(userID);
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
                finish();

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
