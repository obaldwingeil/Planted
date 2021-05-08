package com.example.afinal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class CheckFragment extends Fragment {

    View view;
    private ImageView imageView_light;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_check, container, false);

        imageView_light = view.findViewById(R.id.imageView_light);
        Picasso.get().load("file:///android_asset/sun.png").into(imageView_light);

        return view;
    }
}
