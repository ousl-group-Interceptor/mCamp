package com.interceptor.mcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ActivityNotification extends AppCompatActivity {

    LinearLayout parentContainer;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        parentContainer = findViewById(R.id.notificationView);
        inflater = LayoutInflater.from(this);

        load();
    }

    private void load() {
        clearViewChild();
        for (int i = 1; i <= 10; i++) {
            addNotification("2023.11.11", "13.15", String.valueOf(i), "ABC");
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void addNotification(String date, String time, String title, String description) {
        // Inflate the XML layout to obtain the view object
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.one_notification, parentContainer, false);

        // Customize the view or set data to it
        TextView textDate = xmlView.findViewById(R.id.notification_date);
        TextView textTime = xmlView.findViewById(R.id.notification_time);
        TextView textTitle = xmlView.findViewById(R.id.notification_title);
        TextView textDescription = xmlView.findViewById(R.id.notification_description);

        textDate.setText(date);
        textTime.setText(time);
        textTitle.setText(title);
        textDescription.setText(description);

//        xmlView.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            // Check if the YouTube app is installed
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                // Open the YouTube link in the app
//                startActivity(intent);
//            } else {
//                // If the YouTube app is not installed, open the link in a web browser
//                intent.setPackage(null);
//                startActivity(intent);
//            }
//        });

        // Add the view to the parent container
        parentContainer.addView(xmlView);
    }

//    private void load(){
//        Data.child("video").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                parentContainer.removeAllViews();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    String url = Objects.requireNonNull(snapshot.child("url").getValue()).toString();
//                    String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
//                    videoAdd(name,url);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    @SuppressLint("QueryPermissionsNeeded")
//    private void videoAdd(String name, String url){
//        // Inflate the XML layout to obtain the view object
//        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.one_video, parentContainer, false);
//
//        // Customize the view or set data to it
//        TextView videoName=xmlView.findViewById(R.id.video_name);
//        String youtubeLink = url;
//
//        videoName.setText(name);
//
//        xmlView.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            // Check if the YouTube app is installed
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                // Open the YouTube link in the app
//                startActivity(intent);
//            } else {
//                // If the YouTube app is not installed, open the link in a web browser
//                intent.setPackage(null);
//                startActivity(intent);
//            }
//        });
//
//        // Add the view to the parent container
//        parentContainer.addView(xmlView);
//    }

    private void clearViewChild() {
        // Remove all views from the parent layout
        parentContainer.removeAllViews();
    }
}