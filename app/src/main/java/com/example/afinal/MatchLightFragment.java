package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MatchLightFragment extends Fragment {

    View view;
    private Context context;

    CheckLightActivity activity;
    String light;

    TextView textView_light;
    Button button_add;
    Button button_buy;

    private SharedPreferences sharedPreferences;
    private String api_root;
    private static AsyncHttpClient client = new AsyncHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_match_light, container, false);
        context = view.getContext();

        textView_light = view.findViewById(R.id.textView_wrongLightCheck);
        button_add = view.findViewById(R.id.button_addPlant);
        button_buy = view.findViewById(R.id.button_buyPlant);

        activity = (CheckLightActivity)getActivity();
        light = activity.light;
        api_root = getString(R.string.api_root);

        textView_light.setText(light);

        button_buy.setOnClickListener(v -> openWebsite());
        button_add.setOnClickListener(v -> addPlant());

        return view;
    }

    public void openWebsite(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.buyURL));
        // check to make sure that there is an app or activity that can handle this action
        Log.d("activity", intent.resolveActivity(context.getPackageManager()).toString());
        // tells you which apps/activities can handle this intent
        if (intent.resolveActivity(context.getPackageManager()) != null){
            // send the intent
            startActivity(intent);
        }
        else{
            // if not, log the error
            Log.e("intent", "cannot handle intent");
        }
    }

    public void addPlant(){
        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String _id = sharedPreferences.getString("_id", "id");
        Log.d("shared id", _id);

        String api = api_root + "user/add/" + _id;
        JSONObject body = new JSONObject();
        try {
            body.put("plant", activity.plantName);
            StringEntity entity = new StringEntity(body.toString());
            client.post(context, api, entity, "application/json", new AsyncHttpResponseHandler() {
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
