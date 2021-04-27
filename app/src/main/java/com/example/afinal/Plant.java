package com.example.afinal;

public class Plant {

    private String name;
    private String description;
    private int rating;
    private String image_url;
    private boolean saved;

    public Plant(String name, String description, int rating, String image_url, boolean saved){
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
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
}
