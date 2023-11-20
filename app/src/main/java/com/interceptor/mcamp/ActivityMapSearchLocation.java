package com.interceptor.mcamp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityMapSearchLocation extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LinearLayout bottomBar;
    private LatLng pin;
    private DatabaseReference Data;
    private double[] pinPoint;
    private String[] popupValues = new String[]{};
    private String selectedCategory = "Select Category";
    private boolean select = false;
    private List<String> items;
    Spinner category;
    private DataSnapshot locations;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search_location);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setVisibility(View.GONE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            if (!select) {
                select = true;
                pin = latLng;
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                pinPoint = new double[]{latitude, longitude};
                addMarker(latLng);
                bottomBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addMarker(LatLng latLng) {
        // Clear existing markers (optional, depending on your use case)
        mMap.clear();

        // Add a marker at the specified location
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Center")  // You can set a title for the marker
                .snippet("Around locations"));  // You can set additional information (snippet) for the marker

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
        PopupWindow popupWindow;
        // Inflate the popup window layout
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_map_search, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Find the views within the popup window layout
        EditText searchText = popupView.findViewById(R.id.keyWordEditText);
        EditText radiusText = popupView.findViewById(R.id.radiusEditText);
        category = popupView.findViewById(R.id.searchSpinner);
        createSpinner();
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        if(popupValues.length==2){
            searchText.setText(popupValues[0]);
            radiusText.setText(popupValues[1]);
        }
        okButton.setEnabled(true);

        // Set click listener for the OK button
        okButton.setOnClickListener(v -> {
            Common.startLoading(this, "Searching...");
            popupValues = new String[]{String.valueOf(searchText.getText()), String.valueOf(radiusText.getText())};
            getDataSnapshot(new boolean[]{true});
            popupWindow.dismiss();
        });

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        // Create the popup window
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void getDataSnapshot(boolean[] run) {
        Data.child("Locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    locations = dataSnapshot;
                    searchManager();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchManager() {
        mMap.clear();
        addMarker(pin);

        if (popupValues[1].equals("")) {
            popupValues[1] = "5";
        }
        if (selectedCategory.equals("Select Category")) {
            search(popupValues[1], "Place", popupValues[0]);
            search(popupValues[1], "Accommodation", popupValues[0]);
            search(popupValues[1], "Handy Craft", popupValues[0]);
            search(popupValues[1], "Transportation", popupValues[0]);
        } else
            search(popupValues[1], selectedCategory, popupValues[0]);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void reset(View view) {
        mMap.clear();
        bottomBar.setVisibility(View.GONE);
        select = false;
        popupValues = new String[]{};
    }

    // Other methods (onResume, onPause, onDestroy, etc.) can be implemented as needed

    private void createSpinner() {
        // Create a list of items for the Spinner
        items = new ArrayList<>();
        items.add("Select Category");
        items.add("Place");
        items.add("Accommodation");
        items.add("Handy Craft");
        items.add("Transportation");

        // Create an ArrayAdapter using the string array and a default spinner layout
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);

        // Create an ArrayAdapter using the custom layout for the spinner items and the List<String>
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_layout,
                items // Your List<String>
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        category.setAdapter(adapter);

        // Set a listener to handle item selections
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item
                selectedCategory = items.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void search(String distance, String category, String value) {
        Common.stopLoading();
        for (DataSnapshot snapshot : locations.child(category).getChildren()) {
            if (value.equals("")) {
                double lat = Double.parseDouble(String.valueOf(snapshot.child("Latitude").getValue()));
                double lon = Double.parseDouble(String.valueOf(snapshot.child("Longitude").getValue()));
                double[] point = {lat, lon};
                if (Common.getDistance(pinPoint, point) <= Double.parseDouble(distance)) {
                    String id = snapshot.getKey();
                    addCustomeMarker(lat, lon, category, id);
                }
            } else {
                for (DataSnapshot keySnapshot : snapshot.child("KeyWords").getChildren()) {
                    if (String.valueOf(keySnapshot.getValue()).compareToIgnoreCase(value) < 3
                            && String.valueOf(keySnapshot.getValue()).compareToIgnoreCase(value) > -3) {
                        double lat = Double.parseDouble(String.valueOf(snapshot.child("Latitude").getValue()));
                        double lon = Double.parseDouble(String.valueOf(snapshot.child("Longitude").getValue()));
                        double[] point = {lat, lon};
                        if (Common.getDistance(pinPoint, point) <= Double.parseDouble(distance)) {
                            String id = snapshot.getKey();
                            addCustomeMarker(lat, lon, category, id);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void addCustomeMarker(double lat, double lon, String category, String id) {
        LatLng latLng = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Center")  // You can set a title for the marker
                .snippet("Around locations"));  // You can set additional information (snippet) for the marker

        // Optionally, move the camera to the marker's position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
}