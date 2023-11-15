package com.interceptor.mcamp;
//Done

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    SharedVariable sharedVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Common.startLoading(this, "Connecting...");
        sharedVariable = new SharedVariable(this);

        checkVersion(new boolean[]{true});
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
                    }
                    else
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
            startActivity(new Intent(this, ActivityHome.class));
        }else
            Common.stopLoading();
    }

    public void openWelcome(View view) {
        //navigate to welcome page
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