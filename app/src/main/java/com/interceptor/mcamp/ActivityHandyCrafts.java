package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ActivityHandyCrafts extends AppCompatActivity {

    private LinearLayout parentContainer;
    private LayoutInflater inflater;
    private DatabaseReference Data;
    private StorageReference storageRef;
    private ArrayList<DataSnapshot> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handy_crafts);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        parentContainer = findViewById(R.id.locations);
        inflater = LayoutInflater.from(this);

        manageLocation(new boolean[]{true});

    }

    private void manageLocation(boolean[] run) {
        Common.startLoading(this, "Loading...");
        clear();
        data = new ArrayList<>();

        Data.child("Locations/Handy Craft").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        data.add(snapshot);
                    }
                    Common.stopLoading();
                    for (int i = data.size() - 1; i >= 0; i--) {
                        DataSnapshot snapshot = data.get(i);
                        locationAdder(snapshot.getKey(),
                                String.valueOf(snapshot.child("Date").getValue()),
                                String.valueOf(snapshot.child("time").getValue()),
                                String.valueOf(snapshot.child("name").getValue()),
                                String.valueOf(snapshot.child("images/1").getValue()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void search(String value, boolean[] run) {
        clear();
        data = new ArrayList<>();

        Data.child("Locations/Handy Craft").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(value.equals(""))
                            data.add(snapshot);
                        else {
                            for (DataSnapshot keySnapshot : snapshot.child("KeyWords").getChildren()){
                                if(String.valueOf(keySnapshot.getValue()).compareToIgnoreCase(value)<3
                                        && String.valueOf(keySnapshot.getValue()).compareToIgnoreCase(value)>-3){
                                    data.add(snapshot);
                                    break;
                                }
                            }
                        }
                    }
                    Common.stopLoading();
                    if (data.size() == 0)
                        Toast.makeText(ActivityHandyCrafts.this, "Nothing was found", Toast.LENGTH_LONG).show();
                    for (int i = data.size() - 1; i >= 0; i--) {
                        DataSnapshot snapshot = data.get(i);
                        locationAdder(snapshot.getKey(),
                                String.valueOf(snapshot.child("Date").getValue()),
                                String.valueOf(snapshot.child("time").getValue()),
                                String.valueOf(snapshot.child("name").getValue()),
                                String.valueOf(snapshot.child("images/1").getValue()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void locationAdder(String id, String date, String time, String title, String link) {
        // Inflate the XML layout to obtain the view object
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.single_location, parentContainer, false);

        // Customize the view or set data to it
        TextView viewData = xmlView.findViewById(R.id.location_date);
        TextView viewTime = xmlView.findViewById(R.id.location_time);
        TextView viewTitle = xmlView.findViewById(R.id.location_title);
        ImageView viewImage = xmlView.findViewById(R.id.location_image);

        viewData.setText(date);
        viewTime.setText(time);
        viewTitle.setText(title);

        storageRef.child(link).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            // Decode the byte array to a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // Display the image in an ImageView
            Common.userImageBitmap = bitmap;
            viewImage.setImageBitmap(bitmap);
        });

        viewImage.setOnClickListener(v -> {
            Common.locationID = id;
            startActivity(new Intent(this, DetailsLocation.class));
        });

        // Add the view to the parent container
        parentContainer.addView(xmlView);
    }

    private void clear() {
        parentContainer.removeAllViews();
    }

    public void openHome(View view) {
        startActivity(new Intent(this, ActivityHome.class));
    }

    public void openProfile(View view) {
        startActivity(new Intent(this, ActivityProfile.class));
    }

    public void search(View view) {
        PopupWindow popupWindow;
        // Inflate the popup window layout
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_search, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Find the views within the popup window layout
        EditText searchText = popupView.findViewById(R.id.valueEditText);
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        okButton.setEnabled(true);

        // Set click listener for the OK button
        okButton.setOnClickListener(v -> {
            Common.startLoading(this, "Searching...");
            popupWindow.dismiss();
            search(String.valueOf(searchText.getText()), new boolean[]{true});
        });

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        // Create the popup window
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
}