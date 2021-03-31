package com.shazbek11.gametrade.Activities;


import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.shazbek11.gametrade.Adapters.ProfileRecyclerAdapter;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.ProfileGamePost;
import com.shazbek11.gametrade.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener  {

    String URLaddress = "http://shazbekgametrade.000webhostapp.com/profileposts.php?limit=5&ID=";
    private boolean itShouldLoadMore = true;
    private String lastId = "0";

    private ProfileRecyclerAdapter recyclerAdapter;
    private ArrayList<ProfileGamePost> recyclerModels;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    CircleImageView profilepic;

    TextView title;
    TextView username;
    TextView useremail;
    TextView userphonenumber;
    TextView userlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bindActivity();

        final String ADDRESS = URLaddress + SharedPrefManager.getInstance(this).getUserID().toString();

        profilepic = findViewById(R.id.profileactivity_userpic);
        if (!SharedPrefManager.getInstance(Profile.this).getUserProfilepic().equals("null")){
            Glide.with(Profile.this)
                    .asBitmap()
                    .load(SharedPrefManager.getInstance(Profile.this).getUserProfilepic())
                    .into(profilepic);
        }else{
            profilepic.setImageDrawable(getResources().getDrawable(R.drawable.avatar_1577909_1280));
        }

        recyclerModels = new ArrayList<>();
        recyclerAdapter = new ProfileRecyclerAdapter(recyclerModels,Profile.this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.profile_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(Profile.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        mAppBarLayout.addOnOffsetChangedListener(this);

        mToolbar.inflateMenu(R.menu.menu_main);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);


        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar11);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton BackButton = findViewById(R.id.profile_backbutton);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile.super.onBackPressed();
            }
        });

        title = findViewById(R.id.main_textview_title);
        username = findViewById(R.id.profileactivity_username);
        useremail = findViewById(R.id.profileActivity_useremail);
        userphonenumber = findViewById(R.id.profileActivity_userphone);
        userlocation = findViewById(R.id.profileActivity_userlocation);
        title.setText(SharedPrefManager.getInstance(Profile.this).getUsername());
        username.setText(SharedPrefManager.getInstance(Profile.this).getUsername());
        useremail.setText(SharedPrefManager.getInstance(Profile.this).getUserEmail());
        userphonenumber.setText(SharedPrefManager.getInstance(Profile.this).getUserPhonenumber());
        userlocation.setText(SharedPrefManager.getInstance(Profile.this).getUserLocation());

        LoadData(ADDRESS);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            loadMore(ADDRESS);
                        }
                    }
                }
            }
        });

    }

    private void LoadData(String url){
        itShouldLoadMore = false;

        final ProgressBar loading = findViewById(R.id.profile_progressbar);
        loading.setVisibility(View.VISIBLE);

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                loading.setVisibility(View.GONE);
                itShouldLoadMore = true;

                if (response.length() <= 0) {
                    Toast.makeText(Profile.this, "No data available", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("id");
                        String id = jsonObject.getString("id");
                        String gamename = jsonObject.getString("KEY_GAMENAME");
                        String price = jsonObject.getString("KEY_PRICE");
                        String gamepic = jsonObject.getString("KEY_GAMEPIC");

                        recyclerModels.add(new ProfileGamePost(id, gamename, price, gamepic));
                        recyclerAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // also here, volley is not processing, unlock it should load more
                itShouldLoadMore = true;
                loading.setVisibility(View.GONE);
                Toast.makeText(Profile.this, "network error!", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(Profile.this, R.style.AppCompatAlertDialogStyle)
                        .setMessage(error.toString())
                        .show();
            }
        });
        Volley.newRequestQueue(Profile.this).add(jsonArrayRequest);

    }

    private void loadMore(String link){

        String url = link + "&action=loadmore&lastId="+lastId;
        itShouldLoadMore = false;

        final ProgressBar progressWheel = (ProgressBar) this.findViewById(R.id.profile_loadmore);
        progressWheel.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressWheel.setVisibility(View.GONE);

                itShouldLoadMore = true;

                if (response.length() <= 0) {
                    Toast.makeText(Profile.this, "no data available", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("id");
                        String id = jsonObject.getString("id");
                        String gamename = jsonObject.getString("KEY_GAMENAME");
                        String price = jsonObject.getString("KEY_PRICE");
                        String gamepic = jsonObject.getString("KEY_GAMEPIC");

                        recyclerModels.add(new ProfileGamePost(id, gamename, price, gamepic));
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
                Toast.makeText(Profile.this, "Failed to load more, network error", Toast.LENGTH_SHORT).show();

            }
        });

        Volley.newRequestQueue(Profile.this).add(jsonArrayRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_prof){
            Intent editprofileintent = new Intent(Profile.this, EditProfile.class);
            startActivity(editprofileintent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.toolbar11);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SharedPrefManager.getInstance(Profile.this).getUserProfilepic().equals("null")) {
            Glide.with(Profile.this)
                    .asBitmap()
                    .load(SharedPrefManager.getInstance(Profile.this).getUserProfilepic())
                    .into(profilepic);
        } else {
            profilepic.setImageDrawable(getResources().getDrawable(R.drawable.avatar_1577909_1280));
        }
        title.setText(SharedPrefManager.getInstance(Profile.this).getUsername());
        username.setText(SharedPrefManager.getInstance(Profile.this).getUsername());
        useremail.setText(SharedPrefManager.getInstance(Profile.this).getUserEmail());
        userphonenumber.setText(SharedPrefManager.getInstance(Profile.this).getUserPhonenumber());
        userlocation.setText(SharedPrefManager.getInstance(Profile.this).getUserLocation());
    }

}
