package com.example.werayouapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.werayouapp.Activity.DetailPhotoActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> postList;
    Context context;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final String imagelink = postList.get(i).getImage();
        final String id_post = postList.get(i).getId_post();
        final String id_user = postList.get(i).getId_user();
        final String description = postList.get(i).getDescription();
        final String date = postList.get(i).getCreatedDate();

        Picasso.with(context).load(imagelink).into(holder.image);
        holder.progressBar.setVisibility(View.INVISIBLE);
        //holder.card_view.setAnimation ( AnimationUtils.loadAnimation ( context,R.anim.fade_scale ) );
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailPhotoActivity.class);
                intent.putExtra("id_post", id_post);
                intent.putExtra("id_user", id_user);
                intent.putExtra("description", description);
                intent.putExtra("image", imagelink);
                intent.putExtra("date", date);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public ProgressBar progressBar;
        public CardView card_view;


        public ViewHolder(final View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progressBar);
            card_view = itemView.findViewById(R.id.card_view);
        }

    }

}
