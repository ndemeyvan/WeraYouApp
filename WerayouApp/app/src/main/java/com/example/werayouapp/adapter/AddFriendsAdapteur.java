package com.example.werayouapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.werayouapp.Activity.ProfilActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.model.FriendsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendsAdapteur extends RecyclerView.Adapter<AddFriendsAdapteur.ViewHolder>  {
     String id_user;
    List<FriendsModel> friendsModelList;
    Context context;
    private String nom;
    private String prenom;
    private String userID;
    FirebaseAuth user ;
    private DatabaseReference usersDb;

    public AddFriendsAdapteur(List<FriendsModel> friendsModelList, Context context) {
        this.friendsModelList = friendsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public AddFriendsAdapteur.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.add_friends_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendsAdapteur.ViewHolder holder, int i) {
        user=FirebaseAuth.getInstance();
        userID=user.getCurrentUser().getUid();
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

        holder.addFirendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance ();
                SimpleDateFormat currentDate=new SimpleDateFormat (" dd MMM yyyy" );
                String saveCurrentDate=currentDate.format ( calendar.getTime () );
                final String date=saveCurrentDate;
                Map<String, String> user_data = new HashMap<>();
                user_data.put ( "updatedDate",date);
                user_data.put("id",id_user);

                /*ici il est question d'ajouter un utilisateur ajouter de la collection de d'amies */
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("mesAmis");
                db.setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        /*ici il est question de supprimer un utilisateur  de la collection de demande d'amies */
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child("connections").child("accepter").child(id_user);
                            db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        ///mettre l'utilisateur dans les valider
                        Map<String, String> user_data = new HashMap<>();
                        user_data.put ( "updatedDate",date);
                        user_data.put("id",id_user);
                        /*ici il est question d'ajouter un utilisateur ajouter de la collection de d'amies */
                        DatabaseReference db_ = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("valider");
                        db_.setValue(user_data);

                    }
                });

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
            addFirendButton=itemView.findViewById(R.id.writeButton);
            deniedFirendButton=itemView.findViewById(R.id.blockButton);

        }

    }
}