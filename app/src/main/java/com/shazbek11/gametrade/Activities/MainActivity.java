package com.shazbek11.gametrade.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.shazbek11.gametrade.Adapters.RecyclerViewAdapter;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SharedPrefManager;
import com.shazbek11.gametrade.utils.VideoGamePost;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainactivity";

    public static final int RESULT_LOAD_IMAGE = 0;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public final String SAMPLE_CROPED_IMG_NAME = "cropedimgname";
    public String currentPhotoPath;
    MaterialSearchView materialSearchView;
    private static final int LOAD_LIMIT = 3;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;

    private RecyclerViewAdapter recyclerAdapter;
    private ArrayList<VideoGamePost> recyclerModels;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onBackPressed() {
        if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        } else {
            final AlertDialog.Builder ExitMessage = new AlertDialog.Builder(this);
            ExitMessage.setMessage("Are you sure you want to Exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = ExitMessage.create();
            dialog.show();
            Button positivebutton;
            positivebutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positivebutton.setTextColor(getResources().getColor(R.color.indigo));
            Button negativebuutton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativebuutton.setTextColor(getResources().getColor(R.color.indigo));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String verified = SharedPrefManager.getInstance(MainActivity.this).isVerified();
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, login.class));
        }else{
            if (verified.equals("false")){
                finish();
                startActivity(new Intent(this, VerifyPhoneNumber.class));
            }
        }

        recyclerModels = new ArrayList<>();
        recyclerAdapter = new RecyclerViewAdapter(recyclerModels, MainActivity.this);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview11);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        firstLoadData();

        swipeRefreshLayout = findViewById(R.id.main_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerAdapter.clear();
                firstLoadData();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            loadMore();
                        }
                    }
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        materialSearchView = findViewById(R.id.Searchbar);
        materialSearchView.setBackIcon(getResources().getDrawable(R.drawable.searchview_back));
        materialSearchView.setCloseIcon(getResources().getDrawable(R.drawable.searchview_close));
        materialSearchView.closeSearch();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                } else {
                    SelectImage();
                }
            }
        });
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, Search_Results.class);
                intent.putExtra("searchitem", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapter.clear();
        firstLoadData();
    }

    private void SelectImage() {
        final CharSequence[] items = {"Camera", "Gallery"};
        final AlertDialog.Builder pickimage = new AlertDialog.Builder(MainActivity.this);
        pickimage.setTitle("Pick image from:");
        pickimage.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                2);
                    } else {
                        Intent takepictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takepictureintent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                        "com.example.android.fileprovider",
                                        photoFile);
                                takepictureintent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takepictureintent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    }
                } else if (items[which].equals("Gallery")) {
                    Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryintent, RESULT_LOAD_IMAGE);
                }
            }
        });
        pickimage.create();
        pickimage.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedimg = data.getData();
                    if (selectedimg != null) {
                        StartCrop(selectedimg);
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    File file = new File(currentPhotoPath);
                    Uri imgUri = Uri.fromFile(file);
                    if (imgUri != null) {
                        StartCrop(imgUri);
                    }
                }
                break;
            case UCrop
                    .REQUEST_CROP:
                if (resultCode == RESULT_OK && data != null) {
                    Uri imageUriCroped = UCrop.getOutput(data);
                    Intent editpost = new Intent(MainActivity.this, EditPostActivity.class);
                    editpost.putExtra("GamePic", imageUriCroped.toString());
                    startActivity(editpost);
                }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void StartCrop(@Nullable Uri uri) {
        String DestinationFileName = SAMPLE_CROPED_IMG_NAME;
        DestinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), DestinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(1000, 1000);
        uCrop.withOptions(getCropOption());
        uCrop.start(MainActivity.this);
    }

    private UCrop.Options getCropOption() {
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarTitle("Crop image");

        return options;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_searchview, menu);
        MenuItem item1 = menu.findItem(R.id.search_button);
        materialSearchView.setMenuItem(item1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            final Intent intent3 = new Intent(MainActivity.this, Profile.class);
            startActivity(intent3);
        }

        if (id == R.id.action_feedback){
            Intent feedback = new Intent(MainActivity.this, Feedback.class);
            startActivity(feedback);
        }

        if (id == R.id.action_logout) {
            final AlertDialog.Builder logoutmsg = new AlertDialog.Builder(MainActivity.this);
            logoutmsg.setMessage("Do you really want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPrefManager.getInstance(MainActivity.this).logout();
                            finish();
                            startActivity(new Intent(MainActivity.this, login.class));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = logoutmsg.create();
            dialog.show();
            Button positivebutton;
            positivebutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positivebutton.setTextColor(getResources().getColor(R.color.indigo));
            Button negativebuutton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativebuutton.setTextColor(getResources().getColor(R.color.indigo));
        }

        return super.onOptionsItemSelected(item);
    }

    private void firstLoadData() {

        String url = "http://shazbekgametrade.000webhostapp.com/paging.php?limit=" + LOAD_LIMIT;

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        final ProgressBar loading = findViewById(R.id.waitingfordata);
        loading.setVisibility(View.VISIBLE);

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                loading.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                // remember here we are in the main thread, that means,
                //volley has finished processing request, and we have our response.
                // What else are you waiting for? update itShouldLoadMore = true;
                itShouldLoadMore = true;

                if (response.length() <= 0) {
                    // no data available
                    Toast.makeText(MainActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);

                        // please note this last id how we have updated it
                        // if there are 4 items for example, and we are ordering in descending order,
                        // then last id will be 1. This is because outside a loop, we will get the last
                        // value [Thanks to JAVA]

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
                // please note how we have updated our last id variable which is initially 0 (String)
                // outside the loop, java will return the last value, so here it will
                // certainly give us lastId that we need
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // also here, volley is not processing, unlock it should load more
                itShouldLoadMore = true;
                loading.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "network error!", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(error.toString())
                        .show();
            }
        });
        Volley.newRequestQueue(MainActivity.this).add(jsonArrayRequest);
    }

    private void loadMore() {


        String url = "http://shazbekgametrade.000webhostapp.com/paging.php?action=loadmore&lastId=" + lastId + "&limit=" + LOAD_LIMIT;
        // our php page starts loading from 250 to 1, because we have [ORDER BY id DESC]
        // So until you clearly understand everything, for this tutorial use ORDER BY ID DESC
        // so we will do something like this to the php page
        //==============================================
        // $limit = $_GET['limit']
        // $lastId = $_GET['lastId']
        // then [SELECT * FROM table_name WHERE id < $lastId ORDER_BY id DESC LIMIT $limit ]
        // here we shall load 15 items from table where lastId id less than last loaded id

        // if you are using [ASC] in sql, your query might change to tis
        // then [SELECT * FROM table_name WHERE id > $lastId ORDER_BY id DESC LIMIT $limit ]
        // for this tutorial let's stick to [DESC]


        itShouldLoadMore = false; // lock this until volley completes processing

        // progressWheel is just a loading spinner, please see the content_main.xml
        final ProgressBar progressWheel = (ProgressBar) this.findViewById(R.id.progressBar1);
        progressWheel.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressWheel.setVisibility(View.GONE);

                // since volley has completed and it has our response, now let's update
                // itShouldLoadMore

                itShouldLoadMore = true;

                if (response.length() <= 0) {
                    // we need to check this, to make sure, our dataStructure JSonArray contains
                    // something
                    Toast.makeText(MainActivity.this, "no data available", Toast.LENGTH_SHORT).show();
                    return; // return will end the program at this point
                }

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // please note how we have updated the lastId variable
                        // if there are 4 items for example, and we are ordering in descending order,
                        // then last id will be 1. This is because outside a loop, we will get the last
                        // value

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
                Toast.makeText(MainActivity.this, "Failed to load more, network error", Toast.LENGTH_SHORT).show();

            }
        });

        Volley.newRequestQueue(MainActivity.this).add(jsonArrayRequest);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage();
                } else {
                    AlertDialog.Builder Alert = new AlertDialog.Builder(MainActivity.this);
                    Alert.setMessage("Please enable permission");
                    Alert.show();
                }
                break;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                3);
                    } else {
                        Intent takepictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takepictureintent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                        "com.example.android.fileprovider",
                                        photoFile);
                                takepictureintent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takepictureintent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    }

                }else{
                    AlertDialog.Builder Alert = new AlertDialog.Builder(MainActivity.this);
                    Alert.setMessage("Please enable permission");
                    Alert.show();
                }
                break;
            }
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent takepictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takepictureintent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                    "com.example.android.fileprovider",
                                    photoFile);
                            takepictureintent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takepictureintent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }else{
                    AlertDialog.Builder Alert = new AlertDialog.Builder(MainActivity.this);
                    Alert.setMessage("Please enable permission");
                    Alert.show();
                }
                break;
            }
        }

    }
}
