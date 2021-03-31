package com.shazbek11.gametrade.Activities;

import android.app.ProgressDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SharedPrefManager;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        final ProgressDialog progress;
        progress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progress.setMessage("Please Wait");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        final ExtendedEditText oldpassholder = findViewById(R.id.changepass_oldpass);
        final ExtendedEditText newpassholder = findViewById(R.id.changepass_newpass);
        final ExtendedEditText renewpassholder = findViewById(R.id.changepass_renewpass);

        Button cancelButton = findViewById(R.id.changepass_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button saveButton = findViewById(R.id.changepass_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();

                final String oldpass = oldpassholder.getText().toString();
                final String newpass = newpassholder.getText().toString();
                final String renewpass = renewpassholder.getText().toString();
                final String url = "http://shazbekgametrade.000webhostapp.com/ChangePass.php?id="+SharedPrefManager.getInstance(ChangePassword.this).getUserID().toString()+"&oldpass="+oldpass+"&newpass="+newpass;

                if (newpass.equals(renewpass)){
                    RequestQueue queue = Volley.newRequestQueue(ChangePassword.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("CHANGED")) {
                                progress.cancel();
                                Toast toast = Toast.makeText(ChangePassword.this, "Password changed successfully", Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            } else {
                                progress.cancel();
                                AlertDialog.Builder error = new AlertDialog.Builder(ChangePassword.this);
                                error.setMessage(response);
                                error.show();
                                oldpassholder.setText("");
                                newpassholder.setText("");
                                renewpassholder.setText("");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.cancel();
                            AlertDialog.Builder errormsg = new AlertDialog.Builder(ChangePassword.this);
                            errormsg.setMessage(error.getMessage());
                            errormsg.show();
                        }
                    });
                    queue.add(stringRequest);
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangePassword.this);
                    builder.setMessage("Passwords don't match!");
                    builder.show();
                    newpassholder.setText("");
                    renewpassholder.setText("");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
