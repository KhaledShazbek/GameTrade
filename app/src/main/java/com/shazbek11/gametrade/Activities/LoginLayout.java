package com.shazbek11.gametrade.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class LoginLayout extends Fragment {

    private String Address = "http://shazbekgametrade.000webhostapp.com/Login.php";
    private static int setVisibility = 1;

    public LoginLayout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_layout, container, false);

        TextView forgotpassword = view.findViewById(R.id.forgotpassword);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ForgotPassword.class);
                startActivity(intent);
            }
        });


        final Button loginbutton = view.findViewById(R.id.loginbutton);
        final ExtendedEditText name = view.findViewById(R.id.inputSearchEditText);
        final ExtendedEditText passwd = view.findViewById(R.id.inputSearchEditText2);

        final TextFieldBoxes nameholder = view.findViewById(R.id.text_field_boxes);
        nameholder.setFocusableInTouchMode(false);
        nameholder.setFocusable(false);
        nameholder.setFocusableInTouchMode(true);
        nameholder.setFocusable(true);


        final TextFieldBoxes passwordholder = view.findViewById(R.id.text_field_boxes2);
        passwordholder.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setVisibility==1)
                {
                    setVisibility=0;
                    passwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordholder.setEndIcon(R.drawable.notvisible);
                }else {
                    setVisibility=1;
                    passwordholder.setEndIcon(R.drawable.visible);
                    passwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        final ProgressDialog progress;
        progress = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Logging in");
        progress.setMessage("Please Wait");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress.show();
                final String username = name.getText().toString().trim();
                final String password = passwd.getText().toString().trim();
                if (!username.equals("") && !password.equals("")){
                    String URL = Address + "?username=" + username + "&password=" + password;
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    StringRequest stringReq = new StringRequest(Request.Method.GET, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    if (s.equals("FAILED")) {
                                        progress.cancel();
                                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                        builder.setMessage("Check your Username or Password");
                                        builder.setTitle("Authentication error");
                                        builder.show();
                                    } else {
                                        progress.cancel();
                                        try{
                                            JSONObject obj = new JSONObject(s);
                                            SharedPrefManager.getInstance(getActivity().getApplicationContext()).userLogin(username,
                                                    obj.getInt("id"),
                                                    obj.getString("email"),
                                                    obj.getString("location"),
                                                    obj.getString("phonenumber"),
                                                    obj.getString("profilepic"),
                                                    obj.getString("isVerified"));
                                            if (obj.getString("isVerified").equals("true")){
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }else{
                                                Intent verify = new Intent(getActivity(), VerifyPhoneNumber.class);
                                                startActivity(verify);
                                                getActivity().finish();
                                            }
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progress.cancel();
                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                            builder.setMessage(volleyError.getMessage());
                            builder.setTitle("Authentication error!");
                            builder.show();
                        }
                    });
                    queue.add(stringReq);
                }else{
                    progress.cancel();
                    AlertDialog.Builder message = new AlertDialog.Builder(getActivity());
                    message.setMessage("Fill in required fields");
                    message.show();
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}
