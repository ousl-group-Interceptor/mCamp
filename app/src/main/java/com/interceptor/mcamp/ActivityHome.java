package com.interceptor.mcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.nav_home:
//                break;
//            case  R.id.nav_notification:
////                Intent intent1 = new Intent(ActivityHome.this, notification.class);
////                startActivity(intent1);
//                break;
//
//            case  R.id.nav_faq:
////                Intent intent2 = new Intent(ActivityHome.this, FAQ.class);
////                startActivity(intent2);
//                break;
//
//
//            case  R.id.nav_setting:
////                Intent intent3 = new Intent(ActivityHome.this, setting.class);
////                startActivity(intent3);
//                break;
//
//            case  R.id.nav_feedback:
////                Intent intent4 = new Intent(ActivityHome.this, AddFeedback.class);
////                startActivity(intent4);
//                break;
//
//            case R.id.nav_reviews:
//                Intent intent5 = new Intent(ActivityHome.this,reviews.class);
//                startActivity(intent5);
//                break;

//            case R.id.nav_add_account:
//                Intent intent6 = new Intent(ActivityHome.this,add_account.class);
//                startActivity(intent6);
                ;

//            case R.id.nav_profile:
//                Intent intent7 = new Intent(ActivityHome.this, Profile.class);
//                startActivity(intent7);
//                break;

//            case R.id.nav_logout:
//                Intent intent8 = new Intent(ActivityHome.this,RegistrationPage.class);
//                startActivity(intent8);
//                break;

//            case R.id.nav_share:
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_rate:
//                Toast.makeText(this, "Rate", Toast.LENGTH_SHORT).show();
//                break;
//        }
//        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}