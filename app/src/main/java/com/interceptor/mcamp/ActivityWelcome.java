package com.interceptor.mcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityWelcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void openSignIn(View view) {
        startActivity(new Intent(this, ActivitySignIn.class));
    }

    public void openSignUp(View view) {
        startActivity(new Intent(this, ActivitySignUp.class));
    }
}