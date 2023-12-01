package com.interceptor.mcamp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ActivityGiveRate extends AppCompatActivity {

    private RatingBar rating_bar;
    private EditText description;
    SharedVariable sharedVariable;
    private DatabaseReference Data;
    private double[] latLon = new double[]{};
    private static final double maxRange = 1.0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_rate);

        rating_bar = findViewById(R.id.rating_bar);
        rating_bar.setRating(2.5f);
        description = findViewById(R.id.review_description);
        sharedVariable = new SharedVariable(this);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);

        GPS();
    }

    private boolean GPS() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Common.stopLoading();
            // Request the permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        } else {
            // Permission already granted
            return requestLocationUpdates();
        }
    }

    private boolean requestLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if GPS is enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Define a LocationListener
            LocationListener locationListener = location -> {
                // Handle the new location
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                latLon = new double[]{latitude, longitude};
            };


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Request location updates
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user_icon grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return true;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0, // minimum time interval between updates (in milliseconds)
                        0, // minimum distance between updates (in meters)
                        locationListener);
            }
            return true;
        } else {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Handle the permission request result
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start requesting location updates
                requestLocationUpdates();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user_icon)
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cancel(View view) {
        startActivity(new Intent(this, ActivityLocationDetails.class));
        finish();
    }

    public void submit(View view) {
        Common.startLoading(this, "Loading...");
        locationCoordinates(new boolean[]{true});
    }

    private void GPSCheck(double[] locationCoordinates) {
        int delayMillis = 2000;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (latLon.length > 1) {
                double range = Common.getDistance(latLon, locationCoordinates);
                if (range <= maxRange)
                    submit();
                else {
                    Common.stopLoading();
                    String msg = "You in out of range to location.\nMaximum distance between you and" +
                            " location is " + maxRange + "km.";
                    Common.showMessage(ActivityGiveRate.this, "Out of range.", msg);
                }
            } else {
                GPSCheck(locationCoordinates);
            }

        }, delayMillis);
    }

    private void locationCoordinates(boolean[] run) {
        Data.child("Locations/" + Common.currentLocationCategory + "/" + Common.currentLocationID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    double[] locationCoordinates = {Double.parseDouble(String.valueOf(dataSnapshot.child("Latitude").getValue())),
                            Double.parseDouble(String.valueOf(dataSnapshot.child("Longitude").getValue()))};
                    GPSCheck(locationCoordinates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void submit() {
        int i = Integer.parseInt(Common.generateOTP());
        @SuppressLint("DefaultLocale") String path = "Locations/" + Common.currentLocationCategory + "/" + Common.currentLocationID + "/Reviews/" + Common.getCDateTimeString() + String.format("%04d", i);
        Data.child(path).child("Date").setValue(Common.getCDate());
        Data.child(path).child("Description").setValue(String.valueOf(description.getText()));
        Data.child(path).child("Rate").setValue(rating_bar.getRating());
        Data.child(path).child("User").setValue(sharedVariable.getUserID()).addOnSuccessListener(unused -> givePoint(new boolean[]{true}));
    }

    private void givePoint(boolean[] run) {
        String path = "Locations/" + Common.currentLocationCategory + "/" + Common.currentLocationID + "/Points";
        Data.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.exists()) {
                        int point = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                        givePoint(new boolean[]{true}, point);
                    } else
                        showAlert();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void givePoint(boolean[] run, int point) {
        String path = "Users/" + sharedVariable.getUserID() + "/Points";
        Data.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.exists()) {
                        int totalPoint = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString()) + point;
                        Data.child(path).setValue(totalPoint);
                        showAlert();
                    } else {
                        Data.child(path).setValue(point);
                        showAlert();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showAlert() {
        Common.stopLoading();
        new AlertDialog.Builder(this)
                .setTitle("Complete")
                .setMessage("Review added successfully.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(this, ActivityLocationDetails.class));
                    finish();
                })
                .show();
    }
}