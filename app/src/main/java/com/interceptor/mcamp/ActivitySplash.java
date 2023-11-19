package com.interceptor.mcamp;
//Done

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ActivitySplash extends AppCompatActivity {

    private boolean loading = true;
    SharedVariable sharedVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedVariable = new SharedVariable(this);
        sharedVariable.setOnSplash(true);

        checkVersion(new boolean[]{true});

        new Handler().postDelayed(() -> {
            if (loading)
                Common.startLoading(ActivitySplash.this, "Connecting...");
        }, 1000);

    }

    private void checkVersion(boolean[] run) {
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        Data.child("AppAttributes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (Double.parseDouble((String) Objects.requireNonNull(dataSnapshot
                            .child("latestVersion").getValue())) > sharedVariable.getVersion()) {
                        String link = Objects.requireNonNull(dataSnapshot
                                .child("latestVersionLink").getValue()).toString().trim();
                        Common.stopLoading();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                    } else
                        checkLogin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void checkLogin() {
        if (!sharedVariable.getUserID().equals("unknown")) {
            //navigate to home
            Common.stopLoading();
            sharedVariable.setOnSplash(false);
            startActivity(new Intent(this, ActivityHome.class));
            finish();
        } else {
            loading = false;
            Common.stopLoading();
            new Handler().postDelayed(() -> {
                if (sharedVariable.getOnSplash()) {
                    startActivity(new Intent(ActivitySplash.this, ActivityWelcome.class));
                }
            }, 5000);
        }
    }

    public void openWelcome(View view) {
        //navigate to welcome page
        sharedVariable.setOnSplash(false);
        startActivity(new Intent(this, ActivityWelcome.class));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", null)
                .show();
    }
}