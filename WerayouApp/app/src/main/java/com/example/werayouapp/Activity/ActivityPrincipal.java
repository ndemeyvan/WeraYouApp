package com.example.werayouapp.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.werayouapp.Activity.mainFragment.MyFriendFragment;
import com.example.werayouapp.Activity.mainFragment.FriendsFragment;
import com.example.werayouapp.Activity.mainFragment.HomeFragment;
import com.example.werayouapp.Activity.mainFragment.MeFragment;
import com.example.werayouapp.Activity.mainFragment.MessageFragment;
import com.example.werayouapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class ActivityPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //AHBottomNavigation bottomNavigation;
    ImageView add_image;
    Toolbar toolbar;
    TextView toobarTitle;
    SharedPreferences sharedpreferences;
    FirebaseAuth user;
    String userID;
    CountryCodePicker mCountryCode;
    FragmentCommunicator fragmentCommunicator;
    String myPref="countryCode";
    DatabaseReference usersDb;
//  String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        mCountryCode = findViewById(R.id.country_code_text);
        //
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic(userID);
        setStatus("online");
        //
//        getUserData();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        add_image=findViewById(R.id.add_image);
        sharedpreferences =getSharedPreferences(myPref,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains("LastCountryCode")) {
           String contryCode=sharedpreferences.getString("LastCountryCode", "");
            mCountryCode.setDefaultCountryUsingNameCode(contryCode);
            mCountryCode.resetToDefaultCountry();
            Log.i("ValueCode",contryCode);
        }else{
            mCountryCode.setDefaultCountryUsingNameCode("FR");
            mCountryCode.resetToDefaultCountry();
        }

        setSupportActionBar(toolbar);
        BottomNavigationView navigation =  findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new HomeFragment());
        //bottomNavigation.setOnTabSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        toobarTitle = findViewById(R.id.toobarTitle);

        toobarTitle.setText("Werayou");
        HomeFragment homeFragment = new HomeFragment();
        mCountryCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                String countryCode = mCountryCode.getSelectedCountryNameCode();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("LastCountryCode", countryCode);
                editor.commit();
                fragmentCommunicator.passData(mCountryCode.getSelectedCountryName());
            }
        });


        // this.createNavItems();
        showCase();


    }

    void showCase(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "SHOW");
        sequence.setConfig(config);
        sequence.addSequenceItem(mCountryCode,
                "Hi  , cliquez ici pour choisir un pays ou recherchez votre âme sœur. Par defaut elle est sur france et en bas est charger les utilisateurs de votre pays actuel .", "OK");
        sequence.start();

    }

    public void passVal(FragmentCommunicator fragmentCommunicator) {
        this.fragmentCommunicator = fragmentCommunicator;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public interface FragmentCommunicator {
        public void passData(String name);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    toobarTitle.setText("Werayou");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    mCountryCode.setVisibility(View.VISIBLE);
                    return true;
                case R.id.friends:
                    mCountryCode.setVisibility(View.INVISIBLE);
                    toobarTitle.setText("+ d'amis");
                    fragment = new FriendsFragment();
                    loadFragment(fragment);

                    return true;
                case R.id.amies:
                    mCountryCode.setVisibility(View.INVISIBLE);
                    toobarTitle.setText("mes amis");
                    fragment = new MyFriendFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.message:
                    mCountryCode.setVisibility(View.INVISIBLE);
                    toobarTitle.setText("Message");
                    fragment = new MessageFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.me:
                    mCountryCode.setVisibility(View.INVISIBLE);
                    toobarTitle.setText("Moi");
                    fragment = new MeFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_photo:
                Intent gogotoSearch = new Intent(getApplicationContext(), AddPhotoActivity.class);
                startActivity(gogotoSearch);
                //finish
                return true;
            /*case R.id.setting:
                Intent intent = new Intent(ActivityPrincipal.this, SettingActivity.class);
                startActivity(intent);
                //finish();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_photo:
                Intent gogotoSearch = new Intent(getApplicationContext(), AddPhotoActivity.class);
                startActivity(gogotoSearch);
                //finish
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    protected void onStart() {
        super.onStart();
        setStatus("online");
        FirebaseMessaging.getInstance().subscribeToTopic(userID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
        FirebaseMessaging.getInstance().subscribeToTopic(userID);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
        FirebaseMessaging.getInstance().subscribeToTopic(userID);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setStatus("online");
        FirebaseMessaging.getInstance().subscribeToTopic(userID);

    }

//    ///recupere les information de l'utilisateur
//    public void getUserData() {
//        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
//        db.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
//                usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                    data(dataSnapshot);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                data(dataSnapshot);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//
//    }
//
//     void data(DataSnapshot dataSnapshot) {
//        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//            if (map.get("countryCode") != null) {
//                 countryCode = map.get("countryCode").toString();
//            }
//        }
//
//
//    }

}
