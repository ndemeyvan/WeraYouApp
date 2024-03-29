package com.example.werayouapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.werayouapp.Activity.ProfilActivity;
import com.example.werayouapp.Activity.SettingActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.model.CommentModel;
import com.example.werayouapp.model.Post;
import com.google.firebase.auth.FirebaseAuth;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    List<CommentModel> commentModelList;
    Context context;
    private DatabaseReference usersDb;
    private String nom;
    private String prenom;
    private String userID;
    FirebaseAuth user;


    public CommentAdapter(List<CommentModel> commentModelList, Context context) {
        this.commentModelList = commentModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.comment_message_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        final String id = commentModelList.get(i).getId();
        String commentaire = commentModelList.get(i).getCommentaire();
        String createdDate = commentModelList.get(i).getCreatedDate();
        holder.commentaire.setText(commentaire);
        holder.createdDate.setText(createdDate);
        holder.layout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_simple));
        getData(holder, id);


    }

    public void getData(final ViewHolder holder, final String id) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
                usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if (map.get("nom") != null) {
                                nom = map.get("nom").toString();

                            }
                            if (map.get("prenom") != null) {
                                prenom = map.get("prenom").toString();
                                String nomFinal = nom.substring(0, 1).toUpperCase() + nom.substring(1);
                                String prenomFinal = prenom.substring(0, 1).toUpperCase() + prenom.substring(1);
                                holder.nom_profil.setText(prenomFinal + " " + nomFinal);

                            }
                            if (map.get("image") != null) {
                                String profileImageUrl = map.get("image").toString();
                                Picasso.with(context).load(profileImageUrl).into(holder.profil_image);
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
        return commentModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profil_image;
        ProgressBar progressBar;
        TextView nom_profil;
        TextView commentaire;
        TextView createdDate;
        ConstraintLayout layout;


        public ViewHolder(final View itemView) {
            super(itemView);
            profil_image = itemView.findViewById(R.id.profil_image);
            progressBar = itemView.findViewById(R.id.progressBar);
            nom_profil = itemView.findViewById(R.id.nom_profil);
            commentaire = itemView.findViewById(R.id.commentaire);
            createdDate = itemView.findViewById(R.id.createdDate);
            layout = itemView.findViewById(R.id.layout);
        }

    }
}
