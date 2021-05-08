package com.example.afinal;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Review {

    private String name;
    private String text;
    private double rating;
    private ArrayList<String> images = new ArrayList<>();

    public Review(String name, String text, double rating, ArrayList<String> images){
        this.name = name;
        this.text = text;
        this.rating = rating;
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
