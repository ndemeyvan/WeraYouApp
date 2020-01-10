package com.example.werayouapp.Activity.mainFragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.werayouapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    TextView message;


    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        message=v.findViewById(R.id.message);


        return v;
    }

}
