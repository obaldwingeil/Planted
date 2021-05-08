package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> {

    private List<Plant> plants;
    private Context context;
    private SharedPreferences sharedPreferences;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public PlantAdapter(List<Plant> plants){
        this.plants =  plants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View plantView = inflater.inflate(R.layout.item_plant, parent, false);
        ViewHolder viewHolder = new ViewHolder(plantView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plant plant = plants.get(position);
        String plantName = plant.getName();
        holder.textView_name.setText(plant.getName());
        holder.textView_description.setText(plant.getDescription());
        holder.ratingBar_plant.setRating((float) plant.getRating());
        Picasso.get().load("https://drive.google.com/uc?export=view&id=" + plant.getImage_url()).into(holder.imageView_plant);
        if(plant.isSaved()){
            Picasso.get().load("file:///android_asset/saved.png").into(holder.imageView_saved);
        }
        else{
            Picasso.get().load("file:///android_asset/not_saved.png").into(holder.imageView_saved);
        }
        holder.imageView_saved.setOnClickListener(v -> {
            if(plant.isSaved()){
                plant.setSaved(false);
            }
            else{
                plant.setSaved(true);
            }
            this.notifyItemChanged(position);
            addPlant(plantName);
        });
        holder.linearLayout_plantItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlantActivity.class);
            intent.putExtra("_id", plant.get_id());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView_name;
        TextView textView_description;
        ImageView imageView_plant;
        ImageView imageView_saved;
        RatingBar ratingBar_plant;
        LinearLayout linearLayout_plantItem;

        public ViewHolder(View itemView){
            super(itemView);
            textView_name = itemView.findViewById(R.id.textView_revName);
            textView_description = itemView.findViewById(R.id.textView_revText);
            imageView_plant = itemView.findViewById(R.id.imageView_review);
            imageView_saved = itemView.findViewById(R.id.imageView_saved);
            ratingBar_plant = itemView.findViewById(R.id.ratingBar_review);
            linearLayout_plantItem = itemView.findViewById(R.id.linearLayout_plantItem);
        }
    }

    public void addPlant(String plantName){
        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("_id", null) != null){
            String _id = sharedPreferences.getString("_id", "id");
            Log.d("shared id", _id);

            String api_root = context.getString(R.string.api_root);
            String api = api_root + "user/add/" + _id;
            JSONObject body = new JSONObject();
            try {
                body.put("plant", plantName);
                StringEntity entity = new StringEntity(body.toString());
                client.post(context, api, entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("plant_added", new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
