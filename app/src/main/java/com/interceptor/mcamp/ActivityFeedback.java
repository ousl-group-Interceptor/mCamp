package com.interceptor.mcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityFeedback extends AppCompatActivity {

    private ImageView emoji1, emoji2, emoji3, emoji4;
    private int happyValue = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        emoji1 = findViewById(R.id.emoji_1);
        emoji2 = findViewById(R.id.emoji_2);
        emoji3 = findViewById(R.id.emoji_3);
        emoji4 = findViewById(R.id.emoji_4);
        happyValue = 4;
        reSize1(100,100);
        reSize2(100,100);
        reSize3(100,100);
        reSize4(150,150);
    }

    public void openHome(View view) {
        startActivity(new Intent(this, ActivityHome.class));
    }

    public void openProfile(View view) {
        startActivity(new Intent(this, ActivityProfile.class));
    }

    public void submit(View view) {
        Common.startLoading(this, "Uploading...");
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        EditText feedback = findViewById(R.id.edit_feedback);
        int i = Integer.parseInt(Common.generateOTP());
        @SuppressLint("DefaultLocale")
        String path = "Feedback/" + Common.getCDateTimeString() + String.format("%04d", i);

        Data.child(path).child("User").setValue(new SharedVariable(this).getUserID());
        Data.child(path).child("HappyLevel").setValue(happyValue);
        Data.child(path).child("Feedback").setValue(feedback.getText().toString()).addOnSuccessListener(unused -> Common.stopLoading());
    }
    public void emoji1(View view) {
        happyValue = 1;
        reSize1(150,150);
        reSize2(100,100);
        reSize3(100,100);
        reSize4(100,100);

    }

    public void emoji2(View view) {
        happyValue = 2;
        reSize1(100,100);
        reSize2(150,150);
        reSize3(100,100);
        reSize4(100,100);
    }

    public void emoji3(View view) {
        happyValue = 3;
        reSize1(100,100);
        reSize2(100,100);
        reSize3(150,150);
        reSize4(100,100);
    }

    public void emoji4(View view) {
        happyValue = 4;
        reSize1(100,100);
        reSize2(100,100);
        reSize3(100,100);
        reSize4(150,150);
    }

    private void reSize1(int x, int y){
        ViewGroup.LayoutParams layoutParams = emoji1.getLayoutParams();
        layoutParams.width = x;
        layoutParams.height = y;
        emoji1.setLayoutParams(layoutParams);
    }

    private void reSize2(int x, int y){
        ViewGroup.LayoutParams layoutParams = emoji2.getLayoutParams();
        layoutParams.width = x;
        layoutParams.height = y;
        emoji2.setLayoutParams(layoutParams);
    }

    private void reSize3(int x, int y){
        ViewGroup.LayoutParams layoutParams = emoji3.getLayoutParams();
        layoutParams.width = x;
        layoutParams.height = y;
        emoji3.setLayoutParams(layoutParams);
    }

    private void reSize4(int x, int y){
        ViewGroup.LayoutParams layoutParams = emoji4.getLayoutParams();
        layoutParams.width = x;
        layoutParams.height = y;
        emoji4.setLayoutParams(layoutParams);
    }
}