package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class UserFragment extends Fragment {

    View view;

    TextView textView_name;
    RecyclerView recyclerView_myPlants;
    RecyclerView recyclerView_myReviews;
    FlexboxLayout flex;

    ArrayList<Plant> myPlants;
    ArrayList<Review> myReviews;

    PlantDAO plantDAO;

    private String api_root;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static AsyncHttpClient client2 = new AsyncHttpClient();
    private static AsyncHttpClient client3 = new AsyncHttpClient();

    Context context;
    HomeActivity activity;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        context = view.getContext();

        api_root = context.getString(R.string.api_root);

        activity = (HomeActivity)getActivity();

        textView_name = view.findViewById(R.id.textView_userName);
        textView_name.setText(activity.name);

        recyclerView_myPlants = view.findViewById(R.id.recyclerView_myPlants);
        recyclerView_myReviews = view.findViewById(R.id.recyclerView_myReviews);
        flex = view.findViewById(R.id.flexBox);

        myPlants = new ArrayList<>();
        myReviews = new ArrayList<>();

        plantDAO = new PlantDAO();

        // myPlants = plantDAO.getUserById(_id);
        getMyPlants();
        getMyReviews();
        getMyImages();

        return view;
    }

    public void getMyPlants(){
        String api = api_root + "result/user/plants/" + activity._id;
        client.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("plants", new String(responseBody));
                try {
                    JSONArray result = new JSONArray(new String(responseBody));
                    for(int i = 0; i < result.length(); i++){
                        JSONObject plantObject = result.getJSONObject(i);
                        Plant plant = new Plant(
                                plantObject.getInt("_id"),
                                plantObject.getString("name"),
                                plantObject.getString("description"),
                                plantObject.getDouble("rating"),
                                plantObject.getJSONArray("images").getString(0),
                                true
                        );
                        myPlants.add(plant);
                    }
                    PlantAdapter adapter = new PlantAdapter(myPlants);
                    recyclerView_myPlants.setAdapter(adapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    recyclerView_myPlants.setLayoutManager(layoutManager);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Toast.makeText(context, "Incorrect email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getMyReviews(){
        String api = api_root + "result/user/reviews/" + activity._id;
        client2.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("reviews", new String(responseBody));
                try {
                    JSONArray result = new JSONArray(new String(responseBody));
                    for(int i = 0; i < result.length(); i++) {
                        JSONObject reviewObject = result.getJSONObject(i);
                        JSONArray images = reviewObject.getJSONArray("images");
                        ArrayList<String> imageTemp = new ArrayList<>();
                        for(int j = 0; j < images.length(); j++){
                            imageTemp.add(images.getString(j));
                        }
                        Review review = new Review(
                                reviewObject.getString("plant_name"),
                                reviewObject.getString("text"),
                                reviewObject.getDouble("rating"),
                                imageTemp
                        );
                        myReviews.add(review);
                    }
                    // Log.d("myReviews", myReviews.toString());
                    ReviewAdapter adapter = new ReviewAdapter(myReviews);
                    recyclerView_myReviews.setAdapter(adapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    recyclerView_myReviews.setLayoutManager(layoutManager);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void getMyImages(){
        String api = api_root + "result/user/photos/" + activity._id;
        client3.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("images", new String(responseBody));
                try {
                    JSONArray images = new JSONArray(new String(responseBody));
                    for(int i = 0; i < images.length(); i++){
                        ImageView photo = new ImageView(context);
                        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(8,8,8,8);
                        lp.setWidth(400);
                        lp.setHeight(400);
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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
