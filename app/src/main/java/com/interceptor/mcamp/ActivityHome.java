package com.interceptor.mcamp;

import static com.interceptor.mcamp.R.id.nav_view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

public class ActivityHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private SharedVariable sharedVariable;
    private View header;
    private DatabaseReference Data;
    private ImageView profileImage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(nav_view);

        sharedVariable = new SharedVariable(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_view);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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
        if (Common.userName == null) {
            Common.userName = sharedVariable.getName();
            Log.d("outputNameShare", sharedVariable.getName());
            Log.d("outputNameCommon", Common.userName);
            displayName.setText(sharedVariable.getName());
        } else {
            displayName.setText(Common.userName);
        }
    }

    private void loadPersonalDetailsImage() {
        if (!sharedVariable.getUserImageUri().equals("unknown")) {
            profileImage = header.findViewById(R.id.profile_picture);
            if (Common.userImageBitmap == null) {
                if (sharedVariable.getGoogle() || sharedVariable.getFacebook())
                    getImageBitmap(sharedVariable.getUserImageUri());
                else
                    loadImage(new boolean[]{true});
            } else {
                profileImage.setImageBitmap(Common.userImageBitmap);
            }
        }
    }

    private void loadImage(boolean[] run) {
        String ID = sharedVariable.getUserID();

        Data.child("Users/" + ID + "/image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.exists()) {
                        storageRef.child(String.valueOf(dataSnapshot.getValue())).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                            // Decode the byte array to a Bitmap
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            // Display the image in an ImageView
                            Common.userImageBitmap = bitmap;
                            profileImage.setImageBitmap(bitmap);
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    public void getImageBitmap(String imageUrl) {
        //String imageUrl = "https://lh3.googleusercontent.com/a/ACg8ocLlv81FT4BvNtgUmlfUQ6qCfAFllgyJ96TnGn6BOV3HPA=s96-c";

        // Use Picasso to load the image and convert it to a Bitmap
        Picasso.get().load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // The 'bitmap' variable now contains the image in Bitmap format
                // You can use it as needed, for example, set it to an ImageView
                Common.userImageBitmap = bitmap;
                profileImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Handle the case where the bitmap couldn't be loaded
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Do nothing or handle as needed
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
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
        if (item.getItemId() == R.id.nav_rate) {

        }
        if (!sharedVariable.getUserID().equals("unknown")) {
            if (item.getItemId() == R.id.nav_notification) {
                startActivity(new Intent(this, ActivityNotification.class));
            }
            if (item.getItemId() == R.id.nav_profile) {

            }
            if (item.getItemId() == R.id.nav_add_location) {
                startActivity(new Intent(this, ActivityAddLocation.class));
            }
        } else {
            if (item.getItemId() == R.id.nav_notification) {
                Toast.makeText(this, "Please Sign In To Use This Feature", Toast.LENGTH_SHORT).show();
            }
            if (item.getItemId() == R.id.nav_profile) {
                Toast.makeText(this, "Please Sign In To Use This Feature", Toast.LENGTH_SHORT).show();
            }
            if (item.getItemId() == R.id.nav_rate) {
                Toast.makeText(this, "Please Sign In To Use This Feature", Toast.LENGTH_SHORT).show();
            }
            if (item.getItemId() == R.id.nav_add_location) {
                Toast.makeText(this, "Please Sign In To Use This Feature", Toast.LENGTH_SHORT).show();
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        //if(sharedVariable.getFacebook())
        //facebook logout
        if (sharedVariable.getGoogle())
            FirebaseAuth.getInstance().signOut();
        sharedVariable.setWhileLogin("unknown", "unknown", "unknown", null, false, false);
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