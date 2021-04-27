package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private EditText editText_email;
    private EditText editText_password;
    private Button button_login;
    private Button button_newUser;
    private ImageView imageView;
    private Context context;

    private String api_root = "http://192.168.1.70:5000/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        editText_email = findViewById(R.id.editText_loginEmail);
        editText_password = findViewById(R.id.editText_loginPassword);
        button_login = findViewById(R.id.button_login);
        button_newUser = findViewById(R.id.button_new);
        imageView = findViewById(R.id.imageView_logo);
        Picasso.get().load("file:///android_asset/logo_w_title.png").into(imageView);


        button_login.setOnClickListener(v -> login(v));

        button_newUser.setOnClickListener(v -> newUser());

    }

    public void login(View view){
        if(TextUtils.isEmpty(editText_email.getText()) || TextUtils.isEmpty(editText_password.getText())){
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
        }
        else{
            String api = api_root + "result/user/" + editText_email.getText();
            client.get(api, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("userInfo", new String(responseBody));
                    Log.d("responseBody", String.valueOf(responseBody.length));
                    if (responseBody.length == 2) {
                        Log.d("check", "length = 2");
                        Toast.makeText(context, "Incorrect Username", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONArray result = new JSONArray(new String(responseBody));
                            JSONObject user = result.getJSONObject(0);
                            String password = user.getString("password");

                            if (editText_password.getText().toString().equals(password)) {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.putExtra("userInfo", new String(responseBody));
                                startActivity(intent);
                            } else {
                                Toast.makeText(context, "Incorrect Password", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    // Toast.makeText(context, "Incorrect email", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void newUser(){
        Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
        startActivity(intent);
    }
}