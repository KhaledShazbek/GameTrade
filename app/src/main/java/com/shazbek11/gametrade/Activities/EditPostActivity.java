package com.shazbek11.gametrade.Activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SharedPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    String Address = "http://shazbekgametrade.000webhostapp.com/UploadPost.php";
    String Gameprice;
    String gamename;
    String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        final EditText Gamenameholder = findViewById(R.id.editText);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final ImageView gameimage = findViewById(R.id.Game_Pic);
        final Button Cancelbutton = findViewById(R.id.button4);

        Intent intent = getIntent();
        String image_path = intent.getStringExtra("GamePic");
        final Uri fileUri = Uri.parse(image_path);
        gameimage.setImageURI(fileUri);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] bytes = baos.toByteArray();
            image = Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final EditText editText_gameprice = findViewById(R.id.Game_Price11);
        final Switch switchbutton = findViewById(R.id.switch1);
        switchbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editText_gameprice.setVisibility(View.VISIBLE);
                }
                if (!isChecked){
                    editText_gameprice.setText("");
                    editText_gameprice.setVisibility(View.GONE);
                }
            }
        });

        final ProgressDialog progress;
        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setMessage("Please Wait");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        final Button UploadButton = findViewById(R.id.button3);
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                if (!editText_gameprice.getText().toString().equals("")) {
                    Gameprice = editText_gameprice.getText().toString().trim();
                } else {
                    Gameprice = "Trade";
                }
                gamename = Gamenameholder.getText().toString().trim();

                if (!gamename.equals("")){
                    RequestQueue queue = Volley.newRequestQueue(EditPostActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Address,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progress.cancel();
                                    if (response.equals("OK")){
                                        Toast success = Toast.makeText(EditPostActivity.this, "Image upload successful",Toast.LENGTH_SHORT);
                                        success.show();
                                        finish();
                                    }else{
                                        Toast error = Toast.makeText(EditPostActivity.this, response,Toast.LENGTH_SHORT);
                                        error.show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progress.cancel();
                                    Toast errormsg = Toast.makeText(EditPostActivity.this, error.getMessage(), Toast.LENGTH_SHORT);
                                    errormsg.show();
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("image",image);
                            params.put("price",Gameprice);
                            params.put("name",gamename);
                            params.put("FK",SharedPrefManager.getInstance(EditPostActivity.this).getUserID().toString().trim());
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                }else{
                    progress.cancel();
                    Toast.makeText(EditPostActivity.this,"Please type in a game name",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

