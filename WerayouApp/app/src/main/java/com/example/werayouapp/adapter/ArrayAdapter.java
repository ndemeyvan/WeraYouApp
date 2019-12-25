package com.example.werayouapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.werayouapp.R;
import com.example.werayouapp.model.Cards;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArrayAdapter extends android.widget.ArrayAdapter<Cards> {

    Context context;


    public ArrayAdapter(@NonNull Context context, int resource, @NonNull List<Cards> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.nameUser);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageUser);
        TextView cityUser= convertView.findViewById(R.id.cityUser);
        CardView card = convertView.findViewById(R.id.card_item);
//        card.setAnimation ( AnimationUtils.loadAnimation ( context,R.anim.fade_scale ) );

        name.setText(card_item.getNom() +" " + card_item.getPrenom());
        cityUser.setText(card_item.getPays()+" / "+card_item.getVille());
        Picasso.with(convertView.getContext()).load(card_item.getImage()).into(image);
        /*switch(card_item.getImage()){
            case "default":
                break;
            default:

                Picasso.with(convertView.getContext()).load(card_item.getImage()).into(image);
                break;
        }*/


        return convertView;

    }
}
