package com.example.afinal;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> reviews;
    private Context context;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    public ReviewAdapter(List<Review> reviews){
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View plantView = inflater.inflate(R.layout.item_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(plantView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.textView_revName.setText(review.getName());
        holder.textView_revText.setText(review.getText());
        holder.ratingBar_review.setRating((float) review.getRating());
        ArrayList<String> images = review.getImages();
        if(images.size() != 0) {
            holder.imageView_review.setVisibility(View.VISIBLE);
            storageReference.child(images.get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).rotate(90).into(holder.imageView_review);
                }
            });

            // Picasso.get().load(new File(images.get(0))).into(holder.imageView_review);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView_revName;
        TextView textView_revText;
        ImageView imageView_review;
        RatingBar ratingBar_review;

        public ViewHolder(View itemView){
            super(itemView);
            textView_revName = itemView.findViewById(R.id.textView_revName);
            textView_revText = itemView.findViewById(R.id.textView_revText);
            imageView_review = itemView.findViewById(R.id.imageView_review);
            ratingBar_review = itemView.findViewById(R.id.ratingBar_review);
        }
    }
}
