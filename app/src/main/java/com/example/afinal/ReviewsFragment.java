package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment {

    View view;
    private Context context;

    PlantActivity activity;

    private ProgressBar progressBar_5;
    private ProgressBar progressBar_4;
    private ProgressBar progressBar_3;
    private ProgressBar progressBar_2;
    private ProgressBar progressBar_1;
    private TextView textView_overAll;
    private RatingBar ratingBar_overAll;
    private TextView textView_numReviews;
    private RecyclerView recyclerView_reviews;
    private Button button_write;

    private ArrayList<Review> reviewList;

    private String plantName;
    private int plantID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_reviews, container, false);
        context = view.getContext();

        progressBar_1 = view.findViewById(R.id.progressBar_1);
        progressBar_2 = view.findViewById(R.id.progressBar_2);
        progressBar_3 = view.findViewById(R.id.progressBar_3);
        progressBar_4 = view.findViewById(R.id.progressBar_4);
        progressBar_5 = view.findViewById(R.id.progressBar_5);
        textView_overAll = view.findViewById(R.id.textView_overAll);
        ratingBar_overAll = view.findViewById(R.id.ratingBar_overAll);
        textView_numReviews = view.findViewById(R.id.textView_numReviews);
        recyclerView_reviews = view.findViewById(R.id.recyclerView_reviews);
        button_write = view.findViewById(R.id.button_write);

        reviewList = new ArrayList<>();

        activity = (PlantActivity)getActivity();
        try {
            assert activity != null;
            JSONArray plant = new JSONArray(activity.plant);
            // Log.d("reviewFrag", plant.toString());
            JSONObject plantObj = plant.getJSONObject(0);
            plantName = plantObj.getString("name");
            plantID = plantObj.getInt("_id");
            String rating =  String.valueOf(plantObj.getDouble("rating"));
            if(rating.length() > 3){
                rating = rating.substring(0, 4);
            }
            textView_overAll.setText(rating);
            ratingBar_overAll.setRating((float)plantObj.getDouble("rating"));
            JSONArray reviews = plantObj.getJSONArray("reviews");
            textView_numReviews.setText(reviews.length() + " Reviews");
            int five = 0;
            int four = 0;
            int three = 0;
            int two = 0;
            int one = 0;
            for(int i = 0; i < reviews.length(); i++){
                JSONObject reviewObject = reviews.getJSONObject(i);
                if(reviewObject.getDouble("rating") == 5){
                    five++;
                }
                else if(reviewObject.getDouble("rating") >= 4){
                    four++;
                }
                else if(reviewObject.getDouble("rating") >= 3){
                    three++;
                }
                else if(reviewObject.getDouble("rating") >= 2){
                    two++;
                }
                else{
                    one++;
                }
                JSONArray images = reviewObject.getJSONArray("images");
                ArrayList<String> imageTemp = new ArrayList<>();
                for(int j = 0; j < images.length(); j++){
                    imageTemp.add(images.getString(j));
                }
                Review review = new Review(
                        reviewObject.getString("name"),
                        reviewObject.getString("text"),
                        reviewObject.getDouble("rating"),
                        imageTemp
                );
                reviewList.add(review);
            }
            progressBar_1.setProgress(one*5);
            progressBar_2.setProgress(two*5);
            progressBar_3.setProgress(three*5);
            progressBar_4.setProgress(four*5);
            progressBar_5.setProgress(five*5);

            ReviewAdapter adapter = new ReviewAdapter(reviewList);
            recyclerView_reviews.setAdapter(adapter);
            LinearLayoutManager layoutManager =  new LinearLayoutManager(context);
            recyclerView_reviews.setLayoutManager(layoutManager);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        button_write.setOnClickListener(v -> writeReview());

        return view;
    }

    public void writeReview(){
        Intent intent = new Intent(context, ReviewActivity.class);
        intent.putExtra("plantName", plantName);
        intent.putExtra("plantID", plantID);
        startActivity(intent);
    }
}
