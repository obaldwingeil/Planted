package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class PhotosFragment extends Fragment {

    View view;
    private Context context;

    private FlexboxLayout flex;

    PlantActivity activity;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_photos, container, false);
        context = view.getContext();

        flex = view.findViewById(R.id.flexBox_photos);
        activity = (PlantActivity)getActivity();

        try {
            JSONArray plant = new JSONArray(activity.plant);
            JSONObject plantObj = plant.getJSONObject(0);
            JSONArray images = plantObj.getJSONArray("photos");

            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8,8,8,8);
            lp.setWidth(400);
            lp.setHeight(400);
            for(int i = 0; i < images.length(); i++){
                ImageView photo = new ImageView(context);
                photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photo.setLayoutParams(lp);
                storageReference.child(images.getString(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).rotate(90).into(photo);
                    }
                });
                flex.addView(photo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}
