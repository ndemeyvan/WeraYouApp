package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.werayouapp.Activity.mainFragment.MyFriendFragment;
import com.example.werayouapp.Activity.mainFragment.FriendsFragment;
import com.example.werayouapp.Activity.mainFragment.HomeFragment;
import com.example.werayouapp.Activity.mainFragment.MeFragment;
import com.example.werayouapp.Activity.mainFragment.MessageFragment;
import com.example.werayouapp.R;
import com.example.werayouapp.login.LoginActivity;
import com.example.werayouapp.login.OtpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class ActivityPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    AHBottomNavigation bottomNavigation;
    TextView add_image;
    Toolbar toolbar;
    TextView toobarTitle;
    SharedPreferences sharedpreferences;
    SharedPreferences firstOpenSharedpreferences;
    FirebaseAuth user;
    String userID;
    CountryCodePicker mCountryCode;
    FragmentCommunicator fragmentCommunicator;
    String myPref = "countryCode";
    DatabaseReference usersDb;
    String countryCode;
    String firstOpen = "firstOpen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        mCountryCode = findViewById(R.id.country_code_text);
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbar);
        add_image = findViewById(R.id.add_image);
        sharedpreferences = getSharedPreferences(myPref,
                Context.MODE_PRIVATE);
        firstOpenSharedpreferences = getSharedPreferences(firstOpen,
                Context.MODE_PRIVATE);

        // setStatus("online");

        if (getIntent().hasExtra("chat_notification")) {
            Intent intent = new Intent(ActivityPrincipal.this, ChatActivity.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(intent);
        } else if (getIntent().hasExtra("post_notification")) {
            Intent intent = new Intent(ActivityPrincipal.this, ChatActivity.class);
            intent.putExtra("id_post", getIntent().getStringExtra("id_post"));
            intent.putExtra("id_user", getIntent().getStringExtra("id_user"));
            intent.putExtra("description", getIntent().getStringExtra("description"));
            intent.putExtra("image", getIntent().getStringExtra("image"));
            intent.putExtra("date", getIntent().getStringExtra("date"));
            startActivity(intent);
        }

        if (sharedpreferences.contains("LastCountryCode")) {
            String contryCode = sharedpreferences.getString("LastCountryCode", "");
            mCountryCode.setDefaultCountryUsingNameCode(contryCode.toLowerCase());
            mCountryCode.resetToDefaultCountry();
            Log.i("ValueCode", contryCode);
        } else {
            usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
            usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("countryCode").getValue() != null) {
                            countryCode = dataSnapshot.child("countryCode").getValue().toString();
                            mCountryCode.setDefaultCountryUsingNameCode(countryCode);
                            mCountryCode.resetToDefaultCountry();
                            Log.i("ValueCode", "lol");

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorPrimary));
        this.createNavItems();

        toolbar = findViewById(R.id.toolbar);
        toobarTitle = findViewById(R.id.toobarTitle);
        toobarTitle.setText("Werayou");
        loadFragment(new HomeFragment());

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

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gogotoSearch = new Intent(getApplicationContext(), AddPhotoActivity.class);
                startActivity(gogotoSearch);
            }
        });

        showCase();
        checkIfHaveAccount();

    }

    void notification(int i) {
        bottomNavigation.setNotification("new", i);
        bottomNavigation.setNotificationBackgroundColor(getResources().getColor(R.color.green));
    }

    private void createNavItems() {
        //CREATE ITEMS
        AHBottomNavigationItem home = new AHBottomNavigationItem("Werayou", R.drawable.ic_home);
        AHBottomNavigationItem addfriend = new AHBottomNavigationItem("Ask", R.drawable.ic_add_friend);
        AHBottomNavigationItem friend = new AHBottomNavigationItem("Amis", R.drawable.ic_friend);
        AHBottomNavigationItem messages = new AHBottomNavigationItem("Messages", R.drawable.ic_conversation);
        AHBottomNavigationItem me = new AHBottomNavigationItem("Moi", R.drawable.ic_account);
        //ADD ITEMS TO BAR
        bottomNavigation.addItem(home);
        bottomNavigation.addItem(addfriend);
        bottomNavigation.addItem(friend);
        bottomNavigation.addItem(messages);
        bottomNavigation.addItem(me);
        //notification
        checkifHavenotification();
        //PROPERTIES
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position == 0) {
                    toobarTitle.setText("Werayou");
                    HomeFragment fragment = new HomeFragment();
                    loadFragment(fragment);
                } else if (position == 1) {
                    toobarTitle.setText("");
                    FriendsFragment fragment = new FriendsFragment();
                    loadFragment(fragment);
                    checkifHavenotification();
                    updateNotification("newFriendNotif", false);
                } else if (position == 2) {
                    toobarTitle.setText("");
                    MyFriendFragment fragment = new MyFriendFragment();
                    loadFragment(fragment);
                } else if (position == 3) {
                    toobarTitle.setText("");
                    MessageFragment fragment = new MessageFragment();
                    loadFragment(fragment);
                    checkifHavenotification();
                    updateNotification("newMessageNotif", false);
                } else if (position == 4) {
                    toobarTitle.setText("");
                    MeFragment fragment = new MeFragment();
                    loadFragment(fragment);
                }
                return true;
            }
        });

    }

    void updateNotification(final String key, final boolean status) {
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> user_data = new HashMap<>();
                    user_data.put(key, status);
                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                    userDb.child(key).setValue(status);
                } else {
                    if (users != null) {
                        user.signOut();
                        SharedPreferences preferences = getSharedPreferences(myPref, Context.MODE_PRIVATE);
                        SharedPreferences FirstOpenPreferences = getSharedPreferences(firstOpen, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        SharedPreferences.Editor firstOpeneditor = FirstOpenPreferences.edit();
                        firstOpeneditor.clear();
                        firstOpeneditor.apply();
                        editor.clear();
                        editor.apply();
                        Intent homeIntent = new Intent(ActivityPrincipal.this, LoginActivity.class);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        SharedPreferences preferences = getSharedPreferences(myPref, Context.MODE_PRIVATE);
                        SharedPreferences FirstOpenPreferences = getSharedPreferences(firstOpen, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        SharedPreferences.Editor firstOpeneditor = FirstOpenPreferences.edit();
                        firstOpeneditor.clear();
                        firstOpeneditor.apply();
                        editor.clear();
                        editor.apply();
                        Intent homeIntent = new Intent(ActivityPrincipal.this, LoginActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void checkIfHaveAccount() {
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (userID != null) {
                        user.signOut();
                        SharedPreferences preferences = getSharedPreferences(myPref, Context.MODE_PRIVATE);
                        SharedPreferences FirstOpenPreferences = getSharedPreferences(firstOpen, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        SharedPreferences.Editor firstOpeneditor = FirstOpenPreferences.edit();
                        firstOpeneditor.clear();
                        firstOpeneditor.apply();
                        editor.clear();
                        editor.apply();
                        Intent homeIntent = new Intent(ActivityPrincipal.this, LoginActivity.class);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        SharedPreferences preferences = getSharedPreferences(myPref, Context.MODE_PRIVATE);
                        SharedPreferences FirstOpenPreferences = getSharedPreferences(firstOpen, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        SharedPreferences.Editor firstOpeneditor = FirstOpenPreferences.edit();
                        firstOpeneditor.clear();
                        firstOpeneditor.apply();
                        editor.clear();
                        editor.apply();
                        Intent homeIntent = new Intent(ActivityPrincipal.this, LoginActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                }else{
                    //statement
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    void checkifHavenotification() {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("newFriendNotif") != null) {
                        boolean friendNotif = (boolean) map.get("newFriendNotif");
                        if (friendNotif == true) {
                            notification(1);
                        } else {
                            bottomNavigation.setNotification("", 1);
                        }
                    }
                    if (map.get("newMessageNotif") != null) {
                        boolean messageNotif = (boolean) map.get("newMessageNotif");
                        if (messageNotif == true) {
                            notification(3);
                        } else {
                            bottomNavigation.setNotification("", 3);
                        }
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    void showCase() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "SHOW");
        sequence.setConfig(config);
        sequence.addSequenceItem(mCountryCode,
                getResources().getString(R.string.make_search), "OK");
        sequence.start();

    }

    public interface FragmentCommunicator {
        void passData(String name);
    }

    public void passVal(FragmentCommunicator fragmentCommunicator) {
        this.fragmentCommunicator = fragmentCommunicator;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

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
                    toobarTitle.setText(getResources().getString(R.string.make_search));
                    fragment = new FriendsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.amies:
                    mCountryCode.setVisibility(View.INVISIBLE);
                    toobarTitle.setText(getResources().getString(R.string.my_firends));
                    fragment = new MyFriendFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.message:
                    mCountryCode.setVisibility(View.INVISIBLE);
                    toobarTitle.setText("Messages");
                    fragment = new MessageFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.me:
                    mCountryCode.setVisibility(View.INVISIBLE);
                    toobarTitle.setText(getResources().getString(R.string.me));
                    fragment = new MeFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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


    void setStatus(final String status) {
        //    Map<String, Object> user_data = new HashMap<>();
//        user_data.put("isOnline", status);
//        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
//        userDb.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//            }
//        });

//            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
//            userDb.child("isOnline").setValue(status);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //setStatus("online");
        FirebaseMessaging.getInstance().subscribeToTopic(userID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //setStatus("offline");
        FirebaseMessaging.getInstance().subscribeToTopic(userID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setStatus("online");
        FirebaseMessaging.getInstance().subscribeToTopic(userID);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //setStatus("online");
        FirebaseMessaging.getInstance().subscribeToTopic(userID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //setStatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //setStatus("offline");
    }
}
