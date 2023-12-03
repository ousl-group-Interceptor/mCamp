package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActivityMyReviews extends AppCompatActivity {

    private LinearLayout parentContainer;
    private LayoutInflater layoutInflater;
    private StorageReference storageRef;
    private SharedVariable sharedVariable;
    private DatabaseReference Data;
    private List<DataSnapshot> myLocations;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        sharedVariable = new SharedVariable(this);

        parentContainer = findViewById(R.id.locations);
        layoutInflater = LayoutInflater.from(this);

        checkData(new boolean[]{true});
    }

    private void checkData(boolean[] run) {
        Common.startLoading(this, "Loading...");
        String path = "Users/" + sharedVariable.getUserID() + "/Reviews";
        Data.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    boolean[] secondRun = {true};
                    if (dataSnapshot.exists()) {
                        Data.child("Locations").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (secondRun[0]) {
                                    secondRun[0] = false;

                                    myLocations = new ArrayList<>();
                                    for (DataSnapshot location : dataSnapshot.getChildren()) {
                                        myLocations.add(
                                                snapshot.child(Objects.requireNonNull(location.getValue()).toString())
                                                        .child(Objects.requireNonNull(location.getKey())));
                                    }
                                    Collections.reverse(myLocations);
                                    clear();
                                    Common.stopLoading();
                                    for (DataSnapshot reversedMyLocation : myLocations) {
                                        locationAdder(reversedMyLocation);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                            }
                        });
                    } else
                        Common.stopLoading();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void locationAdder(DataSnapshot reversedMyLocation) {
        LinearLayout xmlView = (LinearLayout) layoutInflater.inflate(R.layout.individual_my_location, parentContainer, false);

        LinearLayout location =  xmlView.findViewById(R.id.my_location);
        TextView name = xmlView.findViewById(R.id.location_name);
        ImageView image = xmlView.findViewById(R.id.location_image);

        name.setText(String.valueOf(reversedMyLocation.child("name").getValue()));

        String imageUrl = Objects.requireNonNull(reversedMyLocation.child("images/1").getValue()).toString();
        storageRef.child(imageUrl).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image.setImageBitmap(bitmap);
        });

        location.setOnClickListener(v -> {
            Common.currentLocationID = String.valueOf(reversedMyLocation.getKey());
            Common.currentLocationCategory = String.valueOf(reversedMyLocation.child("Category").getValue());
            startActivity(new Intent(this, ActivityLocationDetails.class));
        });

        parentContainer.addView(xmlView);
    }

    private void clear() {
        parentContainer.removeAllViews();
    }
}