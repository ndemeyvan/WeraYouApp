package com.example.werayouapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.werayouapp.Activity.DetailPhotoActivity;
import com.example.werayouapp.Activity.ProfilActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.model.FriendsModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapteur extends RecyclerView.Adapter<FriendsAdapteur.ViewHolder>  {
     String id_user;
    List<FriendsModel> friendsModelList;
    Context context;
    private String nom;
    private String prenom;
    private DatabaseReference usersDb;

    public FriendsAdapteur(List<FriendsModel> friendsModelList, Context context) {
        this.friendsModelList = friendsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendsAdapteur.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.add_friends_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapteur.ViewHolder holder, int i) {
       id_user = friendsModelList.get(i).getId();
        getUserData(holder,id_user);
        holder.seeProfilText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfilActivity.class);
                intent.putExtra("id",id_user);
                context.startActivity(intent);

            }
        });
        holder.profil_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfilActivity.class);
                intent.putExtra("id",id_user);
                context.startActivity(intent);
            }
        });
    }


    public void getUserData(final ViewHolder holder , String id){
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if(map.get("nom")!=null){
                                nom = map.get("nom").toString();
                            }
                            if(map.get("prenom")!=null){
                                prenom = map.get("prenom").toString();
                                String nomFinal = nom.substring(0, 1).toUpperCase() + nom.substring(1);
                                String prenomFinal = prenom.substring(0, 1).toUpperCase() + prenom.substring(1);
                                holder.nom_profil.setText(prenomFinal +" " + nomFinal);
                            }

                            if(map.get("image")!=null){
                                String image = map.get("image").toString();
                                Picasso.with(context).load(image).into(holder.profil_image);
                                holder.progressBar.setVisibility(View.INVISIBLE);

                            }

                            //
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



    }

    @Override
    public int getItemCount() {
        return friendsModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profil_image;
        ProgressBar progressBar;
        TextView nom_profil;
        TextView seeProfilText;
        Button addFirendButton;
        Button deniedFirendButton;


        public ViewHolder(final View itemView) {
            super(itemView);
            profil_image=itemView.findViewById(R.id.profil_image);
            progressBar=itemView.findViewById(R.id.progressBar);
            nom_profil=itemView.findViewById(R.id.nom_profil);
            seeProfilText=itemView.findViewById(R.id.seeProfilText);
            addFirendButton=itemView.findViewById(R.id.addFirendButton);
            deniedFirendButton=itemView.findViewById(R.id.deniedFirendButton);

        }

    }
}
