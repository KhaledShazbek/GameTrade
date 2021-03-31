package com.shazbek11.gametrade.Activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.shazbek11.gametrade.Adapters.ViewPagerAdapter;
import com.shazbek11.gametrade.R;
import com.shazbek11.gametrade.utils.SharedPrefManager;


public class login extends AppCompatActivity {


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String verified = SharedPrefManager.getInstance(login.this).isVerified();
        String TRUE = "true";
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            if (verified.equalsIgnoreCase(TRUE)){
                finish();
                startActivity(new Intent(this, MainActivity.class));
                return;
            }else{
                finish();
                startActivity(new Intent(this, VerifyPhoneNumber.class));
                return;
            }
        }

        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.AddFragment(new LoginLayout(),"Sign In");
        viewPagerAdapter.AddFragment(new RegisterLayout(),"Register");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }
}
