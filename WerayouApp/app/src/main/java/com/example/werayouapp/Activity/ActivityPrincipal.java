package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.werayouapp.Activity.mainFragment.FriendsFragment;
import com.example.werayouapp.Activity.mainFragment.HomeFragment;
import com.example.werayouapp.Activity.mainFragment.MeFragment;
import com.example.werayouapp.Activity.mainFragment.MessageFragment;
import com.example.werayouapp.R;
public class ActivityPrincipal extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener{

    AHBottomNavigation bottomNavigation;
    ImageView add_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigation= (AHBottomNavigation) findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnTabSelectedListener(this);
        add_image=findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gogotoSearch = new Intent(getApplicationContext(),AddPhotoActivity.class);
                startActivity(gogotoSearch);
                //overridePendingTransition(R.anim.slide_in_right, R.anim.translate);
                //finish();
            }
        });
        this.createNavItems();

    }

    private void createNavItems() {
        //CREATE ITEMS
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("", R.drawable.ic_home, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("", R.drawable.ic_aadd_friend, R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("", R.drawable.ic_conversation, R.color.green);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("", R.drawable.ic_account, R.color.black);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        //PROPERTIES
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        bottomNavigation.setCurrentItem(0);

    }


    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        if(position==0) {
            HomeFragment home=new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,home).commit();
            return true;
        }else if(position==1) {
            FriendsFragment friend=new FriendsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,friend).commit();
            return true;
        }else if(position==2) {
            MessageFragment message=new MessageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,message).commit();
            return true;
        }
        else if(position==3) {
            MeFragment me=new MeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,me).commit();
            return true;
        }

        return false;
    }
}
