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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.werayouapp.Activity.ChatActivity;
import com.example.werayouapp.Activity.ProfilActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.model.CommentModel;
import com.example.werayouapp.model.LastMessageModel;
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

public class LastMessageChatAdapter extends RecyclerView.Adapter<LastMessageChatAdapter.ViewHolder>{


    List<LastMessageModel> lastMessageModelList;
    Context context;
    private DatabaseReference usersDb;
    private String nom;
    private String prenom;
    private String userID;
    FirebaseAuth user ;

    public LastMessageChatAdapter(List<LastMessageModel> lastMessageModelList, Context context) {
        this.lastMessageModelList = lastMessageModelList;
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
        user=FirebaseAuth.getInstance();
        userID=user.getCurrentUser().getUid();
        final String id = lastMessageModelList.get(i).getId_recepteur();
        String dernier_message = lastMessageModelList.get(i).getDernier_message();
        holder.createdDate.setVisibility(View.GONE);
        if (dernier_message.equals("image")){
                holder.image.setVisibility(View.VISIBLE);
                holder.commentaire.setVisibility(View.GONE);
        }else {
            holder.commentaire.setText(dernier_message);
            holder.commentaire.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.INVISIBLE);

        }
        holder.layout.setAnimation ( AnimationUtils.loadAnimation ( context,R.anim.fade_simple ) );
        holder.profil_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!id.equals(userID)){
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("id",id);
                    context.startActivity(intent);
                }
            }
        });
        getData(holder,id);


    }

    public void getData(final ViewHolder holder, final String id){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
                usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                holder.nom_profil.setText(prenomFinal + " " + nomFinal);

                            }
                            if(map.get("image")!=null){
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
        return lastMessageModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profil_image;
        ProgressBar progressBar;
        TextView nom_profil;
        TextView commentaire;
        TextView createdDate;
        ConstraintLayout layout;
        ImageView image;


        public ViewHolder(final View itemView) {
            super(itemView);
            profil_image=itemView.findViewById(R.id.profil_image);
            progressBar=itemView.findViewById(R.id.progressBar);
            nom_profil=itemView.findViewById(R.id.nom_profil);
            commentaire=itemView.findViewById(R.id.commentaire);
            createdDate=itemView.findViewById(R.id.createdDate);
            image=itemView.findViewById(R.id.image);
            layout = itemView.findViewById(R.id.layout);
        }

    }
}
