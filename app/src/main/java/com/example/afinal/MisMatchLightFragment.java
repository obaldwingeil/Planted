package com.example.afinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MisMatchLightFragment extends Fragment {

    View view;
    Context context;

    TextView textView_lightCheck;
    TextView textView_wrongLight;
    RecyclerView recyclerView_light;

    CheckLightActivity activity;

    ArrayList<Plant> plantList;

    private String api_root;
    private static AsyncHttpClient client = new AsyncHttpClient();

    private SharedPreferences sharedPreferences;
    private String _id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mismatch_light, container, false);
        context = view.getContext();

        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        _id = sharedPreferences.getString("_id", null);

        textView_lightCheck = view.findViewById(R.id.textView_wrongLightCheck);
        textView_wrongLight = view.findViewById(R.id.textView_wrongLight);
        recyclerView_light = view.findViewById(R.id.recyclerView_light);
        api_root = getString(R.string.api_root);
        plantList = new ArrayList<>();

        activity = (CheckLightActivity)getActivity();
        textView_lightCheck.setText(activity.checkLight);
        String suggest = "";
        if(getValue(activity.light) < getValue(activity.checkLight)){
            suggest = "That amount of light is too strong for this plant. Instead try a " + activity.checkLight + " plant:";
        }
        else{
            suggest = "That amount of light is too low for this plant. Instead try a " + activity.checkLight + " plant:";
        }
        textView_wrongLight.setText(suggest);

        getPlantSuggestions();

        return view;
    }

    public int getValue(String light){
        if(light.equals("low light")){
            return 0;
        }
        else if(light.equals("bright indirect light")){
            return 1;
        }
        else{
            return 2;
        }
    }

    public void getPlantSuggestions(){
        String api = api_root + "result/plants/light";
        JSONObject body = new JSONObject();
        try {
            body.put("light", activity.checkLight);
            StringEntity entity = new StringEntity(body.toString());
            client.get(context, api, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("light_plants", new String(responseBody));
                    try {
                        JSONArray response = new JSONArray(new String(responseBody));
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject plantObject = response.getJSONObject(i);
                            boolean saved = false;
                            JSONArray users = plantObject.getJSONArray("users");
                            for(int j = 0; j < users.length(); j++){
                                if(users.getString(j).equals(_id)){
                                    saved = true;
                                }
                            }
                            Plant plant = new Plant(
                                    plantObject.getInt("_id"),
                                    plantObject.getString("name"),
                                    plantObject.getString("description"),
                                    plantObject.getDouble("rating"),
                                    plantObject.getJSONArray("images").getString(0),
                                    saved
                            );
                            plantList.add(plant);
                        }
                        PlantAdapter adapter = new PlantAdapter(plantList);
                        recyclerView_light.setAdapter(adapter);
                        recyclerView_light.setLayoutManager(new LinearLayoutManager(context));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
