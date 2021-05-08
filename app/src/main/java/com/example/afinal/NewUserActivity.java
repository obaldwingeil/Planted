package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewUserActivity extends AppCompatActivity {

    private EditText editText_name;
    private EditText editText_email;
    private EditText editText_password;
    private EditText editText_confirm;
    private Button button_create;

    private ArrayList<Plant> plantList;
    private RecyclerView recyclerView;

    private String api_root;
    private static AsyncHttpClient client = new AsyncHttpClient();

    private Context context;

    private PlantDAO plantDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);
        editText_name = findViewById(R.id.editText_name);
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        editText_confirm = findViewById(R.id.editText_confirm);

        api_root = getString(R.string.api_root);

        plantDAO = new PlantDAO();

        recyclerView = findViewById(R.id.recyclerView_quickAdd);
        plantList = new ArrayList<>();
        context = this;

        getPlants();


        button_create = findViewById(R.id.button_create);
        button_create.setOnClickListener(v -> checkAccount(v));

    }

    public void getPlants(){
        /*
        plantList = plantDAO.getAllPlants();
        Log.d("plantList", String.valueOf(plantList));
        PlantAdapter adapter = new PlantAdapter(plantList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager =  new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);*/

        // recyclerView.addItemDecoration(dividerItemDecoration);
        String api = api_root + "/plants";
        client.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("api", new String(responseBody));
                try {
                    JSONArray response = new JSONArray(new String(responseBody));
                    for(int i = 0; i < 5; i++){
                        JSONObject plantObject = response.getJSONObject(i);
                        Plant plant = new Plant(
                                plantObject.getInt("_id"),
                                plantObject.getString("name"),
                                plantObject.getString("description"),
                                plantObject.getDouble("rating"),
                                plantObject.getJSONArray("images").getString(0),
                                false
                        );
                        plantList.add(plant);
                    }
                    PlantAdapter adapter = new PlantAdapter(plantList);
                    recyclerView.setAdapter(adapter);
                    LinearLayoutManager layoutManager =  new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(layoutManager);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
                    // recyclerView.addItemDecoration(dividerItemDecoration);

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

    public void checkAccount(View view){
        if(TextUtils.isEmpty(editText_email.getText()) || TextUtils.isEmpty(editText_password.getText())
                || TextUtils.isEmpty(editText_name.getText()) || TextUtils.isEmpty(editText_confirm.getText())){
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
        }
        else if(!editText_confirm.getText().toString().equals(editText_password.getText().toString())){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(editText_email.getText()).matches()){
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        }
        else {
            String api = api_root + "result/user/" + editText_email.getText();
            client.get(api, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("userInfo", new String(responseBody));
                    if (responseBody.length == 2) {
                        create(view);
                    } else {
                        Toast.makeText(context, "Email linked to an existing account", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }
    }

    public void create(View view) {

            JSONObject body = new JSONObject();
            try {
                ArrayList<String> myPlants = new ArrayList<>();
                for(Plant plant: plantList){
                    if(plant.isSaved()){
                        myPlants.add(plant.getName());
                    }
                }
                // Log.d("myPlants", myPlants.toString());
                body.put("_id", editText_email.getText().toString());
                body.put("password", editText_password.getText().toString());
                body.put("name", editText_name.getText().toString());
                body.put("myPlants", myPlants);

                StringEntity entity = new StringEntity(body.toString());
                String user_api = api_root + "/user/add";
                client.post(this, user_api, entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("post", new String(responseBody));
                        Intent intent = new Intent(NewUserActivity.this, HomeActivity.class);
                        intent.putExtra("_id", editText_email.getText().toString());
                        intent.putExtra("name", editText_name.getText().toString());
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("post", new String(responseBody));
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
    }
}
