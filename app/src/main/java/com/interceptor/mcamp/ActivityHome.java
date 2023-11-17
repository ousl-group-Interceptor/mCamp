package com.interceptor.mcamp;

import static com.interceptor.mcamp.R.id.nav_view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ActivityHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private SharedVariable sharedVariable;
    private View header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(nav_view);

        sharedVariable = new SharedVariable(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar
                ,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_view);

        NavigationView navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        loadPersonalDetails();



    }

    private void loadPersonalDetails() {
        loadPersonalDetailsImage();
        loadPersonalDetailsName();
    }

    @SuppressLint("SetTextI18n")
    private void loadPersonalDetailsName() {
        TextView displayName = header.findViewById(R.id.user_name);
        if (Common.userName == null){
            Common.userName = sharedVariable.getName();
            assert sharedVariable.getName() != null;
            displayName.setText(sharedVariable.getName());
        }else {
            displayName.setText(Common.userName);
        }
    }

    private void loadPersonalDetailsImage() {
        if(!sharedVariable.getUserImageUri().equals("unknown")){
            ImageView profileImage = header.findViewById(R.id.profile_picture);
            if (Common.userImageBitmap == null){
                Common.userImageBitmap = Common.getImageBitmap(this, sharedVariable.getUserImageUri());
                profileImage.setImageBitmap(Common.userImageBitmap);
            }else
                profileImage.setImageBitmap(Common.userImageBitmap);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(this, ActivityHome.class));
        }
        if (item.getItemId() == R.id.nav_faq) {

        }
        if (item.getItemId() == R.id.nav_setting) {

        }
        if (item.getItemId() == R.id.nav_feedback) {

        }
        if (item.getItemId() == R.id.nav_logout) {
            logOut();
        }
        if (item.getItemId() == R.id.nav_share) {

        }
        if(!sharedVariable.getUserID().equals("unknown")) {
            if (item.getItemId() == R.id.nav_notification) {
                startActivity(new Intent(this, ActivityNotification.class));
            }
            if (item.getItemId() == R.id.nav_profile) {

            }
            if (item.getItemId() == R.id.nav_rate) {

            }
            if (item.getItemId() == R.id.nav_add_location) {
                startActivity(new Intent(this, ActivityAddLocation.class));
            }
        }else {
            if (item.getItemId() == R.id.nav_rate) {

            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        //if(sharedVariable.getFacebook())
            //facebook logout
        if(sharedVariable.getGoogle())
            FirebaseAuth.getInstance().signOut();
        sharedVariable.setWhileLogin("unknown","unknown","unknown",null, false, false);
        startActivity(new Intent(this, ActivitySplash.class));
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

    public void openTravelPlaces(View view) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void openHandyCrafts(View view) {
    }

    public void openTransportation(View view) {
    }

    public void openAccommodation(View view) {
    }
}