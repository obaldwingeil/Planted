package com.example.afinal;

import android.util.Log;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Dao;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PlantDAO {

    private String api_root = "http://192.168.1.70:5000/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public PlantDAO(){

    }

    public void getAllPlantsHelper(){

    }

    public ArrayList<Plant> getAllPlants() {
        ArrayList<Plant> plantList = new ArrayList<>();

        String api = api_root + "/plants";
        client.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("api", new String(responseBody));
                try {
                    JSONArray response = new JSONArray(new String(responseBody));
                    for (int i = 0; i < 5; i++) {
                        JSONObject plantObject = response.getJSONObject(i);
                        JSONArray reviews = plantObject.getJSONArray("reviews");
                        int total = 0;
                        for (int j = 0; j < reviews.length(); j++) {
                            total += reviews.getJSONObject(j).getInt("rating");
                        }
                        int rating = total / reviews.length();
                        Plant plant = new Plant(
                                plantObject.getInt("_id"),
                                plantObject.getString("name"),
                                plantObject.getString("description"),
                                rating,
                                plantObject.getJSONArray("images").getString(0),
                                false
                        );
                        plantList.add(plant);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("api", new String(responseBody));
            }
        });
        // Log.d("DAO_plantList", String.valueOf(plantList));
        return plantList;
    }

    /*
    public Plant getPlantByID(String _id){

    }

    public boolean addUserPlant(String _id){
        return false;
    }

    public User getUserByID(String _id){

    }

    public boolean addUser(){

    }*/
}
