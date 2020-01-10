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
        TextView apropos = convertView.findViewById(R.id.description);

//        card.setAnimation ( AnimationUtils.loadAnimation ( context,R.anim.fade_scale ) );
        String prenomFinal = card_item.getPrenom().substring(0, 1).toUpperCase() + card_item.getPrenom().substring(1);
        String nomFinal = card_item.getNom().substring(0, 1).toUpperCase() + card_item.getNom().substring(1);

        name.setText(prenomFinal +" " + nomFinal);
        cityUser.setText(card_item.getPays()+" / "+card_item.getVille() + " / " +card_item.getAge() + " ans");
        Picasso.with(convertView.getContext()).load(card_item.getImage()).into(image);
        apropos.setText(card_item.getApropos());
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
