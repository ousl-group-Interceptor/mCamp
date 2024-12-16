package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActivityNotification extends AppCompatActivity {

    LinearLayout parentContainer;
    LayoutInflater inflater;
    private DatabaseReference Data;
    private String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        parentContainer = findViewById(R.id.notificationView);
        inflater = LayoutInflater.from(this);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        SharedVariable sharedVariable = new SharedVariable(this);
        ID = sharedVariable.getUserID();
        load(new boolean[]{true});
        setDatabase();
    }

    private void setDatabase() {
        Data.child("Users/" + ID + "/Notification/newNotification").setValue("false");
    }

    private void load(boolean[] run) {
        Common.startLoading(this, "Loading...");
        Data.child("Users/" + ID + "/Notification/Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if(dataSnapshot.exists()){
                        List<DataSnapshot> snapshotList = new ArrayList<>();
                        for (DataSnapshot faq : dataSnapshot.getChildren()) {
                            snapshotList.add(faq);
                        }

                        Collections.reverse(snapshotList);
                        clear();
                        for (DataSnapshot reversedNotification : snapshotList) {
                            addNotification(Common.idToDateTime(Objects.requireNonNull(reversedNotification.getKey()))[0],
                                    Common.idToDateTime(reversedNotification.getKey())[1],
                                    Objects.requireNonNull(reversedNotification.child("Title").getValue()).toString(),
                                    Objects.requireNonNull(reversedNotification.child("Description").getValue()).toString());
                        }
                        Common.stopLoading();
                    }else
                        Common.stopLoading();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void addNotification(String date, String time, String title, String description) {
        // Inflate the XML layout to obtain the view object
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.individual_notification, parentContainer, false);

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

    private void clear() {
        // Remove all views from the parent layout
        parentContainer.removeAllViews();
    }
}