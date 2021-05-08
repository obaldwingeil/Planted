package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.flexbox.FlexboxLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PlantActivity extends AppCompatActivity {

    private TextView textView_name;
    private ImageView imageView_plantSaved;
    private TextView textView_level;
    private RatingBar ratingBar_plant;
    private ImageView imageView_plant1;
    private ImageView imageView_plant2;
    private ImageView imageView_plant3;
    private Button button_buy;
    private TextView textView_description;
    private LinearLayout linearLayout_colors;
    private TextView textView_light;
    private Button button_checkLight;
    private TextView textView_temp;
    private FlexboxLayout flexboxLayout_tags;
    private Button button_reviews;
    private Button button_photos;

    private int plantID;
    private String buyURL;
    private String light;
    private String plantName;

    private String api_root;
    private static AsyncHttpClient client = new AsyncHttpClient();

    protected String plant;
    private ArrayList<String> colorList;
    private ArrayList<String> tagList;

    private SharedPreferences sharedPreferences;
    private String _id;
    private boolean saved;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        textView_name = findViewById(R.id.textView_plantName);
        imageView_plantSaved = findViewById(R.id.imageView_plantSaved);
        textView_level = findViewById(R.id.textView_level);
        ratingBar_plant = findViewById(R.id.ratingBar_plant);
        imageView_plant1 = findViewById(R.id.imageView_plant1);
        imageView_plant2 = findViewById(R.id.imageView_plant2);
        imageView_plant3 = findViewById(R.id.imageView_plant3);
        button_buy = findViewById(R.id.button_buy);
        textView_description = findViewById(R.id.textView_plantDesc);
        linearLayout_colors = findViewById(R.id.layout_colors);
        textView_light  = findViewById(R.id.textView_lightDesc);
        textView_temp = findViewById(R.id.textView_tempDesc);
        button_checkLight = findViewById(R.id.button_checkLight);
        flexboxLayout_tags = findViewById(R.id.flexBox_plant);
        button_reviews = findViewById(R.id.button_reviews);
        button_photos = findViewById(R.id.button_photos);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        _id = sharedPreferences.getString("_id", null);
        saved = false;

        api_root = getString(R.string.api_root);

        Intent intent = getIntent();
        plantID = intent.getIntExtra("_id", 0);
        getPlant();

        colorList = new ArrayList<>();
        tagList  = new ArrayList<>();

        button_reviews.setOnClickListener(v -> {
            loadFragment(new ReviewsFragment(), R.id.fragContainer_plant);
        });


        button_photos.setOnClickListener(v -> {
            loadFragment(new PhotosFragment(), R.id.fragContainer_plant);
        });

        button_buy.setOnClickListener(v -> openWebsite());
        button_checkLight.setOnClickListener(v -> checkLight());

        imageView_plantSaved.setOnClickListener(v -> addPlant(plantName));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPlant();
    }

    public void getPlant(){
        String api = api_root + "result/plant/" + plantID;
        client.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("api", new String(responseBody));
                plant = new String(responseBody);
                button_reviews.callOnClick();
                try {
                    JSONArray response = new JSONArray(new String(responseBody));
                    JSONObject plant = response.getJSONObject(0);
                    plantName = plant.getString("name");
                    JSONArray users = plant.getJSONArray("users");
                    for(int j = 0; j < users.length(); j++){
                        if(users.getString(j).equals(_id)){
                            saved = true;
                        }
                    }
                    if(saved){
                        Picasso.get().load("file:///android_asset/saved.png").into(imageView_plantSaved);
                    }
                    else{
                        Picasso.get().load("file:///android_asset/not_saved.png").into(imageView_plantSaved);
                    }
                    textView_name.setText(plant.getString("name"));
                    textView_level.setText(plant.getString("level"));
                    ratingBar_plant.setRating((float)plant.getDouble("rating"));
                    JSONArray images = plant.getJSONArray("images");
                    Picasso.get().load("https://drive.google.com/uc?export=view&id=" +
                            images.getString(0)).into(imageView_plant1);
                    Picasso.get().load("https://drive.google.com/uc?export=view&id=" +
                            images.getString(1)).into(imageView_plant2);
                    Picasso.get().load("https://drive.google.com/uc?export=view&id=" +
                            images.getString(2)).into(imageView_plant3);
                    buyURL = plant.getString("link");
                    textView_description.setText(plant.getString("description"));

                    JSONArray colors = plant.getJSONArray("colors");
                    if(colorList.size() == 0){
                        for(int i = 0; i < colors.length(); i++){
                            TextView color = new TextView(PlantActivity.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            lp.setMargins(8,8,8,8);
                            String name = colors.getString(i);
                            colorList.add(name);
                            color.setBackground(getDrawable(getResources().getIdentifier(name, "drawable", getPackageName())));
                            color.setLayoutParams(lp);
                            linearLayout_colors.addView(color);
                        }
                    }
                    textView_light.setText(plant.getString("light"));
                    light = plant.getString("light");
                    String temp = plant.getInt("tempMin") + "-" + plant.getInt("tempMax") + "°F";
                    textView_temp.setText(temp);


                    if(tagList.size()  == 0){
                        TextView label = new TextView(PlantActivity.this);
                        label.setText(getString(R.string.tags));
                        label.setTextSize(24);
                        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(8,8,8,8);
                        label.setLayoutParams(lp);
                        flexboxLayout_tags.addView(label);
                        JSONArray tags = plant.getJSONArray("tags");
                        for(int i = 0; i < tags.length(); i++){
                            tagList.add(tags.getString(i));
                            TextView tag = new TextView(PlantActivity.this);
                            tag.setLayoutParams(lp);
                            tag.setPadding(50, 4, 50, 4);
                            tag.setBackgroundColor(getColor(R.color.medium_green));
                            tag.setText(tags.getString(i));
                            tag.setTextColor(getColor(R.color.white));
                            tag.setGravity(Gravity.CENTER);
                            flexboxLayout_tags.addView(tag);
                        }
                    }
                    JSONArray reviews = plant.getJSONArray("reviews");
                    button_reviews.setText(getString(R.string.reviews) + " (" + reviews.length() + ')');
                    button_photos.setText(getString(R.string.photos) + " (" + plant.getJSONArray("photos").length() + ')');
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("api", new String(responseBody));
            }
        });
    }

    public void loadFragment(Fragment fragment, int id){
        FragmentManager fragmentManager = getSupportFragmentManager();
        // create a fragment transaction to begin the transaction and replace the fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //replacing the placeholder - fragmentContainterView with the fragment that is passed as parameter
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    public void openWebsite(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(buyURL));
        // check to make sure that there is an app or activity that can handle this action
        Log.d("activity", intent.resolveActivity(getPackageManager()).toString());
        // tells you which apps/activities can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null){
            // send the intent
            startActivity(intent);
        }
        else{
            // if not, log the error
            Log.e("intent", "cannot handle intent");
        }
    }

    public void checkLight(){
        Intent intent = new Intent(this, CheckLightActivity.class);
        intent.putExtra("light", light);
        intent.putExtra("buyURL", buyURL);
        intent.putExtra("plantName", plantName);
        startActivity(intent);
    }

    public void addPlant(String plantName){
        if(_id != null){
            saved = !saved;
            if(saved){
                Picasso.get().load("file:///android_asset/saved.png").into(imageView_plantSaved);
            }
            else{
                Picasso.get().load("file:///android_asset/not_saved.png").into(imageView_plantSaved);
            }
            String api_root = getString(R.string.api_root);
            String api = api_root + "user/add/" + _id;
            JSONObject body = new JSONObject();
            try {
                body.put("plant", plantName);
                StringEntity entity = new StringEntity(body.toString());
                client.post(this, api, entity, "application/json", new AsyncHttpResponseHandler() {
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
