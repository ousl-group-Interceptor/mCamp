package com.interceptor.mcamp;
//Done
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ActivityResetPassword extends AppCompatActivity {

    TextInputEditText email;
    private DatabaseReference Data;
    EmailManager emailManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = findViewById(R.id.inEmail);
        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
    }

    public void sendOTP(View view) {
        Common.startLoading(this, "Loading");
        checkFiled();
    }

    private void checkFiled(){
        if(String.valueOf(email.getText()).equals("")) {
            Common.stopLoading();
            Common.showMessage(this, "Warning!", "Please enter your email.");
        }
        else {
            if (new EmailValidator().isValidEmail(Objects.requireNonNull(email.getText()).toString())) {
                checkAccount(new boolean[]{true});
            } else {
                Common.stopLoading();
                Common.showMessage(this, "Invalid Inputs!", "Please check email you entered.");
            }
        }
    }

    private void checkAccount(boolean[] run) {
        String ID;
        ID = Common.emailToID(Objects.requireNonNull(email.getText()).toString());

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        Data.child("Users/" + ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    if (dataSnapshot.exists()) {
                        getName(new boolean[]{true});
                    } else
                        accountNotExit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void accountNotExit() {
        Common.stopLoading();
        Common.showMessage(this, "No Account found", "Please check your email.");
    }

    private void getName(boolean[] run) {
        String ID;
        ID = Common.emailToID(Objects.requireNonNull(email.getText()).toString());

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        Data.child("Users/" + ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (run[0]) {
                    run[0] = false;
                    String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    sendOTP(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    private void sendOTP(String name){
        Common.stopLoading();
        int OTP = Integer.parseInt(Common.generateOTP());
        emailManager = new EmailManager(this, Objects.requireNonNull(email.getText()).toString());
        if(emailManager.sendMail(name, OTP)){
            getOTP(OTP);
        }
    }

    private void getOTP(int OTP) {
        Context context = this;
        PopupWindow popupWindow;
        // Inflate the popup window layout
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_opt, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Find the views within the popup window layout
        EditText OTPEditText = popupView.findViewById(R.id.valueEditText);
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        okButton.setEnabled(false);
        OTPEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                okButton.setEnabled(OTPEditText.getText().toString().length() == 6);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set click listener for the OK button
        okButton.setOnClickListener(v -> {
            String stringOTP = OTPEditText.getText().toString();
            // Handle the nickname input as needed
            if (stringOTP.equals(String.valueOf(OTP))) {
                Common.stopLoading();
                Common.userID = Common.emailToID(Objects.requireNonNull(email.getText()).toString());
                Common.email = String.valueOf(email.getText());
                startActivity(new Intent(this, ActivityCreateNewPassword.class));
            }else {
                Common.stopLoading();
                Common.showMessage(context, "Error!", "Invalid OTP");
            }
        });

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        // Create the popup window
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

}