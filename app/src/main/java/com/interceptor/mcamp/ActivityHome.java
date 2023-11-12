package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ActivityHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    TextView textView;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.nav_home:
//                break;
//            case  R.id.nav_notification:
//                Log.d("Navigate", "Notification");
//                break;
//
//            case  R.id.nav_faq:
//                Log.d("Navigate", "FAQ");
//                break;
//
//            case  R.id.nav_setting:
//                Log.d("Navigate", "Setting");
//                break;
//
//            case  R.id.nav_feedback:
//                Log.d("Navigate", "feedback");
//                break;
//
//            case R.id.nav_reviews:
//                Log.d("Navigate", "Reviews");
//                break;
//
//            case R.id.nav_profile:
//                Log.d("Navigate", "Profile");
//                break;
//
//            case R.id.nav_logout:
//                Log.d("Navigate", "Logout");
//                break;
//
//            case R.id.nav_share:
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_rate:
//                Toast.makeText(this, "Rate", Toast.LENGTH_SHORT).show();
//                break;
//        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}