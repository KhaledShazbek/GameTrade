package com.shazbek11.gametrade.Activities;

import android.content.Intent;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.shazbek11.gametrade.Adapters.RecyclerViewAdapter;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.VideoGamePost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class profilevisit extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    String Address = "http://shazbekgametrade.000webhostapp.com/profilevisit.php?username=";
    String postAddress = "http://shazbekgametrade.000webhostapp.com/profilepostvisit.php?username=";
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;
    private boolean itShouldLoadMore = true;
    private String lastId = "0";

    private RecyclerViewAdapter recyclerAdapter;
    private ArrayList<VideoGamePost> recyclerModels;

    private TextView EmailHolder;
    private TextView PhoneHolder;
    private TextView LocationHolder;
    CircleImageView userPhoto;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilevisit);

        bindActivity();
        mAppBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.visit_toolbar11);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final TextView username = findViewById(R.id.visit_username);

        userPhoto = findViewById(R.id.visit_userpic);

        Intent intent = getIntent();
        String name  = intent.getStringExtra("username");
        username.setText(name);

        EmailHolder = findViewById(R.id.visit_useremail);
        PhoneHolder = findViewById(R.id.visit_userphone);
        LocationHolder = findViewById(R.id.visit_userlocation);
        TextView title = findViewById(R.id.visit_title);
        title.setText(name);

        ImageButton backbutton = findViewById(R.id.profilevisit_backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String URL = Address + name;
        final String Post_URL = postAddress + name;
        GetInfo(URL);
        recyclerModels = new ArrayList<>();
        recyclerAdapter = new RecyclerViewAdapter(recyclerModels,profilevisit.this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.visit_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(profilevisit.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        firstLoadData(Post_URL);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            loadMore(Post_URL);
                        }
                    }
                }
            }
        });
    }

    private void firstLoadData(String url) {
        String link = url + "&limit=5";
        itShouldLoadMore = false;

        final ProgressBar loading = findViewById(R.id.loadingposts);
        loading.setVisibility(View.VISIBLE);

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( Request.Method.GET, link,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                loading.setVisibility(View.GONE);
                itShouldLoadMore = true;

                if (response.length() <= 0) {
                    // no data available
                    Toast.makeText(profilevisit.this, "No data available", Toast.LENGTH_SHORT).show();
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
                // also here, volley is not processing, unlock it should load more
                itShouldLoadMore = true;
                loading.setVisibility(View.GONE);
                Toast.makeText(profilevisit.this, "network error!", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(profilevisit.this, R.style.AppCompatAlertDialogStyle)
                        .setMessage(error.toString())
                        .show();
            }
        });
        Volley.newRequestQueue(profilevisit.this).add(jsonArrayRequest);
    }

    private void loadMore(String url) {

        String link = url + "&action=loadmore&lastId=" + lastId + "&limit=5";

        itShouldLoadMore = false;

        final ProgressBar progressWheel = (ProgressBar) this.findViewById(R.id.loadmoreposts);
        progressWheel.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( link,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressWheel.setVisibility(View.GONE);

                itShouldLoadMore = true;

                if (response.length() <= 0) {
                    Toast.makeText(profilevisit.this, "no data available", Toast.LENGTH_SHORT).show();
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
                // volley finished and returned network error, update and unlock  itShouldLoadMore
                itShouldLoadMore = true;
                Toast.makeText(profilevisit.this, "Failed to load more, network error", Toast.LENGTH_SHORT).show();

            }
        });

        Volley.newRequestQueue(profilevisit.this).add(jsonArrayRequest);

    }

    private void GetInfo(String url) {
        RequestQueue queue = Volley.newRequestQueue(profilevisit.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("success").equals("true")){
                        LocationHolder.setText(response.getString("location"));
                        PhoneHolder.setText(response.getString("phone"));
                        EmailHolder.setText(response.getString("email"));
                        if (!response.getString("profilepic").equals("null")){
                            Glide.with(profilevisit.this)
                                    .asBitmap()
                                    .load(response.getString("profilepic"))
                                    .into(userPhoto);
                        }else{
                            userPhoto.setImageDrawable(getDrawable(R.drawable.avatar_1577909_1280));
                        }
                    }else{
                        Toast failed = Toast.makeText(profilevisit.this, "Something went wrong!",Toast.LENGTH_SHORT);
                        failed.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast failure = Toast.makeText(profilevisit.this, "Try again later!", Toast.LENGTH_SHORT);
                failure.show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.visit_toolbar11);
        mTitle          = (TextView) findViewById(R.id.visit_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.visit_linearlayout);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.visit_appbar);
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
}
