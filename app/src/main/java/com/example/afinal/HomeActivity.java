package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {

    private Button button_userProfile;
    private Button button_discover;

    protected String _id;
    protected String name;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        _id = intent.getStringExtra("_id");
        name = intent.getStringExtra("name");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("_id",_id); // key:value
        editor.putString("name", name);
        editor.apply();


        button_discover = findViewById(R.id.button_discover);
        button_discover.setOnClickListener(v -> {
            loadFragment(new DiscoverFragment(), R.id.fragContainer_main);
        });
        button_discover.callOnClick();

        button_userProfile = findViewById(R.id.button_userProfile);
        button_userProfile.setOnClickListener(v -> {
            loadFragment(new UserFragment(), R.id.fragContainer_main);
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
}
