package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class FragmentOverView extends Fragment {

    private LinearLayout parentContainer;
    private LayoutInflater layoutInflater;
    private StorageReference storageRef;
    private View view;
    private GestureDetector gestureDetector;
    private int imageCount;

    public FragmentOverView() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_over_view, container, false);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        parentContainer = view.findViewById(R.id.location_images);
        layoutInflater = LayoutInflater.from(requireContext());

        loadData(new boolean[]{true});

        return view;
    }

    // Custom GestureListener class to handle double taps
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Handle double tap event
            startActivity(new Intent(requireContext(), ActivityFullscreenImage.class));
            return true;
        }
    }

    private void loadData(boolean[] run) {
        TextView locationName = view.findViewById(R.id.location_name);
        LinearLayout coin = view.findViewById(R.id.coin);
        TextView coinCount = view.findViewById(R.id.coin_count);
        TextView addedDate = view.findViewById(R.id.added_date);
        TextView addedTime = view.findViewById(R.id.added_time);
        TextView description = view.findViewById(R.id.description);
        TextView showInMap = view.findViewById(R.id.show_in_map);

        addedDate.setText(Common.idToDateTime(Common.currentLocationID)[0]);
        addedTime.setText(Common.idToDateTime(Common.currentLocationID)[1]);

        String path = "Locations/" + Common.currentLocationCategory + "/" + Common.currentLocationID;
        Common.currentLocationData.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;

                    locationName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                    description.setText(Objects.requireNonNull(dataSnapshot.child("Description").getValue()).toString());
                    double lat = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("Latitude").getValue()).toString());
                    double lng = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("Longitude").getValue()).toString());
                    Common.currentLocationLatLng = new LatLng(lat, lng);
                    showInMap.setVisibility(View.VISIBLE);

                    if (dataSnapshot.child("Points").exists()) {
                        coin.setVisibility(View.VISIBLE);
                        coinCount.setText(Objects.requireNonNull(dataSnapshot.child("Points").getValue()).toString());
                    }

                    imageCount = 0;
                    for (DataSnapshot ignored : dataSnapshot.child("images").getChildren()) {
                        imageCount++;
                    }
                    Common.currentLocationImageUrls = new String[imageCount];
                    Common.currentLocationImageBitmap = new Bitmap[imageCount];

                    int i = 0;
                    for (DataSnapshot image : dataSnapshot.child("images").getChildren()) {
                        Common.currentLocationImageUrls[i] = Objects.requireNonNull(image.getValue()).toString();
                        i++;
                    }
                    loadImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        showInMap.setOnClickListener(v->startActivity(new Intent(requireContext(), ActivityMapShowLocation.class)));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void loadImage() {
        int[] loop = new int[]{0};
        for (String imageUrl : Common.currentLocationImageUrls) {
            LinearLayout xmlView = (LinearLayout) layoutInflater.inflate(R.layout.single_image, parentContainer, false);
            ImageView locationImage = xmlView.findViewById(R.id.location_single_image);

            gestureDetector = new GestureDetector(getActivity(), new GestureListener());

            // Set the double tap listener for the HorizontalScrollView
            locationImage.setOnTouchListener((v, event) -> {
                // Pass the touch event to the GestureDetector
                gestureDetector.onTouchEvent(event);
                return true;
            });

            storageRef.child(imageUrl).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Common.currentLocationImageBitmap[loop[0]] = bitmap;
                loop[0]++;
                locationImage.setImageBitmap(bitmap);
            });
            parentContainer.addView(xmlView);
        }
    }
}