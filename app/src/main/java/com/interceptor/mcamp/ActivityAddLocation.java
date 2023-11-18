package com.interceptor.mcamp;
//Done

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ActivityAddLocation extends AppCompatActivity {

    private ImageView locationImage;
    private TextInputEditText locationName;
    private EditText locationDetails, keyWords;
    private Spinner category;
    private TextView showLocation;
    private String selectedCategory, stringLocationName, stringLocationDetails, ID, newLocationID;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Uri> imageUris = new ArrayList<>();
    private final List<String> imageDownloadUris = new ArrayList<>();
    private double[] locationCoordinates, currentGPS;
    private String[] keyWordsList;
    private DatabaseReference Data;
    private List<String> items;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final double maxRange = 5.0;
    private static String locationLinkUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        locationImage = findViewById(R.id.location_image);
        locationName = findViewById(R.id.inLocationName);
        locationDetails = findViewById(R.id.location_details);
        keyWords = findViewById(R.id.keyWords);
        category = findViewById(R.id.spinner);
        showLocation = findViewById(R.id.show_latitut_lontitut);

        if (Common.addLocation) {
            imageUris = Common.imageUris;
            if (imageUris != null)
                locationImage.setImageURI(imageUris.get(0));
            if (Common.locationName != null)
                locationName.setText(Common.locationName);
            if (Common.locationDetails != null)
                locationDetails.setText(Common.locationDetails);
            if (Common.keyWords != null)
                keyWords.setText(Common.keyWords);
            if (Common.position != 0)
                category.setSelection(Common.position);
            if (Common.addingLocation != null) {
                locationCoordinates = Common.addingLocation;
                String str = locationCoordinates[0] + ", " + locationCoordinates[1];
                showLocation.setText(str);
            }
            Common.resetAddLocationBackup();
        }

        Common.addingLocation = null;

        createSpinner();

        GPS();
    }

    private boolean GPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Common.stopLoading();
            // Request the permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                currentGPS = new double[]{latitude, longitude};
            };


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Request location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
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
                // Permission denied, handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
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
                Common.position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Common.startLoading(this, "Loading");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // User selected multiple images
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    imageUris.add(uri);
                }
            } else if (data.getData() != null) {
                // User selected a single image
                Uri uri = data.getData();
                imageUris.add(uri);
            }
            if (!imageUris.isEmpty()) { // Replace with your ImageView's ID
                locationImage.setImageURI(imageUris.get(0));
            }
            Common.stopLoading();
        } else
            Common.stopLoading();
    }

    public void addLocation(View view) {
        if (GPS()) {
            Common.startLoading(this, "Loading...");
            checkFiled();
        }
    }

    public void cancel(View view) {
        startActivity(new Intent(this, ActivityHome.class));
    }

    private void checkFiled() {
        if (imageUris.isEmpty()) {
            Common.stopLoading();
            Common.showMessage(this, "No images!!!", "Please select images.");
        } else if (selectedCategory.equals("Select Category")) {
            Common.stopLoading();
            Common.showMessage(this, "No category!!!", "Please select category.");
        } else if (String.valueOf(locationName.getText()).equals("")) {
            Common.stopLoading();
            Common.showMessage(this, "No name!!!", "Please enter location name.");
        } else if (String.valueOf(showLocation.getText()).equals("")) {
            Common.stopLoading();
            Common.showMessage(this, "No location!!!", "Please select location.");
        } else if (String.valueOf(locationDetails.getText()).equals("")) {
            Common.stopLoading();
            Common.showMessage(this, "No location details!!!", "Please enter description about this location.");
        } else if (String.valueOf(keyWords.getText()).equals("")) {
            Common.stopLoading();
            Common.showMessage(this, "No key word!!!", "Please enter some key words for search this location.");
        } else {
            stringLocationName = String.valueOf(locationName.getText());
            runNext();
        }
    }

    public void runNext() {
        stringLocationDetails = String.valueOf(locationDetails.getText());
        String stringKeyWords = String.valueOf(keyWords.getText());
        keyWordsList = stringKeyWords.split(", ");
        if (locationCoordinates.length == 2) {
            if (checkRange())
                createLocation();
        } else {
            Common.stopLoading();
            Common.showMessage(this, "Location Not found", "Location not found please select.");
        }
    }

    private boolean checkRange() {
        double range = Common.getDistance(currentGPS, locationCoordinates);
        if (range <= maxRange)
            return true;
        else {
            Common.stopLoading();
            String msg = "You in out of range to location.\nMaximum distance between you and" +
                    " location is " + maxRange + "km.";
            Common.showMessage(this, "Out of range.", msg);
            return false;
        }
    }

    private void createLocation() {
        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);

        ID = new SharedVariable(this).getUserID();
        createLocationID(new boolean[]{true});
    }

    private void createLocationID(boolean[] run) {
        Data.child("Locations/" + selectedCategory)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (run[0]) {
                            run[0] = false;
                            int i = 0;
                            do {
                                newLocationID = Common.getCDateTimeString() + String.format("%04d", i);
                                i++;
                            } while (dataSnapshot.child(newLocationID).exists());
                            uploadImage();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                    }
                });
    }

    private void uploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        int imgNumber = 0;
        for (Uri image : imageUris) {
            imgNumber++;
            String link = "Location/" + selectedCategory + "/" + newLocationID + "/" + imgNumber + ".jpg";
            StorageReference imageRef = storageRef.child(link);
            imageDownloadUris.add(link);
            // Upload the image to Firebase Storage
            imageRef.putFile(image)
                    .addOnFailureListener(e -> {
                        Common.stopLoading();
                        Toast.makeText(ActivityAddLocation.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    });

            if (imgNumber == imageUris.size()) {
                uploadData();
            }
        }
    }

    private void uploadData() {
        Data.child("Users/" + ID + "/Locations/" + newLocationID).setValue(selectedCategory);
        Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("name").setValue(stringLocationName);
        Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("time").setValue(Common.getCTime());
        Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("Date").setValue(Common.getCDate());
        Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("Latitude").setValue(locationCoordinates[0]);
        Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("Longitude").setValue(locationCoordinates[1]);
        int i = 0;
        for (String img : imageDownloadUris) {
            i++;
            Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("images/" + i).setValue(img);
        }
        Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("Description").setValue(stringLocationDetails);
        for (String key : keyWordsList) {
            Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("KeyWords").push().setValue(key);
        }
        Data.child("Locations/" + selectedCategory + "/" + newLocationID).child("Category").setValue(selectedCategory)
                .addOnSuccessListener(unused -> {
                    Common.stopLoading();

                    new AlertDialog.Builder(this)
                            .setTitle("Successful")
                            .setMessage("Location adding completed.")
                            .setPositiveButton("Open Home", (dialog, which) -> startActivity(new Intent(this, ActivityHome.class)))
                            .show();
                });
    }

    public void selectLocation(View view) {
        Common.imageUris = imageUris;
        Common.locationName = String.valueOf(locationName.getText());
        Common.locationDetails = String.valueOf(locationDetails.getText());
        Common.keyWords = String.valueOf(keyWords.getText());
        Common.addLocation = true;
        startActivity(new Intent(this, ActivityMapAddLocation.class));
    }

    private static class FollowRedirectTask extends AsyncTask<String, Void, String> {
        private final Callback callback;

        interface Callback {
            void onResult(String result);
        }

        public FollowRedirectTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String shortLink = params[0];
                return followRedirect(shortLink);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    static String followRedirect(String shortLink) throws IOException {
        URL url = new URL(shortLink);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set instance follow redirects to true
        connection.setInstanceFollowRedirects(true);

        // Optional: Set other connection properties if needed
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        // Get the response code
        int responseCode = connection.getResponseCode();

        // Check if it's a redirection (HTTP 3xx status)
        if (responseCode >= 300 && responseCode < 400) {
            // Get the new location from the 'Location' header
            String newLocation = connection.getHeaderField("Location");

            // Follow the redirect recursively (if needed)
            return followRedirect(newLocation);
        } else {
            // Not a redirect or final destination reached
            // Extract other information as needed
            return connection.getURL().toString();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ActivityHome.class));
        finish();
    }
}