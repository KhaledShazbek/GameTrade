package com.shazbek11.gametrade.Activities;


import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SharedPrefManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class RegisterLayout extends Fragment {

    String Address = "http://shazbekgametrade.000webhostapp.com/Register.php";
    String location;

    private static int setVisibilitypas1 = 1;
    private static int setVisibilitypas2 = 1;

    public RegisterLayout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_register_layout, container, false);


        final ExtendedEditText Username = view.findViewById(R.id.Username);
        final ExtendedEditText Password = view.findViewById(R.id.Password);
        final ExtendedEditText RetypePassword = view.findViewById(R.id.RetypePassword);
        final ExtendedEditText EmailAddress = view.findViewById(R.id.EmailAddress);
        final ExtendedEditText Location = view.findViewById(R.id.Location);
        final ExtendedEditText PhoneNumber = view.findViewById(R.id.PhoneNumber);
        final Button SignupButton = view.findViewById(R.id.signup);

        final TextFieldBoxes passwordholder = view.findViewById(R.id.register_passholder);
        passwordholder.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setVisibilitypas1==1)
                {
                    setVisibilitypas1=0;
                    Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordholder.setEndIcon(R.drawable.notvisible);
                }else {
                    setVisibilitypas1=1;
                    passwordholder.setEndIcon(R.drawable.visible);
                    Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        final TextFieldBoxes repasswordholder = view.findViewById(R.id.register_repassholder);
        repasswordholder.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setVisibilitypas2==1)
                {
                    setVisibilitypas2=0;
                    RetypePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    repasswordholder.setEndIcon(R.drawable.notvisible);
                }else {
                    setVisibilitypas2=1;
                    repasswordholder.setEndIcon(R.drawable.visible);
                    RetypePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        final ProgressDialog progress;
        progress = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Signing up");
        progress.setMessage("Please Wait");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                passwordholder.validate();
                repasswordholder.validate();
                final TextFieldBoxes phonenumber = view.findViewById(R.id.phoneholder);
                phonenumber.validate();

                if (passwordholder.isOnError() || repasswordholder.isOnError() || phonenumber.isOnError()){
                    progress.cancel();
                    AlertDialog.Builder wrongpass = new AlertDialog.Builder(getActivity());
                    wrongpass.setMessage("Please check your info");
                    wrongpass.show();
                }else{
                    final String username = Username.getText().toString().trim();
                    final String password = Password.getText().toString().trim();
                    final String retypepasswprd = RetypePassword.getText().toString().trim();
                    final String emailaddress = EmailAddress.getText().toString().trim();
                    final String locationholder = Location.getText().toString().trim();
                    try {
                        location = URLEncoder.encode(locationholder,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    final String phone = PhoneNumber.getText().toString().trim();

                    if (!username.equals("") && !password.equals("") && !retypepasswprd.equals("") && !emailaddress.equals("") && !location.equals("") && !phone.equals("")){
                        if (!password.equals(retypepasswprd)){
                            progress.cancel();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Passwords don't match!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            Button positivebutton;
                            positivebutton=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            positivebutton.setTextColor(getResources().getColor(R.color.textcolor));
                        }else {
                            //volley request
                            String URL = Address + "?username="+username+"&password="+password+"&location="+location+"&email="+emailaddress+"&phonenumber="+phone;
                            RequestQueue queue = Volley.newRequestQueue(getActivity());
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    if (!s.equals("FAILED")){
                                        progress.cancel();
                                        if (s.equals("Username Already Exist, Please Choose Another one.")){
                                            AlertDialog.Builder errormessage = new AlertDialog.Builder(getActivity());
                                            errormessage.setMessage(s);
                                            errormessage.show();
                                        }else {
                                            SharedPrefManager.getInstance(getActivity().getApplicationContext()).userLogin(username,
                                                    Integer.parseInt(s),
                                                    emailaddress,
                                                    locationholder,
                                                    phone,
                                                    "null",
                                                    "false");
                                            Intent intent1 = new Intent(getActivity(), VerifyPhoneNumber.class);
                                            startActivity(intent1);
                                            getActivity().finish();
                                        }
                                    }else{
                                        progress.cancel();
                                        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(getActivity());
                                        builder.setMessage(s);
                                        builder.show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    progress.cancel();
                                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(getActivity());
                                    builder.setMessage("Something went wrong!");
                                    builder.show();
                                }
                            });
                            queue.add(stringRequest);
                        }
                    }else
                    {
                        progress.cancel();
                        AlertDialog.Builder Alert = new AlertDialog.Builder(getActivity());
                        Alert.setMessage("Please fill in all the fields!");
                        Alert.show();
                    }
                }

            }
        });

        return view;
    }

}
