package com.example.afinal;

import java.util.List;

public class User {
    private String _id;
    private String password;
    private String name;
    private List<Plant> myPlants;
    private List<Review> myReviews;

    public User(String _id, String password, String name,
                List<Plant> myPlants, List<Review> myReviews){
        this._id = _id;
        this.password = password;
        this.name = name;
        this.myPlants = myPlants;
        this.myReviews = myReviews;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Plant> getMyPlants() {
        return myPlants;
    }

    public void setMyPlants(List<Plant> myPlants) {
        this.myPlants = myPlants;
    }

    public List<Review> getMyReviews() {
        return myReviews;
    }

    public void setMyReviews(List<Review> myReviews) {
        this.myReviews = myReviews;
    }
}
