package com.interceptor.mcamp;
//Done

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

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

    private boolean loading = true, latestVersion = true;
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
                    String playStoreLink = Objects.requireNonNull(dataSnapshot
                            .child("latestVersionLink").getValue()).toString().trim();
                    Common.newAppLink = playStoreLink;
                    if (Double.parseDouble((String) Objects.requireNonNull(dataSnapshot
                            .child("latestVersion").getValue())) > sharedVariable.getVersion()) {
                        Common.stopLoading();

                        latestVersion = false;
                        // Check if the link is a Play Store link
                        if (isPlayStoreLink(playStoreLink)) {
                            // Open the Play Store app
                            openPlayStore(playStoreLink);
                        } else {
                            // Open the link using a browser
                            openLinkInBrowser(playStoreLink);
                        }
                    } else {
                        latestVersion = true;
                        checkLogin();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    // Helper method to check if the link is a Play Store link
    private boolean isPlayStoreLink(String link) {
        return link != null && link.startsWith("https://play.google.com/store/apps/");
    }

    // Helper method to open the Play Store
    private void openPlayStore(String playStoreLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));
        intent.setPackage("com.android.vending"); // Specify the Play Store package

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle if the Play Store app is not installed
            Toast.makeText(this, "Play Store app not installed", Toast.LENGTH_SHORT).show();
            openLinkInBrowser(playStoreLink);
        }
    }

    // Helper method to open the link in a browser
    private void openLinkInBrowser(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle if no app is available to handle the link
            Toast.makeText(this, "No app available to handle the link", Toast.LENGTH_SHORT).show();
        }
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
        if(latestVersion)
            startActivity(new Intent(this, ActivityWelcome.class));
        else {
            if (isPlayStoreLink(Common.newAppLink)) {
                // Open the Play Store app
                openPlayStore(Common.newAppLink);
            } else {
                // Open the link using a browser
                openLinkInBrowser(Common.newAppLink);
            }
        }
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