package com.shazbek11.gametrade.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shazbek11.gametrade.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ForgotPassword extends AppCompatActivity {

    private String PhoneNumber;
    private String verificationID = null;
    private String newpass;
    private String name;
    private Button proceed;
    private ProgressDialog progress;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setMessage("Please Wait");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        final ExtendedEditText recoveryCode = findViewById(R.id.forgotpassword_otpholder);

        proceed = findViewById(R.id.proceed);

        ImageButton backbutton = findViewById(R.id.forgotpassword_back);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Button getnumber = findViewById(R.id.forgotpassword_send);
        getnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExtendedEditText username = findViewById(R.id.forgotpass_usernameholder);
                name = username.getText().toString().trim();
                if (name.equals("")){
                    Toast.makeText(ForgotPassword.this, "Please write your username",Toast.LENGTH_SHORT).show();
                }else{
                    progress.show();
                    RequestQueue requestQueue = Volley.newRequestQueue(ForgotPassword.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET,
                            "http://shazbekgametrade.000webhostapp.com/getnumber.php?user=" + name,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("Something went wrong")){
                                        progress.cancel();
                                        Toast.makeText(ForgotPassword.this, "Check your username!", Toast.LENGTH_SHORT).show();
                                    }else{

                                        PhoneNumber = "+961"+response;
                                        getnumber.setVisibility(View.GONE);
                                        TextFieldBoxes recoverycode = findViewById(R.id.forgotpassword_otp);
                                        recoverycode.setVisibility(View.VISIBLE);
                                        proceed.setVisibility(View.VISIBLE);
                                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                PhoneNumber,
                                                60 ,
                                                TimeUnit.SECONDS,
                                                ForgotPassword.this,
                                                mCallbacks);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.cancel();
                            Toast.makeText(ForgotPassword.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(stringRequest);
                }

            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recoveryCode.getText().toString().trim().isEmpty()){
                    Toast.makeText(ForgotPassword.this, "Please enter the code", Toast.LENGTH_SHORT).show();
                }else{
                    if (!verificationID.equals(null)) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, recoveryCode.getText().toString().trim());
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            }
        });

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Changepass();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progress.cancel();
            Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            progress.cancel();
            verificationID = verificationId;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Changepass();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(ForgotPassword.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ForgotPassword.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void Changepass(){
        progress.cancel();
        final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
        builder.setTitle("New password");
        final EditText input = new EditText(ForgotPassword.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newpass = input.getText().toString().trim();
                if (newpass.length()<8){
                    Toast.makeText(ForgotPassword.this, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
                    Changepass();
                }else{
                    progress.show();
                    final RequestQueue requestQueue = Volley.newRequestQueue(ForgotPassword.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            "http://shazbekgametrade.000webhostapp.com/resetpassword.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progress.cancel();
                                    if (response.equals("DONE")){
                                        Intent intent = new Intent(ForgotPassword.this, login.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(ForgotPassword.this, "Something went wrong! Try again later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.cancel();
                            Toast.makeText(ForgotPassword.this, "Something went wrong! Try again later", Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("username", name);
                            params.put("password", newpass);
                            return params;
                        }
                    };
                    requestQueue.add(stringRequest);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
