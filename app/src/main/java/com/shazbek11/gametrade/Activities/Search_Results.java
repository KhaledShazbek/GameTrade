package com.shazbek11.gametrade.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.shazbek11.gametrade.Adapters.RecyclerViewAdapter;
import com.shazbek11.gametrade.Adapters.SearchProfileAdapter;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SearchProfile;
import com.shazbek11.gametrade.utils.VideoGamePost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;


public class Search_Results extends AppCompatActivity {
    String address = "http://shazbekgametrade.000webhostapp.com/Search.php?limit=6&search=";
    String profilesAddress = "http://shazbekgametrade.000webhostapp.com/SearchProfiles.php?limit=6&search=";
    private String lastId = "0";
    private boolean itShouldLoadMore = true;

    SwipeRefreshLayout refreshLayout;

    private RecyclerViewAdapter recyclerAdapter;
    private ArrayList<VideoGamePost> recyclerModels;

    private ArrayList<SearchProfile> profilesearch;
    private SearchProfileAdapter searchProfileAdapter;

    private EditText searchText;
    private String searchItem;
    String filter;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__results);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        searchItem = intent.getStringExtra("searchitem");
        FetchResults(address, searchItem);

        ImageButton backButton = findViewById(R.id.searchresults_backicon);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profilesearch = new ArrayList<>();
        recyclerModels = new ArrayList<>();
        recyclerAdapter = new RecyclerViewAdapter(recyclerModels,Search_Results.this);
        searchProfileAdapter = new SearchProfileAdapter(profilesearch, Search_Results.this);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.searchresult_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(Search_Results.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        refreshLayout = findViewById(R.id.search_swiprerefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (filter.equals("Games & Prices")){
                    recyclerAdapter.clear();
                    FetchResults(address, searchItem);
                }else if(filter.equals("Profiles & Locations")){
                    searchProfileAdapter.clear();
                    SearchForProfiles(profilesAddress, searchItem);
                }
            }
        });

        final Spinner spinner = findViewById(R.id.Spinner);
        ArrayList<String> content = new ArrayList<String>();
        content.add("Games & Prices");
        content.add("Profiles & Locations");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item
                ,content){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // this part is needed for hiding the original view
                View view = super.getView(position, convertView, parent);
                view.setVisibility(View.GONE);

                return view;
            }
        };
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter = parent.getSelectedItem().toString().trim();
                switch (filter){
                    case "Profiles & Locations":
                        searchItem = searchText.getText().toString().trim();
                        recyclerAdapter.clear();
                        recyclerView.setAdapter(searchProfileAdapter);
                        SearchForProfiles(profilesAddress,searchItem);
                        break;
                    case "Games & Prices":
                        searchItem = searchText.getText().toString().trim();
                        searchProfileAdapter.clear();
                        recyclerView.setAdapter(recyclerAdapter);
                        FetchResults(address, searchItem);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchText = findViewById(R.id.searchtext);
        searchText.setText(searchItem);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    searchItem = searchText.getText().toString().trim();
                    recyclerAdapter.clear();
                    searchProfileAdapter.clear();
                    if (filter.equals("Games & Prices")){
                        FetchResults(address, searchItem);
                    }else if(filter.equals("Profiles & Locations")){
                        SearchForProfiles(profilesAddress, searchItem);
                    }
                }
                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            if (recyclerView.getAdapter() == searchProfileAdapter){
                                SearchForMoreProfiles(profilesAddress, searchItem);
                            }else{
                                loadMore(address, searchItem);
                            }
                        }
                    }
                }
            }
        });


    }

    private void FetchResults(String link, final String search) {

        String url;
        try {
            String searchableItem = URLEncoder.encode(search, "UTF-8");
            url = link + searchableItem;
            itShouldLoadMore = false;
            final ProgressBar loading = findViewById(R.id.searchresult_progressbar);
            loading.setVisibility(View.VISIBLE);


            final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    refreshLayout.setRefreshing(false);
                    loading.setVisibility(View.GONE);
                    itShouldLoadMore = true;

                    if (response.length() <= 0) {
                        // no data available
                        Toast.makeText(Search_Results.this, "No data available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < response.length(); i++) {
                        try {

                            JSONObject jsonObject = response.getJSONObject(i);

                            lastId = jsonObject.getString("id");
                            String username = jsonObject.getString("KEY_USERNAME");
                            String location = jsonObject.getString("KEY_LOCATION");
                            String gamename = jsonObject.getString("KEY_GAMENAME");
                            String price = jsonObject.getString("KEY_PRICE");
                            String userpic = jsonObject.getString("KEY_USERPIC");
                            String gamepic = jsonObject.getString("KEY_GAMEPIC");

                            recyclerModels.add(new VideoGamePost(username, location, gamename, price, userpic, gamepic));
                            recyclerAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    itShouldLoadMore = true;
                    loading.setVisibility(View.GONE);
                    Toast.makeText(Search_Results.this, "network error!", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(Search_Results.this)
                            .setMessage(error.toString())
                            .show();
                }
            });
            Volley.newRequestQueue(Search_Results.this).add(jsonArrayRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void loadMore(String address, String searchableItem){

        itShouldLoadMore = false;

        String url;
        try {
            url = address + URLEncoder.encode(searchableItem, "UTF-8") + "&action=loadmore&lastId="+lastId;
            final ProgressBar progressWheel = (ProgressBar) this.findViewById(R.id.searchresult_progressbar2);
            progressWheel.setVisibility(View.VISIBLE);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressWheel.setVisibility(View.GONE);

                    itShouldLoadMore = true;

                    if (response.length() <= 0) {
                        Toast.makeText(Search_Results.this, "no data available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            lastId = jsonObject.getString("id");
                            String username = jsonObject.getString("KEY_USERNAME");
                            String location = jsonObject.getString("KEY_LOCATION");
                            String gamename = jsonObject.getString("KEY_GAMENAME");
                            String price = jsonObject.getString("KEY_PRICE");
                            String userpic = jsonObject.getString("KEY_USERPIC");
                            String gamepic = jsonObject.getString("KEY_GAMEPIC");

                            recyclerModels.add(new VideoGamePost(username, location, gamename, price, userpic, gamepic));
                            recyclerAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressWheel.setVisibility(View.GONE);
                    itShouldLoadMore = true;
                    Toast.makeText(Search_Results.this, "Failed to load more, network error", Toast.LENGTH_SHORT).show();
                }
            });

            Volley.newRequestQueue(Search_Results.this).add(jsonArrayRequest);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void SearchForProfiles(String link, final String search){

        String url;
        try {
            String searchableItem = URLEncoder.encode(search, "UTF-8");
            url = link + searchableItem;
            itShouldLoadMore = false;
            final ProgressBar loading = findViewById(R.id.searchresult_progressbar);
            loading.setVisibility(View.VISIBLE);


            final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    refreshLayout.setRefreshing(false);
                    loading.setVisibility(View.GONE);
                    itShouldLoadMore = true;

                    if (response.length() <= 0) {
                        // no data available
                        Toast.makeText(Search_Results.this, "No data available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < response.length(); i++) {
                        try {

                            JSONObject jsonObject = response.getJSONObject(i);

                            lastId = jsonObject.getString("id");
                            String username = jsonObject.getString("KEY_USERNAME");
                            String location = jsonObject.getString("KEY_LOCATION");
                            String email = jsonObject.getString("KEY_EMAIL");
                            String phonenumber = jsonObject.getString("KEY_PHONENUMBER");
                            String userpic = jsonObject.getString("KEY_PROFILEPICTURE");

                            profilesearch.add(new SearchProfile(username, userpic, email, phonenumber, location));
                            searchProfileAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    itShouldLoadMore = true;
                    loading.setVisibility(View.GONE);
                    Toast.makeText(Search_Results.this, "network error!", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(Search_Results.this)
                            .setMessage(error.toString())
                            .show();
                }
            });
            Volley.newRequestQueue(Search_Results.this).add(jsonArrayRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void SearchForMoreProfiles(String address, String searchableItem){
        itShouldLoadMore = false;

        String url;
        try {
            url = address + URLEncoder.encode(searchableItem, "UTF-8") + "&action=loadmore&lastId="+lastId;
            final ProgressBar progressWheel = (ProgressBar) this.findViewById(R.id.searchresult_progressbar2);
            progressWheel.setVisibility(View.VISIBLE);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressWheel.setVisibility(View.GONE);

                    itShouldLoadMore = true;

                    if (response.length() <= 0) {
                        Toast.makeText(Search_Results.this, "no data available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);


                            lastId = jsonObject.getString("id");
                            String username = jsonObject.getString("KEY_USERNAME");
                            String location = jsonObject.getString("KEY_LOCATION");
                            String email = jsonObject.getString("KEY_EMAIL");
                            String phonenumber = jsonObject.getString("KEY_PHONENUMBER");
                            String userpic = jsonObject.getString("KEY_PROFILEPICTURE");

                            profilesearch.add(new SearchProfile(username, userpic, email, phonenumber, location));
                            searchProfileAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressWheel.setVisibility(View.GONE);
                    itShouldLoadMore = true;
                    Toast.makeText(Search_Results.this, "Failed to load more, network error", Toast.LENGTH_SHORT).show();
                }
            });

            Volley.newRequestQueue(Search_Results.this).add(jsonArrayRequest);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
