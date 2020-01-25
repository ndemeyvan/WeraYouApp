package com.example.werayouapp.UtilsForChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.werayouapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    String image_profil;
    FirebaseAuth firebaseAuth;
    private Context context;
    String current_user;
    final int MSG_TYPE_RIGHT=1;
    final int MSG_TYPE_LEFT=0;
    private boolean ischat;
    private List<ModelChat> modelChatList;

    public ChatAdapter(Context context, List<ModelChat> modelChatList,boolean ischat) {
        this.context = context;
        this.modelChatList = modelChatList;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        viewGroup.getContext();
        firebaseAuth=FirebaseAuth.getInstance();
        if(i==MSG_TYPE_RIGHT){
            View v=LayoutInflater.from ( viewGroup.getContext () ).inflate ( R.layout.right_item_chat ,viewGroup,false);
            return new ViewHolder ( v );

        }else{
            View v= LayoutInflater.from ( viewGroup.getContext () ).inflate ( R.layout.left_item_chat ,viewGroup,false);
            return new ViewHolder ( v );
        }


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        ModelChat modelChat=modelChatList.get ( i );
        viewHolder.message.setText ( modelChat.getMessage () );



    }

    @Override
    public int getItemCount() {
        return modelChatList.size ();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        CircleImageView image;
        CircleImageView online_status;
        CircleImageView offline_status;
        // ConstraintLayout right_constraint;

        TextView current_date;
        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            message=itemView.findViewById ( R.id.show_message );
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth= FirebaseAuth.getInstance ();
        current_user=firebaseAuth.getCurrentUser ().getUid ();
        if (modelChatList.get ( position ).getExpediteur ().equals ( current_user )){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }

    }
}
