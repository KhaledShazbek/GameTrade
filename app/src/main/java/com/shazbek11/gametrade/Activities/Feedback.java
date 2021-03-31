package com.shazbek11.gametrade.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder ExitMessage = new AlertDialog.Builder(this);
        ExitMessage.setMessage("Are you sure you want to discard the message?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        final EditText feedback = findViewById(R.id.feedback_text);

        findViewById(R.id.feedback_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.feedback_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = feedback.getText().toString().trim();
                final ProgressDialog progress;
                progress = new ProgressDialog(Feedback.this, R.style.AppCompatAlertDialogStyle);
                progress.setMessage("Please Wait");
                progress.setCancelable(false);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();

                RequestQueue queue = Volley.newRequestQueue(Feedback.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        "http://shazbekgametrade.000webhostapp.com/feedback.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progress.cancel();
                                Toast.makeText(Feedback.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.cancel();
                        Toast.makeText(Feedback.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("message",message);
                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });
    }
}
