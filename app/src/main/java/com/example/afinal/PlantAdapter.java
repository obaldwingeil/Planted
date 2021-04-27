package com.example.afinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> {

    private List<Plant> plants;
    private Context context;

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
        holder.textView_name.setText(plant.getName());
        holder.textView_description.setText(plant.getDescription());
        holder.ratingBar_plant.setRating(plant.getRating());
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

        public ViewHolder(View itemView){
            super(itemView);
            textView_name = itemView.findViewById(R.id.textView_plantName);
            textView_description = itemView.findViewById(R.id.textView_plantDesc);
            imageView_plant = itemView.findViewById(R.id.imageView_plant);
            imageView_saved = itemView.findViewById(R.id.imageView_saved);
            ratingBar_plant = itemView.findViewById(R.id.ratingBar_plant);
        }
    }
}
