package com.interceptor.mcamp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityMapAddLocation extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LinearLayout bottomBar;
    private TextView values;
    private double[] location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_add_location);

        bottomBar = findViewById(R.id.bottom_bar);
        values = findViewById(R.id.show_value);
        bottomBar.setVisibility(View.GONE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Start listening for location updates
            startLocationUpdates();
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Set a touch listener on the map
        mMap.setOnMapClickListener(latLng -> {
            // Handle the touch event and get the coordinates
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            location = new double[]{latitude, longitude};

            addMarker(latLng);

            String str = latitude + ", " +longitude;
            bottomBar.setVisibility(View.VISIBLE);
            values.setText(str);
        });
    }

    private void addMarker(LatLng latLng) {
        // Clear existing markers (optional, depending on your use case)
        mMap.clear();

        // Add a marker at the specified location
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker Title")  // You can set a title for the marker
                .snippet("Marker Snippet"));  // You can set additional information (snippet) for the marker

        // Optionally, move the camera to the marker's position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onLocationChanged(Location location) {
        // Zoom to the current GPS location
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    private void startLocationUpdates() {
        // Check if location updates are allowed and request updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start location updates
            startLocationUpdates();
        }
    }

    public void submitValues(View view) {
        Common.addingLocation = location;
        startActivity(new Intent(this, ActivityAddLocation.class));
    }

    // Other methods (onResume, onPause, onDestroy, etc.) can be implemented as needed
}