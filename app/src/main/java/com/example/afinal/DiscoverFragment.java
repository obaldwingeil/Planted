package com.example.afinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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

public class DiscoverFragment extends Fragment {

    View view;
    private Context context;

    private EditText editText_search;
    private Button button_filter;
    private Button button_sort;
    private RecyclerView recyclerView_search;
    private TextView textView_filters;
    private Button button_apply;
    private Button button_search;

    private String api_root;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static AsyncHttpClient client2 = new AsyncHttpClient();
    private static AsyncHttpClient client3 = new AsyncHttpClient();

    private ArrayList<Plant> plantList;
    private ArrayList<Plant> filteredPlantList;
    private ArrayList<String> filterList;

    private String sortBy;
    private String search;

    private SharedPreferences sharedPreferences;
    private String _id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_discover, container, false);
        context = view.getContext();

        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        _id = sharedPreferences.getString("_id", null);

        editText_search = view.findViewById(R.id.editText_search);
        button_filter = view.findViewById(R.id.button_filter);
        button_sort = view.findViewById(R.id.button_sort);
        recyclerView_search = view.findViewById(R.id.recyclerView_search);
        textView_filters = view.findViewById(R.id.textView_filter);
        button_apply = view.findViewById(R.id.button_applyFilters);
        button_search = view.findViewById(R.id.button_search);

        plantList = new ArrayList<>();
        filterList = new ArrayList<>();
        filteredPlantList = new ArrayList<>();
        api_root = getString(R.string.api_root);
        sortBy = "Alphabetical";
        search = "";

        defaultFillPlants();

        button_filter.setOnClickListener(v -> openFilterMenu());

        button_apply.setOnClickListener(v -> applyFilters());

        button_sort.setOnClickListener(v -> openSortMenu());

        button_search.setOnClickListener(v -> search());

        return view;
    }

    public void defaultFillPlants(){
        String api = api_root + "/plants";
        Log.d("api", api_root);
        client.get(api, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("api", new String(responseBody));
                try {
                    JSONArray response = new JSONArray(new String(responseBody));
                    for(int i = 0; i < response.length(); i++){
                        JSONObject plantObject = response.getJSONObject(i);
                        boolean saved = false;
                        JSONArray users = plantObject.getJSONArray("users");
                        for(int j = 0; j < users.length(); j++){
                            if(users.getString(j).equals( _id)){
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
                        PlantAdapter adapter = new PlantAdapter(plantList);
                        recyclerView_search.setAdapter(adapter);
                        recyclerView_search.setLayoutManager(new LinearLayoutManager(context));

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
    }

    public void openFilterMenu(){
        PopupMenu filterMenu = new PopupMenu(context, button_filter);
        filterMenu.getMenuInflater().inflate(R.menu.menu_filter, filterMenu.getMenu());
        Menu difficulty = filterMenu.getMenu().getItem(0).getSubMenu();
        for(int i = 0; i < 3; i++){
            MenuItem item = difficulty.getItem(i);
            item.setChecked(filterList.contains(item.getTitle().toString()));
        }
        Menu light = filterMenu.getMenu().getItem(1).getSubMenu();
        for(int i = 0; i < 3; i++){
            MenuItem item = light.getItem(i);
            item.setChecked(filterList.contains(item.getTitle().toString()));
        }
        Menu temp = filterMenu.getMenu().getItem(2).getSubMenu();
        for(int i = 0; i < 3; i++){
            MenuItem item = temp.getItem(i);
            item.setChecked(filterList.contains(item.getTitle().toString()));
        }
        filterMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Toast message on menu item clicked

                if(menuItem.isCheckable()){
                    menuItem.setChecked(!menuItem.isChecked());
                    if(menuItem.isChecked()){
                        if(!filterList.contains(menuItem.getTitle().toString())){
                            filterList.add(menuItem.getTitle().toString());
                        }
                    }
                    else{
                        filterList.remove(menuItem.getTitle().toString());
                    }
                    textView_filters.setText("Filters: " + filterList.toString());

                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                    menuItem.setActionView(new View(context));
                    menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return false;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            return false;
                        }
                    });
                }
                return false;
            }
            });
        filterMenu.show();
    }

    public void applyFilters(){
            filteredPlantList = new ArrayList<>();
            String api = api_root + "/plants/filter";
            JSONObject body = new JSONObject();
            try {
                body.put("filters", filterList);
                body.put("sortBy", sortBy);
                body.put("search", search);
                StringEntity entity = new StringEntity(body.toString());
                client2.get(context, api, entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("filtered", new String(responseBody));
                        try {
                            JSONArray response = new JSONArray(new String(responseBody));
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject plantObject = response.getJSONObject(i);
                                boolean saved = false;
                                JSONArray users = plantObject.getJSONArray("users");
                                for(int j = 0; j < users.length(); j++){
                                    if(users.getString(j).equals( _id)){
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
                                filteredPlantList.add(plant);
                            }
                            PlantAdapter adapter = new PlantAdapter(filteredPlantList);
                            recyclerView_search.setAdapter(adapter);
                            recyclerView_search.setLayoutManager(new LinearLayoutManager(context));

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

    public void openSortMenu(){
        PopupMenu popupMenu = new PopupMenu(context, button_sort);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sortBy = menuItem.getTitle().toString();
                applyFilters();
                return true;
            }
        });
        // Showing the popup menu
        popupMenu.show();
    }

    public void search(){
        search = editText_search.getText().toString();
        applyFilters();
    }
}
