package com.example.werayouapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

        name.setText(card_item.getNom() +" " + card_item.getPrenom());
        cityUser.setText(card_item.getPays()+" / "+card_item.getVille());
        /*switch(card_item.getImage()){
            case "default":
                Picasso.with(convertView.getContext()).load(R.mipmap.ic_launcher).into(image);
                break;
            default:

                Picasso.with(convertView.getContext()).load(card_item.getImage()).into(image);
                break;
        }*/


        return convertView;

    }
}
