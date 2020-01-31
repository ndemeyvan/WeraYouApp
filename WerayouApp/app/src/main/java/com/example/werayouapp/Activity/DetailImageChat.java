package com.example.werayouapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Magnifier;

import com.bumptech.glide.Glide;
import com.example.werayouapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

public class DetailImageChat extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    ImageView imageView;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    private String imageLink;
    private static final int code=1000;

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

        Picasso.with(this)
                .load(imageLink)
                .into(imageView);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.download_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.image:
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                            PackageManager.PERMISSION_DENIED){
                        //la permission a ete refuse , redemande
                        String[] permission ={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,code);
                    }else{
                        //la permission a deja ete accorde
                        donwload();
                    }
                }else{
                    //le systeme est inferieur a android marshmallow
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.image:
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                            PackageManager.PERMISSION_DENIED){
                        //la permission a ete refuse , redemande
                        String[] permission ={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,code);
                    }else{
                        //la permission a deja ete accorde
                        donwload();
                    }
                }else{
                    //le systeme est inferieur a android marshmallow
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void donwload(){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageLink));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|
                DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");
        request.setDescription("telechargement en cour ...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS," " + System.currentTimeMillis());
        DownloadManager manager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode){
                case code: {
                    if (grantResults.length>0&&grantResults[0]==
                    PackageManager.PERMISSION_GRANTED){
                        donwload();
                    }else{

                    }
                }
            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
