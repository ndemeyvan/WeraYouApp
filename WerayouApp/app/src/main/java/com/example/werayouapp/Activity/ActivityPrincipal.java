package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.example.werayouapp.Utiles.BottomNavigationBehavior;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

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

    //implements AHBottomNavigation.OnTabSelectedListener


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mCountryCode = findViewById(R.id.country_code_text);
        showCase(mCountryCode);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        sharedpreferences =getSharedPreferences(myPref,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains("LastCountryCode")) {
           String contryCode=sharedpreferences.getString("LastCountryCode", "");
            mCountryCode.setDefaultCountryUsingNameCode(contryCode);
            mCountryCode.resetToDefaultCountry();
            Log.i("ValueCode",contryCode);
        }else{
            mCountryCode.setDefaultCountryUsingNameCode("FR");
        }

        setSupportActionBar(toolbar);
        // bottomNavigation= (AHBottomNavigation) findViewById(R.id.bottomNavigationView);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationBehavior());
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
        //
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic(userID);
        setStatus("online");
        //

        // this.createNavItems();

    }

    void showCase(View view){
        new GuideView.Builder(this)
                .setTitle("Guide Title Text")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setTargetView(view)
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .build()
                .show();
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
            /*case R.id.edit:
                Intent intent = new Intent(ActivityPrincipal.this, SettingActivity.class);
                startActivity(intent);
                //finish();
                return true;*/
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

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        setStatus("offline");
//        FirebaseMessaging.getInstance().subscribeToTopic(userID);
//
//    }

    /*private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);


       // private void createNavItems() {
        //CREATE ITEMS
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("", R.drawable.ic_home, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("", R.drawable.ic_aadd_friend, R.color.color_tab_2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("", R.drawable.ic_conversation, R.color.color_tab_3);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("", R.drawable.ic_account, R.color.color_tab_4);


        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        //PROPERTIES
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FFFFFF"));
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigation.setBehaviorTranslationEnabled(false);

// Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#ff2e55"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

// Force to tint the drawable (useful for font with icon for example)
        //bottomNavigation.setForceTint(true);

// Display color under navigation bar (API 21+)
// Don't forget these lines in your style-v21
// <item name="android:windowTranslucentNavigation">true</item>
//<item name="android:fitsSystemWindows">true</item>
        //bottomNavigation.setTranslucentNavigationEnabled(true);
        //bottomNavigation.setColored(true);
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#ff2e55"));
        //
        // Add or remove notification for each item
        //bottomNavigation.setNotification("1", 0);*/
// OR
      /* AHNotification notification = new AHNotification.Builder()
                .setText("new")
                .setBackgroundColor(ContextCompat.getColor(ActivityPrincipal.this, R.color.colorPrimary))
                .setTextColor(ContextCompat.getColor(ActivityPrincipal.this, R.color.white))
                .build();
        bottomNavigation.setNotification(notification, 1);
        //*/


}




  /*  @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        if(position==0) {
            HomeFragment home=new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,home).commit();
            toobarTitle.setText("Werayou");
            return true;
        }else if(position==1) {
            FriendsFragment friend=new FriendsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,friend).commit();
            toobarTitle.setText("+ d'amies");
            return true;
        }else if(position==2) {
            MessageFragment message=new MessageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,message).commit();
            toobarTitle.setText("Messages");
            return true;
        }
        else if(position==3) {
            MeFragment me=new MeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,me).commit();
            toobarTitle.setText("Moi");
            return true;
        }

        return false;
    }*/

