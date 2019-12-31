package com.example.werayouapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.werayouapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPhotoActivity extends AppCompatActivity {
    String id_post;
    String id_user;
    String description;
    String image;
    String date;
    //
    CircleImageView profil_image;
    TextView nom_profil;
    TextView date_publication;
    TextView description_view;
    ImageView imageView;
    ImageView like_image;
    TextView like_count;
    TextView comment_count;
    RecyclerView recyclerView;
    EditText comment_edittext;
    ImageButton send_comment_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);
        id_post=getIntent().getStringExtra("id_post");
        id_user=getIntent().getStringExtra("id_user");
        description=getIntent().getStringExtra("description");
        image=getIntent().getStringExtra("image");
        date=getIntent().getStringExtra("date");
        //
        profil_image=findViewById(R.id.profil_image);
        nom_profil=findViewById(R.id.nom_profil);
        date_publication=findViewById(R.id.date_publication);
        description_view=findViewById(R.id.description_view);
        imageView=findViewById(R.id.image);
        like_image=findViewById(R.id.like_image);
        like_count=findViewById(R.id.like_count);
        comment_count=findViewById(R.id.comment_count);
        recyclerView=findViewById(R.id.recyclerView);
        comment_edittext=findViewById(R.id.comment_edittext);
        send_comment_button=findViewById(R.id.send_comment_button);








    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
