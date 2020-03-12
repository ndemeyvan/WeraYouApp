package com.example.werayouapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.werayouapp.Activity.ChatActivity;
import com.example.werayouapp.Activity.ProfilActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.model.MyFriendModel;
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

public class MyFriendAdapter extends RecyclerView.Adapter<MyFriendAdapter.ViewHolder> {
    List<MyFriendModel> myFriendModelList;
    Context context;
    private String nom;
    private String prenom;
    private FirebaseAuth user;
    private String userID;
    private DatabaseReference usersDb;
    private boolean isLock;
    private boolean iamblocked;
    String id;


    public MyFriendAdapter(List<MyFriendModel> myFriendModelList, Context context) {
        this.myFriendModelList = myFriendModelList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.my_friend_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyFriendAdapter.ViewHolder holder, final int i) {

        user = FirebaseAuth.getInstance();
        userID = user.getCurrentUser().getUid();
        final String id_user = myFriendModelList.get(i).getId();
        getUserData(holder, id_user);
        checkifIsBlocked(holder, userID, id_user);
        //id =id_user;

        holder.writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id", id_user);
                context.startActivity(intent);


            }
        });
        //
        holder.seeProfilText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfilActivity.class);
                intent.putExtra("id", id_user);
                context.startActivity(intent);

            }
        });

        holder.profil_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfilActivity.class);
                intent.putExtra("id", id_user);
                context.startActivity(intent);
            }
        });


        holder.blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkifIsBlocked(holder, userID, id_user);
                //block user here
                if (isLock == false) {
                    blockUser(userID, id_user);
                    //checkifIsBlocked(holder, userID, id_user);
                } else {
                    unLock(holder,userID,id_user);
                }

            }
        });

        holder.layout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_simple));

    }

    @Override
    public int getItemCount() {
        return myFriendModelList.size();
    }

    public void getUserData(final ViewHolder holder, final String id) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
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

    void checkifIsBlocked(final ViewHolder viewHolder, final String UserId, final String id_user) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(UserId).child("connections").child("bloquer").child(id_user);
        db.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if (dataSnapshot.exists()) {
                 Log.i("Status ", "je bloque");
                 viewHolder.blockButton.setText("Debloquer");
                 viewHolder.writeButton.setBackgroundColor(Color.parseColor("#999999"));
                 viewHolder.writeButton.setTextColor(Color.parseColor("#000000"));
                 isLock = true;
             } else {
                 Log.i("Status ", "je bloque pas");
                 isLock = false;
                 viewHolder.blockButton.setText("Bloquer");
                 viewHolder.writeButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                 viewHolder.writeButton.setTextColor(Color.parseColor("#FFFFFF"));
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     }

        );
    }

    void blockUser(String userID, String id_user) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat(" dd MMM yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        final String date = saveCurrentDate;
        final Map<String, String> user_data = new HashMap<>();
        user_data.put("updatedDate", date);
        user_data.put("id", id);
        DatabaseReference boquer = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("bloquer").child(id_user);
        boquer.setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                makeToast("bloquer");
                isLock = true;
            }
        });
    }

    void unLock(final ViewHolder viewHolder, final String userID, final String id_user) {
        /*ici il est question de supprimer un utilisateur  de la collection de demande d'amies */
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("connections").child("bloquer").child(id_user);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                    makeToast("Debloquer");
                    viewHolder.blockButton.setText("Bloquer");
                    viewHolder.writeButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                    viewHolder.writeButton.setTextColor(Color.parseColor("#FFFFFF"));
                    //checkifIsBlcoked(viewHolder, userID, id_user);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void checkifSheBlcokedMe(final ViewHolder holder, String id_user, String userID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(id_user).child("connections").child("bloquer").child(userID);
        db.addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         if (dataSnapshot.exists()) {
                                             Log.i("Status ", "il ma bloquer");
                                             holder.blockButton.setVisibility(View.INVISIBLE);
                                             holder.writeButton.setVisibility(View.INVISIBLE);
                                             iamblocked = true;
                                         } else {
                                             iamblocked = false;
                                         }
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 }


//                addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("bloquer").hasChild(userID)) {
//                    holder.blockButton.setVisibility(View.INVISIBLE);
//                    holder.writeButton.setVisibility(View.INVISIBLE);
//                    iamblocked = false;
//                } else {
//                    iamblocked = true;
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
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
//
//            }
//        }

        );
    }

    void makeToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profil_image;
        ProgressBar progressBar;
        TextView nom_profil;
        TextView seeProfilText;
        Button writeButton;
        Button blockButton;
        ConstraintLayout layout;


        public ViewHolder(final View itemView) {
            super(itemView);
            profil_image = itemView.findViewById(R.id.profil_image);
            progressBar = itemView.findViewById(R.id.progressBar);
            nom_profil = itemView.findViewById(R.id.nom_profil);
            seeProfilText = itemView.findViewById(R.id.seeProfilText);
            writeButton = itemView.findViewById(R.id.writeButton);
            blockButton = itemView.findViewById(R.id.blockButton);
            layout = itemView.findViewById(R.id.layout);

        }

    }
}
