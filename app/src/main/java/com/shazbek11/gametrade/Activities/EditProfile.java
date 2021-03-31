package com.shazbek11.gametrade.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SharedPrefManager;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class EditProfile extends Activity {

    private static final int RESULT_LOAD_IMAGE1=1;
    public final String SAMPLE_CROPED_IMG_NAME = "cropedimgname";
    String url = "http://shazbekgametrade.000webhostapp.com/EditProfile.php";
    ExtendedEditText usernameHolder;
    ExtendedEditText emailHolder;
    ExtendedEditText locationHolder;
    ExtendedEditText phonenumberHolder;
    CircularImageView Profile_Pic;
    String image;
    Uri imageUriCroped = null;
    String checkChanges;
    String imageURL;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Profile_Pic = findViewById(R.id.editprofile_userpic);
        if(requestCode==RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK && data != null){
            Uri selectedimg = data.getData();
            StartCrop(selectedimg);
        }else
            if (requestCode == UCrop.REQUEST_CROP && data != null){
                imageUriCroped = UCrop.getOutput(data);
                Profile_Pic.setImageURI(imageUriCroped);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final Integer userID = SharedPrefManager.getInstance(EditProfile.this).getUserID();
        final String userpic = SharedPrefManager.getInstance(EditProfile.this).getUserProfilepic();

        final ProgressDialog progress;
        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setMessage("Please Wait");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        RelativeLayout rel = findViewById(R.id.editprofile_parent);
        rel.requestFocus();

        Profile_Pic = findViewById(R.id.editprofile_userpic);
        if (!SharedPrefManager.getInstance(EditProfile.this).getUserProfilepic().equals("null")){
            Glide.with(EditProfile.this)
                    .asBitmap()
                    .load(SharedPrefManager.getInstance(EditProfile.this).getUserProfilepic())
                    .into(Profile_Pic);
        }else{
            Profile_Pic.setImageDrawable(getResources().getDrawable(R.drawable.avatar_1577909_1280));
        }

        ImageButton CancelButton = findViewById(R.id.editprofile_cancelbutton);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView ChangesPassword = findViewById(R.id.editprofile_changepassword);
        ChangesPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(EditProfile.this, ChangePassword.class);
                startActivity(intent);
            }
        });

        usernameHolder = findViewById(R.id.editprofile_username);
        emailHolder = findViewById(R.id.editprofile_emailaddress);
        locationHolder = findViewById(R.id.editprofile_Location);
        phonenumberHolder = findViewById(R.id.editprofile_Phonenumber);
        usernameHolder.setText(SharedPrefManager.getInstance(EditProfile.this).getUsername());
        emailHolder.setText(SharedPrefManager.getInstance(EditProfile.this).getUserEmail());
        locationHolder.setText(SharedPrefManager.getInstance(EditProfile.this).getUserLocation());
        phonenumberHolder.setText(SharedPrefManager.getInstance(EditProfile.this).getUserPhonenumber());

        final TextView ChangeProfilePic = findViewById(R.id.changeprofilepic);
        ChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryintent,RESULT_LOAD_IMAGE1);
            }
        });

        ImageButton saveButton = findViewById(R.id.editprofile_savebutton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                final String username = usernameHolder.getText().toString().trim();
                final String email = emailHolder.getText().toString().trim();
                final String location = locationHolder.getText().toString().trim();
                final String phonenumber = phonenumberHolder.getText().toString().trim();
                if (imageUriCroped != null){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditProfile.this.getContentResolver(), imageUriCroped);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
                        byte[] bytes = baos.toByteArray();
                        image = Base64.encodeToString(bytes, Base64.DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    image = "null";
                }
                if (!username.equals("") && !email.equals("") && !location.equals("") && !phonenumber.equals("")){
                    RequestQueue queue = Volley.newRequestQueue(EditProfile.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progress.cancel();
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                checkChanges = jsonObj.getString("success");
                                imageURL = jsonObj.getString("url");
                                if (checkChanges.equalsIgnoreCase("CHANGED")){
                                    if (image.equals("null")){
                                        SharedPrefManager.getInstance(EditProfile.this).userLogin(username,
                                                userID,
                                                email,
                                                location,
                                                phonenumber,
                                                userpic,
                                                "true");
                                    }else{
                                        SharedPrefManager.getInstance(EditProfile.this).userLogin(username,
                                                userID,
                                                email,
                                                location,
                                                phonenumber,
                                                imageURL,
                                                "true");
                                    }
                                    Toast changed = Toast.makeText(EditProfile.this , "Changes saved", Toast.LENGTH_SHORT);
                                    changed.show();
                                    finish();
                                }else{
                                    Toast error = Toast.makeText(EditProfile.this, response, Toast.LENGTH_SHORT);
                                    error.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.cancel();
                            Toast errormsg = Toast.makeText(EditProfile.this, error.getMessage(), Toast.LENGTH_SHORT);
                            errormsg.show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("NAME",username);
                            params.put("EMAIL",email);
                            params.put("PHONE",phonenumber);
                            params.put("LOCATION",location);
                            params.put("PROFILEPIC",image);
                            params.put("FK",SharedPrefManager.getInstance(EditProfile.this).getUserID().toString().trim());
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                }else{
                    progress.cancel();
                    AlertDialog.Builder Alert = new AlertDialog.Builder(EditProfile.this);
                    Alert.setMessage("Please fill in all the fields!");
                    Alert.show();
                }
            }
        });

    }

    public void StartCrop(@Nullable Uri uri){
        String DestinationFileName = SAMPLE_CROPED_IMG_NAME;
        DestinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),DestinationFileName)));
        uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(1000,1000);
        uCrop.withOptions(getCropOption());
        uCrop.start(EditProfile.this);
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
    public void onBackPressed() {
        finish();
    }
}
