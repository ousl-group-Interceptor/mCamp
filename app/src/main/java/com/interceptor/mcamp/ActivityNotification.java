package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityNotification extends AppCompatActivity {

    LinearLayout parentContainer;
    LayoutInflater inflater;
    private SharedVariable sharedVariable;
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
        sharedVariable = new SharedVariable(this);
        ID = sharedVariable.getUserID();
        load();
        setDatabase();
    }

    private void setDatabase() {
        Data.child("Users/" + ID + "/Notification/newNotification").setValue("false");
    }

    private void load() {
        clear();
        for (int i = 1; i <= 10; i++) {
            addNotification("2023.11.11", "13.15", String.valueOf(i), "ABC");
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void addNotification(String date, String time, String title, String description) {
        // Inflate the XML layout to obtain the view object
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.single_notification, parentContainer, false);

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