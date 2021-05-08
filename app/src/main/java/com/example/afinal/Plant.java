package com.example.afinal;

public class Plant {

    private int _id;
    private String name;
    private String description;
    private double rating;
    private String image_url;
    private boolean saved;

    public Plant(int _id, String name, String description, double rating, String image_url, boolean saved){
        this._id = _id;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.image_url = image_url;
        this.saved = saved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
