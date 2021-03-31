package com.shazbek11.gametrade.Activities;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SharedPrefManager;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class VerifyPhoneNumber extends AppCompatActivity {

    ExtendedEditText OTP;
    private String verificationID = null;
    private String URL = "http://shazbekgametrade.000webhostapp.com/Verify.php?id=";
    ProgressDialog progress;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        progress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progress.setMessage("Please Wait");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        OTP = findViewById(R.id.OTP);

        SendOTP();

        ImageButton backbutton = findViewById(R.id.goback);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(VerifyPhoneNumber.this).logout();
                Intent loginscreen = new Intent(VerifyPhoneNumber.this,login.class);
                startActivity(loginscreen);
                finish();
            }
        });

        Button resendOTP = findViewById(R.id.resendOTP);
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendOTP();
            }
        });

        Button verifyOTP = findViewById(R.id.verifyOTP);
        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OTP.getText().toString().trim().isEmpty()){
                    Toast.makeText(VerifyPhoneNumber.this, "Please enter the code", Toast.LENGTH_SHORT).show();
                }else{
                    if (!verificationID.equals(null)) {
                        progress.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, OTP.getText().toString().trim());
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            }
        });

    }
    private void SendOTP(){
        progress.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+961"+SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserPhonenumber(),
                60 /*timeout*/,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            RequestQueue requestQueue = Volley.newRequestQueue(VerifyPhoneNumber.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL+SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserID().toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("OK")){
                                progress.cancel();
                                SharedPrefManager.getInstance(VerifyPhoneNumber.this).userLogin(SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUsername(),
                                        SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserID(),
                                        SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserEmail(),
                                        SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserLocation(),
                                        SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserPhonenumber(),
                                        "null",
                                        "true");
                                Intent intent = new Intent(VerifyPhoneNumber.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.cancel();
                    Toast.makeText(VerifyPhoneNumber.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(stringRequest);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progress.cancel();
            Toast.makeText(VerifyPhoneNumber.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            RequestQueue requestQueue = Volley.newRequestQueue(VerifyPhoneNumber.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL+SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserID().toString(),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if (response.equals("OK")){
                                                progress.cancel();
                                                SharedPrefManager.getInstance(VerifyPhoneNumber.this).userLogin(SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUsername(),
                                                        SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserID(),
                                                        SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserEmail(),
                                                        SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserLocation(),
                                                        SharedPrefManager.getInstance(VerifyPhoneNumber.this).getUserPhonenumber(),
                                                        "null",
                                                        "true");
                                                Intent intent = new Intent(VerifyPhoneNumber.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progress.cancel();
                                    Toast.makeText(VerifyPhoneNumber.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                            requestQueue.add(stringRequest);
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                progress.cancel();
                                // The verification code entered was invalid
                                Toast.makeText(VerifyPhoneNumber.this,"Something went wrong FA", Toast.LENGTH_SHORT).show();
                            }else{
                                progress.cancel();
                                Toast.makeText(VerifyPhoneNumber.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}


