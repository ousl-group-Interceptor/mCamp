package com.interceptor.mcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(!new SharedVariable(this).getUserID().equals("unknown")){
            //navigate to home page
        }
    }

    public void openWelcome(View view) {
        //navigate to welcome page
    }
}